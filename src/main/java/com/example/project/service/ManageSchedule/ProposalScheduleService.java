package com.example.project.service.ManageSchedule;

import com.example.project.DTO.ManageSchedule.*;
import com.example.project.entity.*;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.ProposalSchedRepository;
import com.example.project.repository.RoomRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
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

    @Autowired
    private ProposalSchedRepository proposalSchedRepository;

    // check already schedule
    public boolean haveExitSchedule(String program, String semesterYear) {

        List<String> projectIds = projectRepository.findByProjectIdAndProgramAndSemster(program, semesterYear);

        System.out.print("🪸List of project: " + projectIds);

        if(projectIds.isEmpty()) {
            System.out.println("No projects found program: " + program);
            return false;
        }

        // have = ture
        return proposalSchedRepository.existsByProjectId(projectIds);
    }

    // setting button
    public List<String> getRoom() {
        List<Room> roomList = roomRepository.findAll();
        return roomList.stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toList());
    }

    public Map<String, String> getRoomWithFloor() {
        List<Room> roomList = roomRepository.findAll();
        return roomList.stream()
                .collect(Collectors.toMap(
                        Room::getRoomNumber,
                        room -> String.valueOf(room.getFloor())
                ));
    }

    // prepare data of project in controller
    public List<ProjectWithInstructorsDTO> prepareProject(String program, String semesterYear) {

        List<Project> ProjectList = projectRepository.findByProjectAndProgramAndSemster(program, semesterYear);

        int semesterYearInt = Integer.parseInt(semesterYear);

        return ProjectList.stream()
                .filter(i -> program.equalsIgnoreCase(i.getProgram()))
                // อย่าลืมกลับมา filter อาจารย์ เเล้วก็ปีการศึกษา
                //❗️❗️❗️❗️❗️ ห้ามลบ
                .filter(i -> {
                    List<StudentProject> studentProjects = i.getStudentProjects();
                    if (studentProjects == null || studentProjects.isEmpty()) return false;

                    boolean hasActive = studentProjects.stream()
                            .anyMatch(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()));

                    boolean allExited = studentProjects.stream()
                            .allMatch(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()));

                    return hasActive && !allExited;
                })
                .filter(i -> Integer.parseInt(i.getSemester()) == semesterYearInt)
                .map(i -> {

                    // get instructor
                    List<String> instructorList = i.getProjectInstructorRoles().stream()
                            .filter(instructorRole -> "Advisor".equalsIgnoreCase(instructorRole.getRole()) || "Committee".equalsIgnoreCase(instructorRole.getRole()))
                            .map(projectInstructorRole -> {
                                Instructor instructor = projectInstructorRole.getInstructor();

                                if (instructor != null && instructor.getAccount() != null) {

//                                    return  instructor.getAccount().getUsername();
                                    return instructor.getProfessorName();
                                }
                                return null;
                            }).filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    if(instructorList.isEmpty()) {
                        return null;
                    }

                    return new ProjectWithInstructorsDTO(i, instructorList);

                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public ScheduleProposalResponseDTO generateSchedule(String startDate,String endDate,String startTime,String endTime,List<String> roomNumbers, List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {

        System.out.println("🙊startDate: "+ startDate +" endDate: " + endDate);

        // https://www.geeksforgeeks.org/how-to-convert-a-string-to-a-localdate-in-java/
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateConvert = LocalDate.parse(startDate, dateFormatter);
        LocalDate endDateConvert = LocalDate.parse(endDate, dateFormatter);

        // https://stackoverflow.com/questions/30788369/coverting-string-to-localtime-with-without-nanoofseconds
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime startTimeConvert = LocalTime.parse(startTime,timeFormatter);
        LocalTime endTimeConvert = LocalTime.parse(endTime,timeFormatter);

//        System.out.println("🔖 Room Numbers:");
//        for (String room: roomNumbers) {
//            System.out.println("🙊Room List: "+ room);
//        }

        roomNumbers.sort(Comparator.naturalOrder());

        for (String room: roomNumbers) {
            System.out.println("Room Sort: "+ room);
        }

        //create time slots
        List<Pair<LocalDateTime, LocalDateTime>> allTimeSlots = generateTimeSlots(startDateConvert, endDateConvert, startTimeConvert, endTimeConvert);

        System.out.println("🔖 All time slot: ");
        System.out.println(allTimeSlots);

        //create time slots with room
        List<ScheduleSlotDTO> availableSlots = generateAvailableSlots(allTimeSlots, roomNumbers);

        List<ScheduleSlotDTO> workingSlots = availableSlots.stream()
                .filter(slot -> !"Break".equals(slot.getRoom())).collect(Collectors.toList());

        System.out.println("workingSlots count: " + workingSlots);

        int realSlotCount = 0;

        for(Pair<LocalDateTime, LocalDateTime> slot : allTimeSlots) {

            long durationMinutes = Duration.between(slot.a, slot.b).toMinutes();

            if (durationMinutes != TIME_BREAK) {
                realSlotCount++;
            }

        }
        System.out.println("realSlotCount: " + realSlotCount);

        int requiredSlots = (int) (projectWithInstructorsDTOList.size() * 1.20);

        if (workingSlots.size() < requiredSlots) {

            int requiredRooms = (int) Math.ceil((double) requiredSlots / realSlotCount);
            int missingRooms = requiredRooms - roomNumbers.size();

            System.out.println("❌ Not enough time slots available");
            return new ScheduleProposalResponseDTO("error", "Not enough rooms to generate the schedule. You are missing " + missingRooms + " room(s).");
        }

        // sort project ตาม instructor
        List<ProjectWithInstructorsDTO> sortedProjects = sortProjectsByInstructors(projectWithInstructorsDTOList);

        // group time
        Map<LocalDateTime, List<ScheduleSlotDTO>> timeGrouping = groupSlots(availableSlots);

        List<ScheduleAssignmentDTO> scheduledAssignments = new ArrayList<>();
        List<ProjectWithInstructorsDTO> unscheduledProjects = new ArrayList<>();

        scheduleProjects(sortedProjects, timeGrouping, scheduledAssignments, unscheduledProjects, roomNumbers, allTimeSlots);

        System.out.println("===== Unscheduled Projects =====");
        for (ProjectWithInstructorsDTO project : unscheduledProjects) {
            System.out.println("Project ID: " + project.getProject().getProjectId() +
                    " | Instructors: " + project.getInstructorUsernames());
        }

        System.out.println("===== Scheduled Assignments =====");
        for (ScheduleAssignmentDTO assignment : scheduledAssignments) {
            System.out.println("Project ID: " + assignment.getProjectId() +
                    " | Room: " + assignment.getRoomNumber() +
                    " | Time: " + assignment.getStartTime() + " - " + assignment.getEndTime() +
                    " | Instructors: " + assignment.getInstructorUsernames());
        }

//        if (!unscheduledProjects.isEmpty()) {
//            return new ScheduleProposalResponseDTO("error", "Some projects could not be scheduled");
//        }


        System.out.println("🔖 All time slot: ");

        for (Pair<LocalDateTime, LocalDateTime> slot : allTimeSlots) {
            // https://www.antlr.org/api/Java/org/antlr/v4/runtime/misc/Pair.html
            System.out.println("Start: " + slot.a + " - End: " + slot.b);
        }

        List<TimeSlotDTO> timeSlotDTOList = new ArrayList<>();

        for(Pair<LocalDateTime, LocalDateTime> slot : allTimeSlots) {

            startTime = slot.a.toString();
            endTime =  slot.b.toString();

            long durationMinutes = Duration.between(slot.a, slot.b).toMinutes();

            String breakLabel = "";
            boolean isBreak = false;

            if (durationMinutes == TIME_BREAK) {
                isBreak = true;
                breakLabel = "Take a Break";
            }

            timeSlotDTOList.add(new TimeSlotDTO(startTime, endTime, isBreak, breakLabel));
        }

//        for (TimeSlotDTO time : timeSlotDTOList) {
//
//            System.out.println("After dailySlotCount: " + time.getStartTime());
//            System.out.print("getEndTimet: " + time.getEndTime());
//            System.out.println("getBreakLabel: " + time.getBreakLabel());
//
//        }


        //❗️❗️❗️❗️❗️ ห้ามลบ
        try {
            saveProposalSchedule(scheduledAssignments);

            if (!unscheduledProjects.isEmpty()) {
                saveUnProposalSchedule(unscheduledProjects);
            }

        } catch (Exception e) {
            return new ScheduleProposalResponseDTO("error", "Failed to generate schedule");
        }

        return new ScheduleProposalResponseDTO("success", "finished generate schedule", scheduledAssignments, unscheduledProjects, timeSlotDTOList);
    }

    private static final int TIME_SLOT_DURATION = 25;
    private static final int TIME_BETWEEN= 5;
    private static final int TIME_BREAK = 15;


    // generate time slot
    public List<Pair<LocalDateTime, LocalDateTime>> generateTimeSlots(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime){

        List<Pair<LocalDateTime, LocalDateTime>> timeSolts = new ArrayList<>();
        Map<LocalDate, Integer> dailySlotCount = new HashMap<>();

        // https://www.geeksforgeeks.org/localdatetime-ofdate-time-method-in-java-with-examples/
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // https://www.geeksforgeeks.org/localdate-isbefore-method-in-java-with-examples/
        while (!startDateTime.isAfter(endDateTime)) {
            LocalDate currentDate = startDateTime.toLocalDate();

            // https://www.geeksforgeeks.org/dayofweek-getvalue-method-in-java-with-examples/
            if (startDateTime.getDayOfWeek().getValue() == 6 || startDateTime.getDayOfWeek().getValue() == 7) {

//                System.out.println("🍭 Check Start Day ");
//                System.out.println("Day: " + startDateTime.getDayOfWeek().name());
//                System.out.println("Start day value: " + startDateTime.getDayOfWeek().getValue());

                startDateTime = LocalDateTime.of(startDateTime.toLocalDate().plusDays(1), startTime);
                continue;
            }

            if (startDateTime.getDayOfWeek().getValue() <= 5) {

                dailySlotCount.putIfAbsent(currentDate, 0);
//                System.out.println("dailySlotCount: " + dailySlotCount);

                int slotCount = dailySlotCount.get(currentDate) + 1;
//                System.out.println("slotCount: " + slotCount);

                // https://www.geeksforgeeks.org/dayofweek-getvalue-method-in-java-with-examples/
//                System.out.println("🍭 Check Start Day ");
//                System.out.println("Day: " + startDateTime.getDayOfWeek().name());
//                System.out.println("Start day value: " + startDateTime.getDayOfWeek().getValue());

                // https://www.geeksforgeeks.org/localtime-plusminutes-method-in-java-with-examples/
                LocalDateTime endSlotTime = startDateTime.plusMinutes(TIME_SLOT_DURATION);

                if (endSlotTime.toLocalTime().isAfter(endTime)) {
                    startDateTime = LocalDateTime.of(startDateTime.toLocalDate().plusDays(1), startTime);
                    continue;
                }

                timeSolts.add(new Pair<>(startDateTime, endSlotTime));

//                System.out.println("Added Time Slot: " + startDateTime + " - " + endSlotTime);

                dailySlotCount.put(currentDate, slotCount);
//                System.out.println("After dailySlotCount: " + dailySlotCount);

                if (slotCount % 3 == 0) {

                    LocalDateTime breakStart = endSlotTime;
                    LocalDateTime breakEnd = breakStart.plusMinutes(TIME_BREAK);

                    // ถ้าเวลา break หลัง endTime
                    if (breakEnd.toLocalTime().isAfter(endTime)) {
                        startDateTime = LocalDateTime.of(startDateTime.toLocalDate().plusDays(1), startTime);
                        continue;
                    }

                    timeSolts.add(new Pair<>(breakStart, breakEnd));
//                    System.out.println("Take a break🍔: " + breakStart + " - " + breakEnd);

                    startDateTime = breakEnd;

                } else {

                    startDateTime = endSlotTime.plusMinutes(TIME_BETWEEN);

                }
            }


            if (!startDateTime.toLocalDate().isAfter(endDate) && startDateTime.toLocalTime().isAfter(endTime)) {

                startDateTime = LocalDateTime.of(startDateTime.toLocalDate().plusDays(1), startTime);
            }
        }

        System.out.println("🔖 All time slot: ");

        for (Pair<LocalDateTime, LocalDateTime> slot : timeSolts) {
            // https://www.antlr.org/api/Java/org/antlr/v4/runtime/misc/Pair.html
            System.out.println("Start: " + slot.a + " - End: " + slot.b);
        }

        return timeSolts;
    }

    // AvailableSlots with room
    public List<ScheduleSlotDTO> generateAvailableSlots( List<Pair<LocalDateTime, LocalDateTime>> timeSlots, List<String> roomNumbers) {

        List<ScheduleSlotDTO> slotDTOList = new ArrayList<>();
        Map<LocalDate, Integer> dailySlotCount = new HashMap<>();

        for( Pair<LocalDateTime, LocalDateTime> timeSlot : timeSlots) {

            LocalDate currentDate = timeSlot.a.toLocalDate();
            dailySlotCount.putIfAbsent(currentDate, 0);
//            System.out.println("dailySlotCount: " + dailySlotCount);

            int slotCount = dailySlotCount.get(currentDate) + 1;
//            System.out.println("slotCount: " + slotCount);


            if (slotCount % 4 == 0){

                slotDTOList.add(new ScheduleSlotDTO("Break", timeSlot.a, timeSlot.b));

            } else {

                for (String roomNumber : roomNumbers) {

                    slotDTOList.add(new ScheduleSlotDTO(roomNumber, timeSlot.a, timeSlot.b));

                }
            }

            dailySlotCount.put(currentDate, slotCount);
//            System.out.println("After dailySlotCount: " + dailySlotCount);

        }

        System.out.println("🕰️Time slots with room");
        System.out.println(slotDTOList);

        for (ScheduleSlotDTO slotDTO: slotDTOList) {
            System.out.println(slotDTO);
        }

        return slotDTOList;
    }

    // sort project
    public List<ProjectWithInstructorsDTO> sortProjectsByInstructors(List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {

        List<ProjectWithInstructorsDTO> sortedProjects = new ArrayList<>();

        System.out.println("🧮 Before sorting:");
        for (ProjectWithInstructorsDTO project : projectWithInstructorsDTOList) {
            System.out.println("ProjectId: " + project.getProject().getProjectId() + ", Instructor Count: " + project.getInstructorUsernames().size());
            System.out.println("List of instructor: " + project.getInstructorUsernames());
        }

        // เก็บคู่ instructor & project
        Map<List<String> , List<String>> mapInstructorWithProject = new HashMap<>();

        for(int i = 0; i < projectWithInstructorsDTOList.size(); i++) {

            ProjectWithInstructorsDTO project = projectWithInstructorsDTOList.get(i);
            List<String> instructors = project.getInstructorUsernames();
//            System.out.println("🍭 ProjectId: " + project.getProject().getProjectId());

            // มันจะไม่เกิดขึ้นเเน่นอน
            if(instructors.size() == 1) {

                List<String> instructorPair = new ArrayList<>();
                instructorPair.add(instructors.get(0));
                instructorPair.add("");

                mapInstructorWithProject
                        .computeIfAbsent(instructorPair, key -> new ArrayList<>())
                        .add(project.getProject().getProjectId());

            } else {

                for(int j = 0; j < instructors.size(); j++) {

                    for(int k = j+1; k < instructors.size(); k++) {
                        List<String> instructorPair = new ArrayList<>();
                        instructorPair.add(instructors.get(j));
                        instructorPair.add(instructors.get(k));

//                        System.out.println("👀 Before sort name: " + instructorPair);

                        // https://how.dev/answers/what-is-the-comparatornaturalorder-method-in-java
                        instructorPair.sort(Comparator.naturalOrder());
//                        System.out.println("After sort name: " + instructorPair);

                        // https://stackoverflow.com/questions/53846428/java-map-computeifabsent-issue
                        mapInstructorWithProject.computeIfAbsent(instructorPair, key -> new ArrayList<>())
                                .add(project.getProject().getProjectId());
                    }
                }
            }
        }

        System.out.println("🌼Check map instructor pairs: ");
        for(Map.Entry<List<String> , List<String>>  entry : mapInstructorWithProject.entrySet()){
            System.out.println("Instructor Pair: " + entry.getKey()+ " -> Projects: "+ entry.getValue());
        }

        //https://stackoverflow.com/questions/29936581/how-can-i-sort-a-map-according-to-the-parameters-of-its-values
        // จัดเรียงค่าตามจน.โปรเจค
        mapInstructorWithProject = mapInstructorWithProject.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().size() - e1.getValue().size())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));

        System.out.println("🌼 Sorted map instructor pairs: ");

        for(Map.Entry<List<String> , List<String>> entry: mapInstructorWithProject.entrySet()){
            System.out.println("Instructor Pair: " + entry.getKey() + " -> Projects: " + entry.getValue());
        }

        Set<String> addprojectIds = new HashSet<>();

        // เอาค่า projectId มาเก็บ
        for (Map.Entry<List<String>, List<String>> entry : mapInstructorWithProject.entrySet()){

            for(String projectId : entry.getValue()) {

                if(!addprojectIds.contains(projectId)){

                    projectWithInstructorsDTOList.stream()
                            .filter(project -> project.getProject().getProjectId().equals(projectId))
                            .findFirst().ifPresent(project -> {
                                sortedProjects.add(project);
                                addprojectIds.add(projectId);
                            });
                }
            }

        }

        System.out.println("✅ Final Sorted Projects:");
        for(ProjectWithInstructorsDTO project: sortedProjects) {

            System.out.println("ProjectId: " + project.getProject().getProjectId() + ", Instructors: " + project.getInstructorUsernames());

        }


        return sortedProjects;
    }


    public Map<LocalDateTime, List<ScheduleSlotDTO>> groupSlots(List<ScheduleSlotDTO> availableSlots) {

        Map<LocalDateTime, List<ScheduleSlotDTO>> timeGrouping = new LinkedHashMap<>();

        List<ScheduleSlotDTO> nonBreakSlots = availableSlots.stream()
                .filter(slot -> !"Break".equalsIgnoreCase(slot.getRoom()))
                .collect(Collectors.toList());

//        for (ScheduleSlotDTO slot : nonBreakSlots){
//            System.out.println("🏖️ Slot: " + slot.getRoom() + " => " + slot.getStartTime() + " - " + slot.getEndTime());
//        }

        for(ScheduleSlotDTO slot : nonBreakSlots) {
            LocalDateTime startTime = slot.getStartTime();
//            System.out.println("🌻 startTime " + startTime);

            if(!timeGrouping.containsKey(startTime)) {
                timeGrouping.put(startTime, new ArrayList<>());
            }

            timeGrouping.get(startTime).add(slot);
//            System.out.println("✅ Add " + timeGrouping);

        }

        System.out.println("🙊 Created " + timeGrouping.size() + " time groups");
        return  timeGrouping;
    }

    // 🤯🤯🤯🤯
    private void scheduleProjects(List<ProjectWithInstructorsDTO> projects, Map<LocalDateTime, List<ScheduleSlotDTO>> timeGroupedSlots, List<ScheduleAssignmentDTO> scheduledAssignments,  List<ProjectWithInstructorsDTO> unscheduledProjects, List<String> roomNumbers, List<Pair<LocalDateTime, LocalDateTime>> allTimeSlots) {

        // [ProjectWithInstructorsDTO{project=DST SP2024-19, instructorUsernames=[Aj.Tipajin, Aj.Petch, Aj.Suradej]}, ProjectWithInstructorsDTO{project=DST SP2024-21, instructorUsernames=[Aj.Petch, Aj.Tipajin, Aj.Pisit]}
        List<ProjectWithInstructorsDTO> remainderProject = new ArrayList<>(projects);

        System.out.println("📋Remainder: " + remainderProject);

        Map<LocalDateTime, List<ScheduleSlotDTO>> availableTimeSlots = new HashMap<>();
        for(Map.Entry<LocalDateTime, List<ScheduleSlotDTO>> entry : timeGroupedSlots.entrySet()) {

            availableTimeSlots.put(entry.getKey(), new ArrayList<>(entry.getValue()));

        }
        //📋availableTimeSlots: {2025-04-11T13:00=[ScheduleSlotDTO{room='Room TBA 1', startTime=2025-04-11T13:00, endTime=2025-04-11T14:15}, ScheduleSlotDTO{room='Room TBA 2', startTime=2025-04-11T13:00, endTime=2025-04-11T14:15}
        System.out.println("📋availableTimeSlots: " + availableTimeSlots);

        List<LocalDateTime> sortTime = new ArrayList<>(availableTimeSlots.keySet());
        // https://www.geeksforgeeks.org/collections-sort-java-examples/
        Collections.sort(sortTime);
//        System.out.println("📋availableTimeSlots: " + availableTimeSlots);
//        System.out.println("sortTime: " + sortTime);

        boolean progress = true;

        Map<LocalDateTime, Set<String>> assignedTimeInstructor = new HashMap<>();

        while (!remainderProject.isEmpty() && progress) {

            progress = false;

            // keep success project
            List<ProjectWithInstructorsDTO> scheduledProjectsSuccess = new ArrayList<>();

            for(ProjectWithInstructorsDTO project : remainderProject) {

//                System.out.println("🧸 Start find slot");
//                System.out.println("project: " + project.getProject().getProjectId());

                // keep project status
                boolean projectScheduled = false;

                for (LocalDateTime timeSlot : sortTime) {

//                    System.out.println("⏳ Find Time: " + timeSlot);

                    if (availableTimeSlots.get(timeSlot).isEmpty()) {
//                        System.out.println("😅ช่วงเวลานี้ได้ถูกใช้ไปเเล้ว");
                        continue;
                    }

                    //check list time and instruc
//                    System.out.println("⏭️ assignedTimeInstructor before check" + assignedTimeInstructor);

                    Set<String> instructorsAtTime = assignedTimeInstructor.getOrDefault(timeSlot, new HashSet<>());

                    boolean instructorConflict = false;

                    for(String instructor : project.getInstructorUsernames()) {

//                        System.out.println("👩🏻‍🏫 Instructor: " + instructor);

                        if (instructorsAtTime.contains(instructor)) {

//                            System.out.println("assignedTimeInstructor " + assignedTimeInstructor);
//                            System.out.println("chek conflict: " + instructorsAtTime);

                            instructorConflict = true;
                            break;
                        }
                    }

                    // instructorConflict(False)
                    if(!instructorConflict) {

                        ScheduleSlotDTO slot = availableTimeSlots.get(timeSlot).remove(0);
//                        System.out.println("slot " + slot);


                        ScheduleAssignmentDTO assignProject = new ScheduleAssignmentDTO(
                                project.getProject().getProjectId(),
                                slot.getRoom(),
                                slot.getStartTime(),
                                slot.getEndTime(),
                                project.getInstructorUsernames()
                        );

                        scheduledAssignments.add(assignProject);

                        if(!assignedTimeInstructor.containsKey(timeSlot)) {
                            assignedTimeInstructor.put(timeSlot, new HashSet<>());
//                            System.out.println("Add assignedTimeInstructor " + assignedTimeInstructor);

                        }

                        assignedTimeInstructor.get(timeSlot).addAll(project.getInstructorUsernames());
//                        System.out.println("🍭 assignedTimeInstructor" + assignedTimeInstructor);


                        scheduledProjectsSuccess.add(project);
                        projectScheduled = true;

//                        System.out.println("✅ Scheduled project " + project.getProject().getProjectId() + " in room " + slot.getRoom() + " at " + slot.getStartTime());

                        break;
                    }

                }

                if (!projectScheduled) {
                    unscheduledProjects.add(project);
                }

            }

            remainderProject.removeAll(scheduledProjectsSuccess);
//            System.out.println("Remove remainderProject" + remainderProject );
        }


    }


    public void saveProposalSchedule(List<ScheduleAssignmentDTO> scheduledAssignments) {

        for (ScheduleAssignmentDTO assignSlot : scheduledAssignments) {
            ProposalSchedule proposalSchedule = new ProposalSchedule();

            System.out.println(assignSlot);

            // https://www.quora.com/How-do-I-generate-a-unique-ID-in-Java
            proposalSchedule.setProposalScheduleId(UUID.randomUUID().toString());
            proposalSchedule.setStartTime(assignSlot.getStartTime());
            proposalSchedule.setEndTime(assignSlot.getEndTime());
            proposalSchedule.setDate(assignSlot.getStartTime().toLocalDate().toString());
            proposalSchedule.setStatus("Active");
            proposalSchedule.setRemark("Auto-generated schedule");
            proposalSchedule.setRecordOn(LocalDateTime.now());

            Project project = projectRepository.findByProjectId(assignSlot.getProjectId());
            String projectId = project.getProjectId();
            System.out.println(projectId);
            proposalSchedule.setProjectId(projectId);

            Room room = roomRepository.findByRoomNumber(assignSlot.getRoomNumber());
            String roomID = room.getRoomNumber();
            System.out.println(roomID);
            proposalSchedule.setRoom(roomID);


            proposalSchedRepository.save(proposalSchedule);

        }
    }

    public void saveUnProposalSchedule(List<ProjectWithInstructorsDTO> unscheduledProjects) {

        for (ProjectWithInstructorsDTO assignUnSlot : unscheduledProjects) {
            ProposalSchedule proposalSchedule = new ProposalSchedule();

            System.out.println(assignUnSlot);

            // https://www.quora.com/How-do-I-generate-a-unique-ID-in-Java
            proposalSchedule.setProposalScheduleId(UUID.randomUUID().toString());
            proposalSchedule.setStatus("Non-Active");
            proposalSchedule.setRemark("Auto-Ungenerated schedule");
            proposalSchedule.setRecordOn(LocalDateTime.now());

            Project project = projectRepository.findByProjectId(assignUnSlot.getProject().getProjectId());
            String projectId = project.getProjectId();
            System.out.println(projectId);
            proposalSchedule.setProjectId(projectId);

            proposalSchedRepository.save(proposalSchedule);

            duplicateUnScheduleProject(projectId);

        }
    }

    public void duplicateUnScheduleProject(String projectId) {

        ProposalSchedule newProject = new ProposalSchedule();

//        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        newProject.setProposalScheduleId(UUID.randomUUID().toString());
        newProject.setProjectId(projectId);
        newProject.setRemark("User-Add");
        newProject.setEditedOn(LocalDateTime.now());
//        newProject.setEditedByUser(username);
        newProject.setRecordOn(LocalDateTime.now());
        newProject.setStatus("Active");


        proposalSchedRepository.save(newProject);
    }

}