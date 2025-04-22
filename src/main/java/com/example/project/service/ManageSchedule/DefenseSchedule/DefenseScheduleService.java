package com.example.project.service.ManageSchedule.DefenseSchedule;

import com.example.project.DTO.ManageSchedule.Defense.ScheduleDefenseResponseDTO;
import com.example.project.DTO.ManageSchedule.Defense.SlotDTO;
import com.example.project.DTO.ManageSchedule.Defense.TimeEachSlotDTO;
import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
import com.example.project.DTO.ManageSchedule.ScheduleAssignmentDTO;
import com.example.project.DTO.ManageSchedule.ScheduleProposalResponseDTO;
import com.example.project.DTO.ManageSchedule.ScheduleSlotDTO;
import com.example.project.entity.*;
import com.example.project.repository.DefenseSchedRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.ProposalSchedRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefenseScheduleService {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    DefenseSchedRepository defenseSchedRepository;
//    @Autowired
//    ProposalSchedRepository proposalSchedRepository;


    // check already schedule
    public boolean haveExitDefenseSchedule(String semesterYear) {

        List<String> projectIds = projectRepository.findByProjectIdAndSemster(semesterYear);

        if(projectIds.isEmpty()) {
            System.out.println("No projects found");
            return false;
        }

        // have = ture
        return defenseSchedRepository.existsByProjectId(projectIds);
    }


    // prepare data of project in controller
    public List<ProjectWithInstructorsDTO> prepareDefenseProject(String semesterYear) {

        List<Project> ProjectList = projectRepository.findByProjectAndSemster(semesterYear);
        int semesterYearInt = Integer.parseInt(semesterYear);

//        List<Project> ProjectList = projectRepository.findAll();
//
//        int maxSemester = ProjectList.stream()
//                .mapToInt(i -> Integer.parseInt(i.getSemester())).max().orElse(0);
//
//        System.out.println("🧸maxSemester" + maxSemester);

        return ProjectList.stream()
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


    public ScheduleDefenseResponseDTO generateDefenseSchedule(String startDate, String endDate, Map<String, List<TimeEachSlotDTO>> timeSlots, List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {

        System.out.println("🙊startDate: "+ startDate +" endDate: " + endDate);

        //create time slots
        List<Pair<LocalDateTime, LocalDateTime>> allTimeSlots = generateTimeSlots(timeSlots);

        int requiredSlots = (int) (projectWithInstructorsDTOList.size() * 1.20);
        System.out.println("Total Project: " + projectWithInstructorsDTOList.size());
        System.out.println("🕰️ requiredSlots: " + requiredSlots);

        //create time slots with room
        List<ScheduleSlotDTO> availableSlots = generateAvailableSlots(allTimeSlots, requiredSlots);

        // sort project ตาม instructor
        List<ProjectWithInstructorsDTO> sortedProjects = sortProjectsByInstructors(projectWithInstructorsDTOList);

        // group time
        Map<LocalDateTime, List<ScheduleSlotDTO>> timeGrouping = groupSlots(availableSlots);

        List<ScheduleAssignmentDTO> scheduledAssignments = new ArrayList<>();
        List<ProjectWithInstructorsDTO> unscheduledProjects = new ArrayList<>();

        scheduleProjects(sortedProjects, timeGrouping, scheduledAssignments, unscheduledProjects, allTimeSlots);

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
//            return new ScheduleDefenseResponseDTO("error", "Some projects could not be scheduled");
//        }

        List<String> roomNameUse = new ArrayList<>();

        for (ScheduleAssignmentDTO assignment : scheduledAssignments) {

            if(!roomNameUse.contains(assignment.getRoomNumber())) {
                roomNameUse.add(assignment.getRoomNumber());
            }
        }

        //❗️❗️❗️❗️❗️ ห้ามลบ
        try {
            saveDefenseSchedule(scheduledAssignments);

            if (!unscheduledProjects.isEmpty()) {
                saveUnDefenseSchedule(unscheduledProjects);
            }

        } catch (Exception e) {
            return new ScheduleDefenseResponseDTO("error", "Failed to generate schedule");
        }

        return new ScheduleDefenseResponseDTO("success", "finished generate schedule", scheduledAssignments, unscheduledProjects, roomNameUse);

    }


    // generate time slot
    public List<Pair<LocalDateTime, LocalDateTime>> generateTimeSlots(Map<String, List<TimeEachSlotDTO>> timeSlots){

        List<Pair<LocalDateTime, LocalDateTime>> timeSolts = new ArrayList<>();
        // https://www.geeksforgeeks.org/how-to-convert-a-string-to-a-localdate-in-java/
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Map.Entry<String, List<TimeEachSlotDTO>> entry : timeSlots.entrySet()) {

            String dateString = entry.getKey();
            List<TimeEachSlotDTO> slots = entry.getValue();

            LocalDate date = LocalDate.parse(dateString, dateFormatter);

            for(TimeEachSlotDTO slotDTO : slots) {

                LocalTime startTime = LocalTime.parse(slotDTO.getStart(), timeFormatter);
                LocalTime endTime = LocalTime.parse(slotDTO.getEnd(), timeFormatter);

                LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
                LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

                timeSolts.add(new Pair<>(startDateTime, endDateTime));
                System.out.println("➕ Added Slot: " + startDateTime + " - " + endDateTime);

            }
        }

        timeSolts.sort(Comparator.comparing((Pair<LocalDateTime, LocalDateTime> p) -> p.a));

        System.out.println("🔖 All time slot: ");

        for (Pair<LocalDateTime, LocalDateTime> slot : timeSolts) {
            // https://www.antlr.org/api/Java/org/antlr/v4/runtime/misc/Pair.html
            System.out.println("Start: " + slot.a + " - End: " + slot.b);
        }

        return timeSolts;
    }


    public List<ScheduleSlotDTO> generateAvailableSlots(List<Pair<LocalDateTime, LocalDateTime>> timeSlots, int requiredSlots) {

        List<ScheduleSlotDTO> availableSlots = new ArrayList<>();

        int roomCount = 0;

        while (availableSlots.size() < requiredSlots) {

            roomCount ++;

            String roomName = "Room TBA " + roomCount;

            for (Pair<LocalDateTime, LocalDateTime> timeSlot : timeSlots ) {

                availableSlots.add(new ScheduleSlotDTO(roomName, timeSlot.a, timeSlot.b));

                if(availableSlots.size() >= requiredSlots) {
                    break;
                }

            }

        }

//        for (ScheduleSlotDTO slot : availableSlots) {
//
//            System.out.println("🏢 Start Time -> " + slot.getStartTime());
//            System.out.println("End Time -> " + slot.getEndTime());
//            System.out.println("Temp room -> " + slot.getRoom());
//
//        }

        return availableSlots;

    }

    // sort project
    public List<ProjectWithInstructorsDTO> sortProjectsByInstructors(List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {

        List<ProjectWithInstructorsDTO> sortedProjects = new ArrayList<>();

//        System.out.println("🧮 Before sorting:");
//        for (ProjectWithInstructorsDTO project : projectWithInstructorsDTOList) {
//            System.out.println("ProjectId: " + project.getProject().getProjectId() + ", Instructor Count: " + project.getInstructorUsernames().size());
//            System.out.println("List of instructor: " + project.getInstructorUsernames());
//        }

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
//
//        System.out.println("🌼Check map instructor pairs: ");
//        for(Map.Entry<List<String> , List<String>>  entry : mapInstructorWithProject.entrySet()){
//            System.out.println("Instructor Pair: " + entry.getKey()+ " -> Projects: "+ entry.getValue());
//        }

        //https://stackoverflow.com/questions/29936581/how-can-i-sort-a-map-according-to-the-parameters-of-its-values
        // จัดเรียงค่าตามจน.โปรเจค
        mapInstructorWithProject = mapInstructorWithProject.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().size() - e1.getValue().size())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));

//        System.out.println("🌼 Sorted map instructor pairs: ");
//
//        for(Map.Entry<List<String> , List<String>> entry: mapInstructorWithProject.entrySet()){
//            System.out.println("Instructor Pair: " + entry.getKey() + " -> Projects: " + entry.getValue());
//        }

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

//        System.out.println("✅ Final Sorted Projects:");
//        for(ProjectWithInstructorsDTO project: sortedProjects) {
//
//            System.out.println("ProjectId: " + project.getProject().getProjectId() + ", Instructors: " + project.getInstructorUsernames());
//
//        }


        return sortedProjects;
    }

    // ScheduleSlotDTO

    public Map<LocalDateTime, List<ScheduleSlotDTO>> groupSlots(List<ScheduleSlotDTO> availableSlots) {

        Map<LocalDateTime, List<ScheduleSlotDTO>> timeGrouping = new LinkedHashMap<>();

        for(ScheduleSlotDTO slot : availableSlots) {

            LocalDateTime startTime = slot.getStartTime();
//            System.out.println("🌻 startTime " + startTime);

            if(!timeGrouping.containsKey(startTime)) {
                timeGrouping.put(startTime, new ArrayList<>());
            }

            timeGrouping.get(startTime).add(slot);
//            System.out.println("✅ Add " + timeGrouping);

        }

//        System.out.println("🙊 Created " + timeGrouping.size() + " time groups");
//
//        for (Map.Entry<LocalDateTime, List<ScheduleSlotDTO>> entry :  timeGrouping.entrySet()) {
//
//            System.out.println("✅ Add " + entry.getKey() + " => " + entry.getValue());
//
//        }

        return  timeGrouping;
    }


    // 🤯🤯🤯🤯
    private void scheduleProjects(List<ProjectWithInstructorsDTO> projects, Map<LocalDateTime, List<ScheduleSlotDTO>> timeGroupedSlots, List<ScheduleAssignmentDTO> scheduledAssignments,  List<ProjectWithInstructorsDTO> unscheduledProjects, List<Pair<LocalDateTime, LocalDateTime>> allTimeSlots) {

        List<ProjectWithInstructorsDTO> remainderProject = new ArrayList<>(projects);

        System.out.println("📋Remainder: " + remainderProject);

        Map<LocalDateTime, List<ScheduleSlotDTO>> availableTimeSlots = new HashMap<>();

        for(Map.Entry<LocalDateTime, List<ScheduleSlotDTO>> entry : timeGroupedSlots.entrySet()) {

            availableTimeSlots.put(entry.getKey(), new ArrayList<>(entry.getValue()));

        }
//        System.out.println("📋availableTimeSlots: " + availableTimeSlots);

        // sort date
        List<LocalDateTime> sortTime = new ArrayList<>(availableTimeSlots.keySet());
        // https://www.geeksforgeeks.org/collections-sort-java-examples/
        Collections.sort(sortTime);
        System.out.println("📋availableTimeSlots: " + availableTimeSlots);
        System.out.println("sortTime: " + sortTime);

        boolean progress = true;

        // เก็บข้อมูล instructor ทั้งหมดที่้ในช่วงเวลานั้น
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

                    // มันเรียกดูเเค่ช่วงเวลาที่ลูปอยู่
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

        if (!unscheduledProjects.isEmpty()) {
            scheduleProjectsUnschedule(unscheduledProjects, scheduledAssignments, availableTimeSlots, assignedTimeInstructor, allTimeSlots);
        }

//        for(Map.Entry<LocalDateTime, List<ScheduleSlotDTO>> entry : availableTimeSlots.entrySet()) {
//
//            System.out.println("availableTimeSlots" + entry.getKey() + " => " + entry.getValue());
//
//        }

    }

    public void scheduleProjectsUnschedule(List<ProjectWithInstructorsDTO> unscheduledProjects,  List<ScheduleAssignmentDTO> scheduledAssignments, Map<LocalDateTime, List<ScheduleSlotDTO>> availableTimeSlots, Map<LocalDateTime, Set<String>> assignedTimeInstructor, List<Pair<LocalDateTime, LocalDateTime>> allTimeSlots){

        System.out.println("===== Attempting to schedule remaining " + unscheduledProjects.size() + " projects =====");

//        for(Map.Entry<LocalDateTime, List<ScheduleSlotDTO>> entry : availableTimeSlots.entrySet()) {
//
//            System.out.println("📌 availableTimeSlots: " + entry.getKey() + " => " + entry.getValue());
//
//        }
//
//        for(Map.Entry<LocalDateTime, Set<String>> entry : assignedTimeInstructor.entrySet()) {
//
//            System.out.println("👩🏻‍🏫 assignedTimeInstructor: " + entry.getKey() + " => " + entry.getValue());
//
//        }

        List<LocalDateTime> timeSlots = new ArrayList<>(availableTimeSlots.keySet());
        Collections.sort(timeSlots);

        List<ProjectWithInstructorsDTO> stillUnschedule = tryScheduleProjects(unscheduledProjects, scheduledAssignments, availableTimeSlots, assignedTimeInstructor, timeSlots);

        if (!stillUnschedule.isEmpty()) {

            System.out.println("⚠️ Still " + stillUnschedule.size() + " projects could not be scheduled.");
            System.out.println("===== Creating additional rooms for " + stillUnschedule.size() + " projects =====");

            int maxRoomCount = getMaxRoomNumber(scheduledAssignments);
            int additionalRoomsToCreate = Math.min(5, stillUnschedule.size());

            for (int i = 1; i <= additionalRoomsToCreate; i++) {

                String newRoomName = "Room TBA " + (maxRoomCount + i);

                for (LocalDateTime timeSlot : timeSlots) {

//                    System.out.println("⏳ Find Time: " + timeSlot);

                    if (!availableTimeSlots.containsKey(timeSlot)) {
                        availableTimeSlots.put(timeSlot, new ArrayList<>());
                    }

                    LocalDateTime endTime = null;
                    for(Pair<LocalDateTime, LocalDateTime> slot : allTimeSlots) {

                        if (slot.a.equals(timeSlot)) {
                            endTime = slot.b;
                            break;
                        }

                    }

                    if (endTime != null) {
                        availableTimeSlots.get(timeSlot).add(new ScheduleSlotDTO(newRoomName, timeSlot, endTime));
                    }

                }
            }

            List<ProjectWithInstructorsDTO> finalUnscheduled = tryScheduleProjects(stillUnschedule, scheduledAssignments, availableTimeSlots, assignedTimeInstructor, timeSlots);

            unscheduledProjects.clear();
            unscheduledProjects.addAll(finalUnscheduled);

        } else {

            unscheduledProjects.clear();

        }
    }

    public List<ProjectWithInstructorsDTO> tryScheduleProjects(List<ProjectWithInstructorsDTO> unscheduledProjects,  List<ScheduleAssignmentDTO> scheduledAssignments, Map<LocalDateTime, List<ScheduleSlotDTO>> availableTimeSlots, Map<LocalDateTime, Set<String>> assignedTimeInstructor, List<LocalDateTime> timeSlots) {

        List<ProjectWithInstructorsDTO> remainingProjects = new ArrayList<>(unscheduledProjects);
        List<ProjectWithInstructorsDTO> successfullySchedule = new ArrayList<>();

        for(ProjectWithInstructorsDTO project : unscheduledProjects) {
            boolean projectScheduled = false;

            for (LocalDateTime timeSlot : timeSlots) {

//                    System.out.println("⏳ Find Time: " + timeSlot);

                if (availableTimeSlots.get(timeSlot).isEmpty()) {
//                        System.out.println("😅ช่วงเวลานี้ได้ถูกใช้ไปเเล้ว");
                    continue;
                }

                //check list time and instruc
//                    System.out.println("⏭️ assignedTimeInstructor before check" + assignedTimeInstructor);

                // มันเรียกดูเเค่ช่วงเวลาที่ลูปอยู่
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


                    successfullySchedule.add(project);
                    projectScheduled = true;

                    System.out.println("✅ Rescheduled project " + project.getProject().getProjectId() +
                            " in room " + slot.getRoom() + " at " + slot.getStartTime());

                    break;
                }

            }

        }

        remainingProjects.removeAll(successfullySchedule);
        System.out.println("Remaining unscheduled projects: " + remainingProjects.size());

        return  remainingProjects;
    }

    public int getMaxRoomNumber(List<ScheduleAssignmentDTO> scheduledAssignments) {

        int maxRoomCount = 0;

        for(ScheduleAssignmentDTO assignmentDTO : scheduledAssignments) {

            String roomName = assignmentDTO.getRoomNumber();

            if(roomName.startsWith("Room TBA ")) {

                try{

                    int roomNumber = Integer.parseInt(roomName.substring("Room TBA ".length()));
                    maxRoomCount = Math.max(maxRoomCount, roomNumber);

                } catch (NumberFormatException e) {

                }
            }
        }

        return maxRoomCount;
    }

    public void saveDefenseSchedule(List<ScheduleAssignmentDTO> scheduledAssignments) {

        for (ScheduleAssignmentDTO assignSlot : scheduledAssignments) {
            DefenseSchedule defenseSchedule = new DefenseSchedule();

            System.out.println(assignSlot);

            // https://www.quora.com/How-do-I-generate-a-unique-ID-in-Java
            defenseSchedule.setDefenseScheduleId(UUID.randomUUID().toString());
            defenseSchedule.setStartTime(assignSlot.getStartTime());
            defenseSchedule.setEndTime(assignSlot.getEndTime());
            defenseSchedule.setDate(assignSlot.getStartTime().toLocalDate().toString());
            defenseSchedule.setStatus("Non-Active");
            defenseSchedule.setRemark("Auto-generated schedule");
            defenseSchedule.setRecordOn(LocalDateTime.now());
            defenseSchedule.setRoomTemp(assignSlot.getRoomNumber());

            Project project = projectRepository.findByProjectId(assignSlot.getProjectId());
            String projectId = project.getProjectId();
            System.out.println(projectId);
            defenseSchedule.setProjectId(projectId);

//            Room room = roomRepository.findByRoomNumber(assignSlot.getRoomNumber());
//            String roomID = room.getRoomNumber();
//            System.out.println(roomID);
//            defenseSchedule.setRoom(roomID);

            defenseSchedRepository.save(defenseSchedule);

            duplicateProject(projectId, assignSlot.getStartTime().toLocalDate().toString(), assignSlot.getStartTime(), assignSlot.getEndTime());

        }
    }

    public void duplicateProject(String projectId, String date, LocalDateTime startTime, LocalDateTime endTime) {

        DefenseSchedule newProject = new DefenseSchedule();

//        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        newProject.setDefenseScheduleId(UUID.randomUUID().toString());
        newProject.setProjectId(projectId);
        newProject.setRemark("User-Add");
        newProject.setEditedOn(LocalDateTime.now());
//        newProject.setEditedByUser(username);
        newProject.setRecordOn(LocalDateTime.now());

        newProject.setStartTime(startTime);
        newProject.setEndTime(endTime);
        newProject.setDate(date);
        newProject.setStatus("Active");


        defenseSchedRepository.save(newProject);
    }

    public void saveUnDefenseSchedule(List<ProjectWithInstructorsDTO> unscheduledProjects) {

        for (ProjectWithInstructorsDTO assignUnSlot : unscheduledProjects) {
            DefenseSchedule defenseSchedule = new DefenseSchedule();

            System.out.println(assignUnSlot);

            // https://www.quora.com/How-do-I-generate-a-unique-ID-in-Java
            defenseSchedule.setDefenseScheduleId(UUID.randomUUID().toString());
            defenseSchedule.setStatus("Non-Active");
            defenseSchedule.setRemark("Auto-Ungenerated schedule");
            defenseSchedule.setRecordOn(LocalDateTime.now());

            Project project = projectRepository.findByProjectId(assignUnSlot.getProject().getProjectId());
            String projectId = project.getProjectId();
            System.out.println(projectId);
            defenseSchedule.setProjectId(projectId);

            defenseSchedRepository.save(defenseSchedule);

            duplicateUnScheduleProject(projectId);

        }
    }

    public void duplicateUnScheduleProject(String projectId) {

        DefenseSchedule newProject = new DefenseSchedule();

//        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        newProject.setDefenseScheduleId(UUID.randomUUID().toString());
        newProject.setProjectId(projectId);
        newProject.setRemark("User-Add");
        newProject.setEditedOn(LocalDateTime.now());
//        newProject.setEditedByUser(username);
        newProject.setRecordOn(LocalDateTime.now());
        newProject.setStatus("Active");


        defenseSchedRepository.save(newProject);
    }



}