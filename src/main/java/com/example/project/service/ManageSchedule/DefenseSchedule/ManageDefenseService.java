package com.example.project.service.ManageSchedule.DefenseSchedule;

import com.example.project.DTO.ManageSchedule.GetProposalScheduleDTO;
import com.example.project.DTO.ManageSchedule.GetProposalUnScheduleDTO;
import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.DTO.ManageSchedule.Preview.StudentDataDTO;
import com.example.project.entity.DefenseSchedule;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ProposalSchedule;
import com.example.project.repository.DefenseSchedRepository;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageDefenseService {

    @Autowired
    DefenseSchedRepository defenseSchedRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectInstructorRoleRepository projectInstructorRoleRepository;

    // delete all
    public boolean deleteAllDefenseSchedule(String semesterYear) {

        List<String> projectIds = projectRepository.findByProjectIdAndSemster(semesterYear);

//        List<String> projectIds = projectRepository.findByProjectIdList();

        System.out.print("🪸List delete project: " + projectIds);

        if(projectIds.isEmpty()) {
            System.out.println("No projects found ");
            return false;
        }

        int deletedCount =  defenseSchedRepository.deleteAllByProgram(projectIds);

        return deletedCount > 0;

    }

    // ====================================== get project =====================================

    // อย่าลืม filter student เเต่มันเป็นเเค่การ get proposal น่าจะไม่ต้องเเล้ว
    // อันนี้น่าจะต้อง filter year
    public Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> getDefenseSchedule(String semesterYear){

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

        System.out.print("🪸List get project: " + ProjectList);

        if(ProjectList.isEmpty()) {

            System.out.println("No projects found" );
            return new HashMap<>();

        }

        List<DefenseSchedule> projectListSchedule = defenseSchedRepository.findByProjectIds(ProjectList);

        System.out.print("projectListSchedule: " + projectListSchedule);


        List<GetProposalScheduleDTO> dtoList = projectListSchedule.stream()
                .map(schedule -> {
                    List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(schedule.getProjectId());

                    List<String> instructorNames = instructors.stream()
                            .filter(instructorRole -> "Advisor".equalsIgnoreCase(instructorRole.getRole()) || "Committee".equalsIgnoreCase(instructorRole.getRole()))
                            .map(instructor -> instructor.getInstructor().getProfessorName())
                            .collect(Collectors.toList());

                    return new GetProposalScheduleDTO(
                            schedule.getDefenseScheduleId(),
                            schedule.getStartTime().toLocalTime(),
                            schedule.getEndTime().toLocalTime(),
                            schedule.getDate(),
                            schedule.getStatus(),
                            schedule.getProjectId(),
                            schedule.getProject().getProgram(),
                            schedule.getProject().getSemester(),
                            schedule.getProject().getProjectTitle(),
                            instructorNames,
                            schedule.getRoomTemp()
                    ); })
                .collect(Collectors.toList());

        dtoList.sort(Comparator.comparing(GetProposalScheduleDTO::getDate).thenComparing(GetProposalScheduleDTO::getStartTime));

        System.out.println("🍭Sorted dto: ");
        for(GetProposalScheduleDTO dto: dtoList) {
            System.out.println("Date: " + dto.getDate() + ", Start Time: " + dto.getStartTime() +
                    ", End Time: " + dto.getEndTime() + ", ProjectId: " + dto.getProjectId());
        };

        Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> groupedData = new LinkedHashMap<>();

        for(GetProposalScheduleDTO dto : dtoList) {

            String date = dto.getDate();

            // ใช้ putIfAbsent ก็ได้
            if(!groupedData.containsKey(date)) {
                groupedData.put(date, new LinkedHashMap<>());
            }

            LocalTime startTime = dto.getStartTime();
            LocalTime endTime = dto.getEndTime();

            Pair<LocalTime, LocalTime> timeSlot = new Pair<>(startTime, endTime);

            if(!groupedData.get(date).containsKey(timeSlot)) {
                groupedData.get(date).put(timeSlot, new ArrayList<>());
            }

            groupedData.get(date).get(timeSlot).add(dto);
        }

        return groupedData;
    }


    // getUnschedule
    // อันนี้น่าจะต้อง filter year
    public List<GetProposalUnScheduleDTO> getDefenseUnSchedule(String semesterYear) {

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
//
//        System.out.print("🪸List get project: " + projectIdsWithMaxSemester);

        if(ProjectList.isEmpty()) {

            System.out.println("No projects found" );
            return new ArrayList<>();

        }

        List<DefenseSchedule> projectListSchedule = defenseSchedRepository.findByProjectIdsUnschedule(ProjectList);

        List<GetProposalUnScheduleDTO> dtoList = projectListSchedule.stream()

                .map(schedule -> {
                    List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(schedule.getProjectId());

                    List<String> instructorNames = instructors.stream()
                            .filter(instructorRole -> "Advisor".equalsIgnoreCase(instructorRole.getRole()) || "Committee".equalsIgnoreCase(instructorRole.getRole()))
                            .map(instructor -> instructor.getInstructor().getProfessorName())
                            .collect(Collectors.toList());

                    return new GetProposalUnScheduleDTO(
                            schedule.getDefenseScheduleId(),
                            schedule.getRemark(),
                            schedule.getStatus(),
                            schedule.getProjectId(),
                            schedule.getProject().getProgram(),
                            schedule.getProject().getSemester(),
                            schedule.getProject().getProjectTitle(),
                            instructorNames
                    ); })
                .collect(Collectors.toList());

        dtoList.sort(Comparator.comparing(GetProposalUnScheduleDTO::getProjectId));

        return dtoList;
    }

    // get proposal perview schdule
    public  List<PreviewProposalDTO> getDataDefensePreviewSchedule(String semesterYear) {

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

        // get project
        List<DefenseSchedule> defenseSchedulesPreview = defenseSchedRepository.findEditProject(ProjectList);

//            for(ProposalSchedule s : proposalSchedulesPreview) {
//                System.out.println("🌻 ProjectId: " + s.getProjectId());
//                System.out.println("Remark: " + s.getRemark());
//                System.out.println("Status: " + s.getStatus());
//                System.out.println("Date: " + s.getDate());
//                System.out.println("StartTime: " + s.getStartTime());
//                System.out.println("EndTime: " + s.getEndTime());
//            }

        //https://howtodoinjava.com/java/sort/stream-sort-with-null-values/?utm_source=chatgpt.com (หัวข้อ 2.2.2)
        List<DefenseSchedule> sortedSchedules = defenseSchedulesPreview.stream()
                .sorted(
                        Comparator.comparing(DefenseSchedule::getDate,
                                        Comparator.nullsLast(Comparator.naturalOrder()))

                                .thenComparing(p -> p.getStartTime() != null ? p.getStartTime().toLocalTime() : null,
                                        Comparator.nullsLast(Comparator.naturalOrder()))

                                .thenComparing(p -> p.getEndTime() != null ? p.getEndTime().toLocalTime() : null,
                                        Comparator.nullsLast(Comparator.naturalOrder()))

                ).collect(Collectors.toList());

        for (DefenseSchedule schedule : sortedSchedules) {
            System.out.println("⭐️ sortedSchedules" + schedule);
        }


        List<PreviewProposalDTO> projectDTO = sortedSchedules.stream()
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

                    List<StudentDataDTO> studentsData = new ArrayList<>();

                    if (project.getStudentProjects() != null && !project.getStudentProjects().isEmpty()) {

                        studentsData = project.getStudentProjects().stream()
                                .filter(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()))
                                .map(s -> new StudentDataDTO(
                                        s.getStudent().getStudentId(),
                                        s.getStudent().getStudentName(),
                                        s.getStudent().getSection(),
                                        s.getStudent().getTrack()
                                )).filter(Objects::nonNull)
                                .collect(Collectors.toList());
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

                    String timeProject = "";
                    String dateFormat = "";

                    if(p.getStartTime() != null && p.getEndTime() != null && p.getDate() != null) {

                        timeProject = p.getStartTime().toLocalTime() + " - " + p.getEndTime().toLocalTime();

                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate startDateConvert = LocalDate.parse(p.getDate(), dateFormatter);
                        //https://www.geeksforgeeks.org/dayofweek-getdisplayname-method-in-java-with-examples/
                        dateFormat = startDateConvert.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", " +
                                startDateConvert.getDayOfMonth() + " " +
                                startDateConvert.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ". " +
                                startDateConvert.getYear();
                    }

                    return new PreviewProposalDTO(
                            project.getProjectId(),
                            project.getProjectTitle(),
                            studentsData,
                            project.getProgram(),
                            project.getSemester(),
                            mapRoleNameInstruct,
                            dateFormat,
                            timeProject,
                            p.getRoom(),
                            p.getStatus(),
                            p.getEditedByUser()
                    );
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return projectDTO;

    }
}