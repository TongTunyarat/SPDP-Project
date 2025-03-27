package com.example.project.service.ManageSchedule;

import com.example.project.DTO.ManageSchedule.EditSchedule.GetAllEditProposalScheduleDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ProposalSchedule;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.ProposalSchedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EditProposalService {

    @Autowired
    ProposalSchedRepository proposalSchedRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public List<GetAllEditProposalScheduleDTO>  getProjectEditProposal() {

        List<Project> ProjectList = projectRepository.findAll();

        int maxSemester = ProjectList.stream()
                .mapToInt(i -> Integer.parseInt(i.getSemester())).max().orElse(0);

        System.out.println("🧸maxSemester" + maxSemester);

        List<String> projectIdsWithMaxSemester = ProjectList.stream()
                .filter(p -> Integer.parseInt(p.getSemester()) == maxSemester)
                .map(Project::getProjectId)
                .collect(Collectors.toList());

        // หา project ที่ user สามารถเพิ่มได้
        List<ProposalSchedule> proposalSchedulesUserAdd = proposalSchedRepository.findEditProject(projectIdsWithMaxSemester);

        List<GetAllEditProposalScheduleDTO> projectDTO = proposalSchedulesUserAdd.stream()
                .filter(p -> "User-Add".equals(p.getRemark()))
                .map(p -> {

                    String projectId = p.getProjectId();
                    System.out.println("projectId: " + projectId);

                    Project project = projectRepository.findByProjectId(projectId);
                    System.out.println("--- project: " + project);

                    project.getStudentProjects().stream()
                            .forEach(studentProject -> {
                                System.out.println("🍭Student Name: " + studentProject.getStudent());
                            });


                    if (project == null) {
                        return null;
                    }

                    List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);

                    List<ProjectInstructorRole> filteredInstructors = instructors.stream()
                            .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) ||"Committee".equalsIgnoreCase(i.getRole()))
                            .collect(Collectors.toList());

                    if(filteredInstructors.isEmpty()) {
                        System.out.println("filteredInstructors.isEmpty");
                        return null;
                    }
//❗️❗️❗️❗️❗️ ห้ามลบ
//                    if (project.getStudentProjects() != null && !project.getStudentProjects().isEmpty()) {
//                        boolean hasActive = project.getStudentProjects().stream()
//                                .anyMatch(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()));
//
//                        boolean allExited = project.getStudentProjects().stream()
//                                .allMatch(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()));
//
//                        if (!hasActive || allExited) {
//                            System.out.println("!hasActive || allExited");
//
//                            project.getStudentProjects().stream()
//                                    .filter(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()))
//                                    .forEach(studentProject -> {
//                                        System.out.println("Student Name: " + studentProject.getStudent().getStudentName());
//                                    });
//
//                            return null;
//                        }
//                    } else {
//                        System.out.println("No student projects available.");
//                    }


                    Map<String, List<String>> mapRoleNameInstruct = new HashMap<>();
                    for (ProjectInstructorRole instructor : filteredInstructors) {

                        String role = instructor.getRole();
                        String name = instructor.getInstructor().getProfessorName();

                        if(!mapRoleNameInstruct.containsKey(role)) {
                            mapRoleNameInstruct.put(role, new ArrayList<>());
                        }

                        mapRoleNameInstruct.get(role).add(name);

                    }


                    return new GetAllEditProposalScheduleDTO (
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

}
