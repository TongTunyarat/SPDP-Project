package com.example.project.service.ManageSchedule.DefenseSchedule;

import com.example.project.DTO.ManageSchedule.EditSchedule.*;
import com.example.project.entity.*;
import com.example.project.repository.DefenseSchedRepository;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.RoomRepository;
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
public class EditDefenseService {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    DefenseSchedRepository defenseSchedRepository;
    @Autowired
    ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    RoomRepository roomRepository;

    // 👀 เอารายการโปรเจคที่สามารถเเก้ไขได้หลังจากยกเลิก 👀
    public List<GetAllEditProposalScheduleDTO> getProjectEditDefense(String semesterYear) {

        List<String> ProjectList = projectRepository.findByProjectIdAndSemster(semesterYear);

//        List<Project> ProjectList = projectRepository.findAll();
//
//        int maxSemester = ProjectList.stream()
//                .mapToInt(i -> Integer.parseInt(i.getSemester())).max().orElse(0);
//
//        System.out.println("🧸maxSemester" + maxSemester);
//
//        List<String> projectIdsWithMaxSemester = ProjectList.stream()
//                .filter(p -> Integer.parseInt(p.getSemester()) == maxSemester)
//                .map(Project::getProjectId)
//                .collect(Collectors.toList());

        // หา project ที่ user สามารถเพิ่มได้
        List<DefenseSchedule> proposalSchedulesUserAdd = defenseSchedRepository.findEditProject(ProjectList);

        List<GetAllEditProposalScheduleDTO> projectDTO = proposalSchedulesUserAdd.stream()
                .filter(p -> "User-Add".equals(p.getRemark()))
                .map(p -> {

                    String projectId = p.getProjectId();
//                    System.out.println("projectId: " + projectId);

                    Project project = projectRepository.findByProjectId(projectId);
//                    System.out.println("--- project: " + project);

//                    project.getStudentProjects().stream()
//                            .forEach(studentProject -> {
//                                System.out.println("🍭Student Name: " + studentProject.getStudent());
//                            });


                    if (project == null) {
                        return null;
                    }

                    List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);

                    List<ProjectInstructorRole> filteredInstructors = instructors.stream()
                            .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                            .collect(Collectors.toList());

                    if (filteredInstructors.isEmpty()) {
//                        System.out.println("filteredInstructors.isEmpty");
                        return null;
                    }
//❗️❗️❗️❗️❗️ ห้ามลบ
                    if (project.getStudentProjects() != null && !project.getStudentProjects().isEmpty()) {
                        boolean hasActive = project.getStudentProjects().stream()
                                .anyMatch(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()));

                        boolean allExited = project.getStudentProjects().stream()
                                .allMatch(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()));

                        if (!hasActive || allExited) {
                            System.out.println("!hasActive || allExited");

                            project.getStudentProjects().stream()
                                    .filter(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()))
                                    .forEach(studentProject -> {
                                        System.out.println("Student Name: " + studentProject.getStudent().getStudentName());
                                    });

                            return null;
                        }
                    } else {
                        System.out.println("No student projects available.");
                        return null;
                    }


                    Map<String, List<String>> mapRoleNameInstruct = new HashMap<>();
                    for (ProjectInstructorRole instructor : filteredInstructors) {

                        String role = instructor.getRole();
                        String name = instructor.getInstructor().getProfessorName();

                        if (!mapRoleNameInstruct.containsKey(role)) {
                            mapRoleNameInstruct.put(role, new ArrayList<>());
                        }

                        mapRoleNameInstruct.get(role).add(name);

                    }

                    return new GetAllEditProposalScheduleDTO(
                            project.getProjectId(),
                            project.getProjectTitle(),
                            project.getProgram(),
                            project.getSemester(),
                            mapRoleNameInstruct,
                            p.getDate(),
                            p.getStartTime(),
                            p.getEndTime(),
                            p.getRoom(),
                            p.getStatus(),
                            p.getEditedByUser()
                    );
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return projectDTO;

    }

    // ดึงค่าสำหรับใน cardpopup
    public List<GetEditProposalScheduleByIdDTO> getProjectEditDefenseByProjectId(String projectId) {

        List<DefenseSchedule> proposalScheduleList = defenseSchedRepository.findEditProjectByProjectId(projectId);

        List<GetEditProposalScheduleByIdDTO> projectDTO = proposalScheduleList.stream()
                .map(p -> {
//                    System.out.println("projectId: " + projectId);

                    Project project = projectRepository.findByProjectId(projectId);
//                    System.out.println("--- project: " + project);

                    if (project == null) {
                        return null;
                    }

                    List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);

                    List<ProjectInstructorRole> filteredInstructors = instructors.stream()
                            .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                            .collect(Collectors.toList());

                    if (filteredInstructors.isEmpty()) {
//                        System.out.println("filteredInstructors.isEmpty");
                        return null;
                    }


                    Map<String, List<String>> mapRoleNameInstruct = new HashMap<>();
                    for (ProjectInstructorRole instructor : filteredInstructors) {

                        String role = instructor.getRole();
                        String name = instructor.getInstructor().getProfessorName();

                        if (!mapRoleNameInstruct.containsKey(role)) {
                            mapRoleNameInstruct.put(role, new ArrayList<>());
                        }

                        mapRoleNameInstruct.get(role).add(name);

                    }

                    return new GetEditProposalScheduleByIdDTO(
                            project.getProjectId(),
                            project.getProjectTitle(),
                            project.getProgram(),
                            project.getSemester(),
                            mapRoleNameInstruct,
                            p.getDate(),
                            p.getStartTime(),
                            p.getEndTime(),
                            p.getRoom(),
                            p.getStatus(),
                            p.getEditedByUser()
                    );
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return projectDTO;
    }

    // เเก้ไขตารางการนำเสนอ
    public ProposalConflictDTO editDefenseByprojectId(String projectId, String program, String startDate, String startTime, String endTime, String roomNumber) {

        // https://www.geeksforgeeks.org/how-to-convert-a-string-to-a-localdate-in-java/
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateConvert = LocalDate.parse(startDate, dateFormatter);

        // https://stackoverflow.com/questions/30788369/coverting-string-to-localtime-with-without-nanoofseconds
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTimeConvert = LocalTime.parse(startTime,timeFormatter);
        LocalTime endTimeConvert = LocalTime.parse(endTime,timeFormatter);

        LocalDateTime startDateTimeInput = LocalDateTime.of(startDateConvert, startTimeConvert);
        LocalDateTime endDateTimeInput = LocalDateTime.of(startDateConvert, endTimeConvert);

        System.out.println("🍭 Check Date: ");
        System.out.println("startDateTimeInput: " + startDateTimeInput);
        System.out.println("endDateTimeInput: " + endDateTimeInput);

        DefenseSchedule project = defenseSchedRepository.findEditByProjectId(projectId);
        String originalStatus = project.getStatus();
        project.setStatus("In-Progress");
        defenseSchedRepository.save(project);


        List<DefenseSchedule> defenseScheduleList = defenseSchedRepository.findByAllAndStatusActive();

        List<RoomColflictDTO> roomConflicts = defenseScheduleList.stream()
                .filter(p -> p.getDate() != null)
                .filter(p -> p.getStartTime() != null && p.getEndTime() != null)
                .filter(p -> p.getRoom() != null)
                .filter(schedule -> {
                    LocalDateTime existStartDateTime = LocalDateTime.of(LocalDate.parse(schedule.getDate()), schedule.getStartTime().toLocalTime());
                    LocalDateTime existEndDateTime = LocalDateTime.of(LocalDate.parse(schedule.getDate()), schedule.getEndTime().toLocalTime());

                    System.out.println("🐍 projectId: " + schedule.getProjectId());
                    System.out.println("existStartDateTime: " + existStartDateTime);
                    System.out.println("existEndDateTime: " + existEndDateTime);

                    boolean isConflict = roomNumber.equals(schedule.getRoom()) && schedule.getDate().equals(startDate)
                            && timeOverlap(startDateTimeInput, endDateTimeInput, existStartDateTime, existEndDateTime);

                    if(isConflict) {
                        System.out.println("🚨 Conflict detected!");
                        System.out.println("🐍 projectId: " + schedule.getProjectId());
                        System.out.println("existStartDateTime: " + existStartDateTime);
                        System.out.println("existEndDateTime: " + existEndDateTime);
                    }

                    return isConflict;
                }).map(schedule -> new RoomColflictDTO(
                        schedule.getDate(),
                        schedule.getRoom(),
                        schedule.getProjectId(),
                        schedule.getStartTime().toLocalTime(),
                        schedule.getEndTime().toLocalTime()
                )).collect(Collectors.toList());

        boolean hasRoomConflict = !roomConflicts.isEmpty();
        System.out.println("roomConflicts: " + roomConflicts);

        Map<String, List<Pair<String, Pair<LocalDateTime, LocalDateTime>>>> instructorScheduleMap = new HashMap<>();

        defenseScheduleList.stream()
                .filter(p -> p.getDate() != null)
                .filter(p -> p.getStartTime() != null && p.getEndTime() != null)
//                .filter(p -> p.getRoom() != null)
                .filter(schedule -> schedule.getDate().equals(startDate))
                .forEach(p -> {

                    System.out.println("🍭 Check  equals Date: " + p.getDate());
                    System.out.println("🐍 projectId: " + p.getProjectId());

                    LocalDateTime existStartDateTime = LocalDateTime.of(LocalDate.parse(p.getDate()), p.getStartTime().toLocalTime());
                    LocalDateTime existEndDateTime = LocalDateTime.of(LocalDate.parse(p.getDate()), p.getEndTime().toLocalTime());
                    System.out.println("existStartDateTime: " + existStartDateTime);
                    System.out.println("existEndDateTime: " + existEndDateTime);

                    String currentProject = p.getProjectId();
                    List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(currentProject);

                    List<ProjectInstructorRole> filteredInstructors = instructors.stream()
                            .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                            .collect(Collectors.toList());

                    System.out.println("👩🏻‍🏫 filteredInstructors: ");
                    for (ProjectInstructorRole instructor : filteredInstructors) {
                        System.out.println(instructor.getInstructor().getProfessorName());
                    }

                    for (ProjectInstructorRole instrucotr : filteredInstructors) {

                        String instructorName = instrucotr.getInstructor().getProfessorName();

                        if (!instructorScheduleMap.containsKey(instructorName)) {
                            instructorScheduleMap.put(instructorName, new ArrayList<>());
                        }

                        Pair<LocalDateTime, LocalDateTime> timePair = new Pair<>(existStartDateTime, existEndDateTime);
                        Pair<String, Pair<LocalDateTime, LocalDateTime>> projectTimePair = new Pair<>(currentProject, timePair);
                        instructorScheduleMap.get(instructorName).add(projectTimePair);

                    }
                });

        System.out.println("👩‍🏫 instructorScheduleMap (All Projects): " + instructorScheduleMap);

        List<InstructorConflictDTO> instructorConflict = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId)
                .stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(newInstructor -> instructorScheduleMap.containsKey(newInstructor.getInstructor().getProfessorName()))
                .flatMap(newInstructor -> {

                    String instructorName = newInstructor.getInstructor().getProfessorName();
                    System.out.println("👩🏻‍🏫 instructorName: " + instructorName);

                    List<Pair<String, Pair<LocalDateTime, LocalDateTime>>> existingProjectTimes = instructorScheduleMap.get(instructorName);
                    System.out.println("🕰️ existingProjectTimes: " + existingProjectTimes);

                    return existingProjectTimes.stream()
                            .filter(projectTimePair -> {
                                Pair<LocalDateTime, LocalDateTime> timePair = projectTimePair.b;
                                return timeOverlap(startDateTimeInput, endDateTimeInput, timePair.a, timePair.b);
                            })
                            .map(projectTimePair -> {
                                String existingProject = projectTimePair.a;
                                Pair<LocalDateTime, LocalDateTime> timePair = projectTimePair.b;
                                System.out.println("🤯ชนกันที่เวลา: " + timePair.a + " - " + timePair.b);
//                                String dateConflict = defenseSchedRepository.findByProjectId(existingProject).getDate();

                                 List<DefenseSchedule> existingSchedule = defenseSchedRepository.findByProjectAllId(existingProject);
                                 String dateConflict = existingSchedule.stream()
                                        .filter(s -> "Active".equalsIgnoreCase(s.getStatus()))
                                        .map(DefenseSchedule::getDate)
                                        .findFirst()
                                        .orElse(null);

                                return new InstructorConflictDTO(instructorName, existingProject, dateConflict, timePair.a.toLocalTime(), timePair.b.toLocalTime());
                            });


                }).collect(Collectors.toList());

        boolean hasInstructorConflict = !instructorConflict.isEmpty();

        System.out.println("🌻 instructorConflict " + instructorConflict);

        if(!roomConflicts.isEmpty() || !instructorConflict.isEmpty()) {
            project.setStatus("Active");
            defenseSchedRepository.save(project);
        }

        boolean hasErrorSaveData = false ;
        //❗️❗️❗️❗️❗️ ห้ามลบ
        if(roomConflicts.isEmpty() && instructorConflict.isEmpty()) {
            try {
                saveProposalSchedule(projectId, program, startDate, startTime, endTime, roomNumber);
            } catch (Exception e) {
                hasErrorSaveData = true ;
            }
        }

        return new ProposalConflictDTO(hasErrorSaveData, roomConflicts, hasRoomConflict, instructorConflict, hasInstructorConflict);
    }


    public boolean timeOverlap(LocalDateTime newStartTime, LocalDateTime newEndTime, LocalDateTime existStartTime, LocalDateTime existEndTime) {

        // ex. 11:00 - 14:00, 13:00 - 15:00
        // F || F == F -> retrun true;
        boolean noOverlap = newEndTime.isBefore(existStartTime) ||  newEndTime.equals(existStartTime) || newStartTime.isAfter(existEndTime) || newStartTime.equals(existEndTime);

        return !noOverlap;
    }

    public void saveProposalSchedule(String projectId, String program, String startDate, String startTime, String endTime, String roomNumber) {

        DefenseSchedule project = defenseSchedRepository.findEditByProjectId(projectId);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // https://www.geeksforgeeks.org/how-to-convert-a-string-to-a-localdate-in-java/
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateConvert = LocalDate.parse(startDate, dateFormatter);

        // https://stackoverflow.com/questions/30788369/coverting-string-to-localtime-with-without-nanoofseconds
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTimeConvert = LocalTime.parse(startTime,timeFormatter);
        LocalTime endTimeConvert = LocalTime.parse(endTime,timeFormatter);

        LocalDateTime startDateTimeInput = LocalDateTime.of(startDateConvert, startTimeConvert);
        LocalDateTime endDateTimeInput = LocalDateTime.of(startDateConvert, endTimeConvert);

        project.setStatus("Active");
        project.setStartTime(startDateTimeInput);
        project.setEndTime(endDateTimeInput);
        project.setDate(startDate);
        project.setEditedOn(LocalDateTime.now());
        project.setEditedByUser(username);
        project.setRecordOn(LocalDateTime.now());

        Room room = roomRepository.findByRoomNumber(roomNumber);
        String roomID = room.getRoomNumber();
        System.out.println(roomID);
        project.setRoom(roomID);

        defenseSchedRepository.save(project);

        DefenseSchedule projectGenerate = defenseSchedRepository.findByProjectId(projectId);
        projectGenerate.setStatus("Edit-Active");
        defenseSchedRepository.save(projectGenerate);
    }

}