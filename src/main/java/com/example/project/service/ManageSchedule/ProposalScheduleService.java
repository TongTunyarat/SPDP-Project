package com.example.project.service.ManageSchedule;

import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
import com.example.project.DTO.ManageSchedule.ScheduleAssignmentDTO;
import com.example.project.DTO.ManageSchedule.ScheduleSlotDTO;
import com.example.project.entity.*;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProposalScheduleService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private ProjectRepository projectRepository;


    // setting button
    public List<String> getRoom() {
        List<Room> roomList = roomRepository.findAll();
        return roomList.stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toList());
    }

    // prepare data of project in controller
    public List<ProjectWithInstructorsDTO> prepareProject(String program) {

        List<Project> ProjectList = projectRepository.findAll();

        return ProjectList.stream()
                .filter(i -> program.equalsIgnoreCase(i.getProgram()))
                .filter(i -> {
                    List<StudentProject> studentProjects = i.getStudentProjects();
                    if (studentProjects == null || studentProjects.isEmpty()) return false;

                    boolean hasActive = studentProjects.stream()
                            .anyMatch(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()));

                    boolean allExited = studentProjects.stream()
                            .allMatch(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()));

                    return hasActive && !allExited;
                })

                .map(i -> {

                    // get instructor
                    List<String> instructorList = i.getProjectInstructorRoles().stream()
                            .map(projectInstructorRole -> {
                                Instructor instructor = projectInstructorRole.getInstructor();

                                if (instructor != null && instructor.getAccount() != null) {
                                    return  instructor.getAccount().getUsername();}
                                return null;
                            }).filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return new ProjectWithInstructorsDTO(i, instructorList);

                }).collect(Collectors.toList());
    }

    private static final int TIME_SLOT_DURATION = 25;

    public String generateSchedule(String startDate, String endDate, String startTime, String endTime, List<String> roomNumbers, List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {

        // https://www.geeksforgeeks.org/how-to-convert-a-string-to-a-localdate-in-java/
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateConvert = LocalDate.parse(startDate, dateFormatter);
        LocalDate endDateConvert = LocalDate.parse(endDate, dateFormatter);

        // https://stackoverflow.com/questions/30788369/coverting-string-to-localtime-with-without-nanoofseconds
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime startTimeConvert = LocalTime.parse(startTime,timeFormatter);
        LocalTime endTimeConvert = LocalTime.parse(endTime,timeFormatter);

        System.out.println("🔖 Room Numbers:");
        for (String room: roomNumbers) {
            System.out.println(room);
        }

        // create instructor project
//        Map<String, List<String>> instructorProjects = createInstructorProjectMap(projectWithInstructorsDTOList);

        // create time slots
        List<LocalDateTime> allTimeSlots = generateTimeSlots(startDateConvert, endDateConvert, startTimeConvert, endTimeConvert);

        // map room and time slot
        List<ScheduleSlotDTO> availableSlots = generateAvailableSlots(allTimeSlots, roomNumbers);

        // sort project ตาม instructor
        List<ProjectWithInstructorsDTO> sortedProjects = sortProjectsByInstructors(projectWithInstructorsDTOList);



        List<ScheduleAssignmentDTO> scheduledAssignments = new ArrayList<>();
        List<ProjectWithInstructorsDTO> unscheduledProjects = new ArrayList<>();


        if (!scheduleProjects(sortedProjects, availableSlots, scheduledAssignments)) {
            unscheduledProjects.addAll(sortedProjects);
        }


        return "Schedule generation check completed!";

    }

    private Map<String, Map<LocalDate, Set<LocalTime>>> roomSchedule = new HashMap<>();
    private Map<String, Map<LocalDate, Set<LocalTime>>> instructorSchedule = new HashMap<>();


    public boolean scheduleProjects(List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList, List<ScheduleSlotDTO> availableSlots, List<ScheduleAssignmentDTO> scheduledAssignments) {

        if(projectWithInstructorsDTOList.isEmpty()) return true;

        ProjectWithInstructorsDTO project = projectWithInstructorsDTOList.remove(0);

        System.out.println("🗓️ Scheduling project: " + project.getProject().getProjectId());

        // note: availableSlots -> room and time
        for (int i = 0; i < availableSlots.size(); i ++) {

            ScheduleSlotDTO slot = availableSlots.get(i);

            System.out.println("Checking slot: Room " + slot.getRoom() + ", Time " + slot.getStartTime());

            if( isSlotAvailable(slot, project) ) {

                System.out.println("✅ Slot available assign: " + slot.getRoom() + " at " + slot.getStartTime());

                ScheduleAssignmentDTO assignment = new ScheduleAssignmentDTO(

                    project.getProject().getProjectId(), slot.getRoom(),slot.getStartTime(), slot.getStartTime().plusMinutes(TIME_SLOT_DURATION), project.getInstructorUsernames()

                );

                System.out.println("Assigned: " + assignment);
                scheduledAssignments.add(assignment);
                markSlot(slot, project);
                availableSlots.remove(i);

                return scheduleProjects(projectWithInstructorsDTOList, availableSlots, scheduledAssignments);
            } else {
                System.out.println("🛑 Slot not available: " + slot.getRoom() + " at " + slot.getStartTime());
            }
        }

        System.out.println("❌ No available slot for project: " + project.getProject().getProjectId());
        return false;
    }

    public boolean isSlotAvailable(ScheduleSlotDTO slot, ProjectWithInstructorsDTO project) {

        LocalDate date = slot.getStartTime().toLocalDate();
        LocalTime time = slot.getStartTime().toLocalTime();
        String room = slot.getRoom();

        System.out.println("💌 Checking isSlotAvailable for room: " + room + ", date: " + date + ", time: " + time);


        // ดูว่าห้องว่างไหม
        // https://www.w3schools.com/java/tryjava.asp?filename=demo_ref_hashmap_getordefault
        if(roomSchedule.getOrDefault(room, Collections.emptyMap())
                .getOrDefault(date, Collections.emptySet())
                .contains(time)) {
            System.out.println("Room " + room + " is already occupied at this time.");
            return false;
        }

        for (String instructor : project.getInstructorUsernames()) {
            System.out.println("🕵️‍♂️ Checking available instructor: " + instructor);

            Set<LocalTime> times = instructorSchedule.getOrDefault(instructor, Collections.emptyMap())
                    .getOrDefault(date, Collections.emptySet());

            for (LocalTime t : times) {
                if (isTimeOverlap(t, t.plusMinutes(TIME_SLOT_DURATION), time, time.plusMinutes(TIME_SLOT_DURATION))) {
                    System.out.println("Instructor " + instructor + " is already scheduled at this time.");
                    return false;
                }
            }
        }

        System.out.println("Slot is available.");
        return true;
    }

    public void markSlot(ScheduleSlotDTO slot, ProjectWithInstructorsDTO project) {

        LocalDate date = slot.getStartTime().toLocalDate();
        LocalTime time = slot.getStartTime().toLocalTime();
        String room = slot.getRoom();

        System.out.println("🌻 Marking slot: Room " + room + ", Date: " + date + ", Time: " + time);

        roomSchedule.putIfAbsent(room, new HashMap<>());
        roomSchedule.get(room).putIfAbsent(date, new HashSet<>());
        roomSchedule.get(room).get(date).add(time);

        System.out.println("Room schedule updated: " + roomSchedule);

        for (String instructor : project.getInstructorUsernames()) {

            instructorSchedule.putIfAbsent(instructor, new HashMap<>());
            instructorSchedule.get(instructor).putIfAbsent(date, new HashSet<>());
            instructorSchedule.get(instructor).get(date).add(time);

            System.out.println("Instructor schedule updated for " + instructor + ": " + instructorSchedule);

        }
    }

    public boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        System.out.println("⏰ Checking overlap: [" + start1 + " - " + end1 + "] with [" + start2 + " - " + end2 + "]");
        return start1.isBefore(end2) && start2.isBefore(end1);
    }


    public List<LocalDateTime> generateTimeSlots(LocalDate startDate,LocalDate endDate,LocalTime startTime,LocalTime endTime){

        List<LocalDateTime> timeSolts = new ArrayList<>();

        // https://www.geeksforgeeks.org/localdatetime-ofdate-time-method-in-java-with-examples/
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // https://www.geeksforgeeks.org/localdate-isbefore-method-in-java-with-examples/
        while (startDateTime.isBefore(endDateTime)) {


            // https://www.geeksforgeeks.org/dayofweek-getvalue-method-in-java-with-examples/
//            System.out.println("🍭 Check Start Day ");
//            System.out.println("Day: " + startDateTime.getDayOfWeek().name());
//            System.out.println("Start day value: " + startDateTime.getDayOfWeek().getValue());

            // if not weekend
            if (startDateTime.getDayOfWeek().getValue() == 6 || startDateTime.getDayOfWeek().getValue() == 7) {

                System.out.println("Holiday (Weekend) Time Slot: " + startDateTime);

            } else if (startDateTime.getDayOfWeek().getValue() <= 5) {

                timeSolts.add(startDateTime);

                System.out.println("Added Time Slot: " + startDateTime);
            }

            // https://www.geeksforgeeks.org/localtime-plusminutes-method-in-java-with-examples/
            startDateTime = startDateTime.plusMinutes(TIME_SLOT_DURATION);

            if (startDateTime.toLocalTime().isAfter(endTime)) {

                startDateTime = LocalDateTime.of(
                        startDateTime.toLocalDate().plusDays(1),
                        startTime

                );
            }

        }

        return timeSolts;

    }

    // time and room
    public List<ScheduleSlotDTO> generateAvailableSlots(List<LocalDateTime> timeSlots, List<String> roomNumbers) {

        List<ScheduleSlotDTO> slotDTOList = new ArrayList<>();

        for(LocalDateTime timeSlot : timeSlots) {

            for (String roomNumber : roomNumbers) {
                slotDTOList.add(new ScheduleSlotDTO(roomNumber, timeSlot));
            }

        }

//        System.out.println("🕰️Time slots with room");
//        System.out.println(slotDTOList);
//
//        for (ScheduleSlotDTO slotDTO: slotDTOList) {
//            System.out.println(slotDTO);
//        }

        return slotDTOList;
    }

    public List<ProjectWithInstructorsDTO> sortProjectsByInstructors(List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {

        List<ProjectWithInstructorsDTO> sortedProjects = new ArrayList<>(projectWithInstructorsDTOList);

//        System.out.println("🧮 Before sorting:");
//        for (ProjectWithInstructorsDTO project : sortedProjects) {
//            System.out.println("ProjectId: " + project.getProject().getProjectId() + ", Instructor Count: " + project.getInstructorUsernames().size());
//        }


        // Dual-Pivot Quicksort
        sortedProjects.sort(new Comparator<ProjectWithInstructorsDTO>() {
            @Override
            public int compare(ProjectWithInstructorsDTO o1, ProjectWithInstructorsDTO o2) {

//                System.out.println("🙂 Comparing: " + o1.getProject().getProjectId() + " (" + o1.getInstructorUsernames().size() + ") with "
//                        + o2.getProject().getProjectId() + " (" + o2.getInstructorUsernames().size() + ")");

                return Integer.compare(o2.getInstructorUsernames().size(), o1.getInstructorUsernames().size());
            }
        });

//        System.out.println("📌 After sorting:");
//        for (ProjectWithInstructorsDTO project : sortedProjects) {
//            System.out.println("Project: " + project.getProject().getProjectId() + ", Instructor Count: " + project.getInstructorUsernames().size());
//        }

        return sortedProjects;
    }














//    public String generateSchedule(String startDate, String endDate, String startTime, String endTime, List<String> roomNumbers, List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {
//
//        // https://www.geeksforgeeks.org/how-to-convert-a-string-to-a-localdate-in-java/
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate startDateConvert = LocalDate.parse(startDate, dateFormatter);
//        LocalDate endDateConvert = LocalDate.parse(endDate, dateFormatter);
//
//        // https://stackoverflow.com/questions/30788369/coverting-string-to-localtime-with-without-nanoofseconds
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
//        LocalTime startTimeConvert = LocalTime.parse(startTime,timeFormatter);
//        LocalTime endTimeConvert = LocalTime.parse(endTime,timeFormatter);
//
//        // create time slots
//        List<LocalDateTime> allTimeSlots = generateTimeSlots(startDateConvert, endDateConvert, startTimeConvert, endTimeConvert);
//
//        System.out.println("🔖 Room Numbers:");
//        for (String room: roomNumbers) {
//            System.out.println(room);
//        }
//
//        // dto สำหรับเก็บค่าที่เป็นไปได้ในเเต่ละ project
//        List<ScheduleNodeProposalDTO> allNodes = new ArrayList<>();
//        for (int i = 0; i < projectWithInstructorsDTOList.size(); i++ ) {
//
//            ProjectWithInstructorsDTO projectDTO = projectWithInstructorsDTOList.get(i);
////            System.out.println("🌟Query project brfore generate schedule");
////            System.out.println("Project: " + projectDTO.getProject());
////            System.out.println("Instructor of project: " + projectDTO.getInstructorUsernames());
//
//            for (String room: roomNumbers) {
//
//                // จาก generateTimeSlots
//                for (LocalDateTime timeSlot: allTimeSlots) {
//                    allNodes.add(new ScheduleNodeProposalDTO(projectDTO.getProject(), projectDTO.getInstructorUsernames(), room, timeSlot));
//                }
//
//            }
//        }
////
////        System.out.println("🏕️ See Schedule Nodes:");
////        for (ScheduleNodeProposal node : allNodes) {
////            System.out.println("Project: " + node.getProject());
////            System.out.println("ProjectId: " + node.getProject().getProjectId());
////            System.out.println("Instructor of project: " + node.getInstructorUsernames());
////            System.out.println("Room: " + node.getRoom());
////            System.out.println("Time Slot: " + node.getTimeSlot());
////            System.out.println("----------------------------------");
////        }
//
////        Map<ScheduleNodeProposal, List<ScheduleNodeProposal>> adjacencyList = adjacencyList(allNodes, TIME_SLOT_DURATION);
//
//
//        // Return a simple confirmation message
//        return "Schedule generation check completed!";
//
//    }


    // project
//    public Map<String, List<String>> createInstructorProjectMap(List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {
//
//        // key: instructor, value: project
//        Map<String, List<String>> instructorProjects =  new HashMap<>();
//        for (ProjectWithInstructorsDTO instructors: projectWithInstructorsDTOList) {
//
//            String projectId = instructors.getProject().getProjectId();
//
//           // loop all instructor in projectId เพื่อสร้าง list ของ instructor
//           for (String username : instructors.getInstructorUsernames()) {
//
//                if (!instructorProjects.containsKey(username)) {
//                   instructorProjects.put(username, new ArrayList<>());
//                }
//
//               instructorProjects.get(username).add(projectId);
//            }
//
//        }
//
////        System.out.println("👩🏻‍Instructor and List of project");
////        // https://www.geeksforgeeks.org/hashmap-entryset-method-in-java/
////        for (Map.Entry<String, List<String>> entry : instructorProjects.entrySet()) {
////            System.out.println("Instructor: " + entry.getKey()+ " => Projects: " + entry.getValue());
////        }
//
//
//        return instructorProjects;
//    }

//    public Map<ScheduleNodeProposal, List<ScheduleNodeProposal>> adjacencyList(List<ScheduleNodeProposal> allNodes, int TIME_SLOT_DURATION) {
//
//        Map<ScheduleNodeProposal, List<ScheduleNodeProposal>> listMap = new HashMap<>();
//
//        // create empty list ของ each node
//        for (ScheduleNodeProposal node: allNodes) {
//            listMap.put(node, new ArrayList<>());
//        }
//
//        // เอาเเต่ละ project ของ instructor ไว้ด้วยกันเพื่อเช็ค conflict ของ instructor
//        // key: instructor, value: project
//        Map<String, List<String>> instructorProjects =  new HashMap<>();
//
//        for (ScheduleNodeProposal node: allNodes) {
//
//            String projectId = node.getProject().getProjectId();
//
//            // loop all instructor in projectId เพื่อสร้าง list ของ instructor
//            for (String instructor : node.getInstructorUsernames()) {
//
//                if (!instructorProjects.containsKey(instructor)) {
//                    instructorProjects.put(instructor, new ArrayList<>());
//                }
//
//                instructorProjects.get(instructor).add(projectId);
//            }
//
//        }
//
//        System.out.println("👩🏻‍Instructor and List of project"+instructorProjects);
//
//        for (int i = 0; i < allNodes.size(); i++) {
//
//            // Note:  ScheduleNodeProposal มีข้อมูล project, username, room, timeSlots
//            ScheduleNodeProposal node1 = allNodes.get(i);
//
//            for (int j = i +1; j < allNodes.size(); j++) {
//                ScheduleNodeProposal node2 = allNodes.get(j);
//
//                System.out.println("📌 Print Loop");
//                System.out.println("allNodes in i" + node1.getProject().getProjectId());
//                System.out.println("allNodes in j" + node2.getProject().getProjectId());
//
//                // project เดียวกันจะไม่เกิดการใช้ห้องอื่นในเวลาเดียวกัน
//                if (node1.getProject().getProjectId().equals(node2.getProject().getProjectId())) {
//                    listMap.get(node1).add(node2);
//                    listMap.get(node2).add(node1);
//                    continue;
//                }
//
//                for (ScheduleNodeProposal node : allNodes) {
//
//                    List<ScheduleNodeProposal> adjacentNodes = listMap.get(node);
//
//                    System.out.println("Node: " + node.getProject().getProjectId());
//                    System.out.println("Adjacent Nodes:");
//
//                    for (ScheduleNodeProposal adjacentNode : adjacentNodes) {
//                        System.out.println(" - " + adjacentNode.getProject().getProjectId() + "+" + adjacentNode.getTimeSlot());
//                        System.out.println( adjacentNode.getRoom());
//                    }
//                }
//
//
//
//
//            }
//        }
//
//        return listMap;
//
//    }


}
