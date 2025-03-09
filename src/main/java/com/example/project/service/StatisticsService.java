package com.example.project.service;

import com.example.project.DTO.Dashboard.GradingStatisticsDTO;
import com.example.project.DTO.Dashboard.EvaluationStatusResponse;
import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;
    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    public StatisticsService(
            ProjectRepository projectRepository,
            GradingProposalEvaluationRepository gradingProposalEvaluationRepository,
            GradingDefenseEvaluationRepository defenseEvaluationRepository) {
        this.projectRepository = projectRepository;
        this.gradingProposalEvaluationRepository = gradingProposalEvaluationRepository;
        this.gradingDefenseEvaluationRepository = defenseEvaluationRepository;
    }

    public GradingStatisticsDTO getGradingStatistics() {
        long totalStudent = studentRepository.count();
        long completedProposals = gradingProposalEvaluationRepository.countDistinctProjectIds();
        long completedDefenses = gradingDefenseEvaluationRepository.countDistinctProjectIds();
        List<Student> student = studentRepository.findAll();
        List<GradingProposalEvaluation> proposalGrade = gradingProposalEvaluationRepository.findAll();
        List<GradingDefenseEvaluation> defenseGrade = gradingDefenseEvaluationRepository.findAll();

        System.out.println("Total groups: " + totalStudent);
        System.out.println("Completed proposals: " + completedProposals);
        System.out.println("Completed defenses: " + completedDefenses);

        return new GradingStatisticsDTO(totalStudent, completedProposals, completedDefenses, student, proposalGrade, defenseGrade);
    }

    public EvaluationStatusResponse getProposalEvaluationStatus(String param) {
        List<Project> allProjects;

        // ตรวจสอบค่าของ param
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findAll();
        } else {
            allProjects = projectRepository.findByProgram(param); // ดึงโครงการตามโปรแกรม
        }

        int totalProjects = allProjects.size();
        int completedProjects = 0;

        for (Project project : allProjects) {
            boolean isProjectEvaluated = true; // สมมติว่าเสร็จแล้วก่อนตรวจสอบ

            Project currentProject = projectRepository.findByProjectId(project.getProjectId());

            // ดึง Instructor ที่มีบทบาทเป็น Advisor หรือ Committee ในโครงการนี้
            List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole(currentProject);

            // เช็คถ้า instructors เป็น null หรือไม่มีรายการเลย
            if (instructors == null || instructors.isEmpty()) {
                System.out.println("⚠️ Project: " + project.getProjectId() + " | No assigned instructors for evaluation.");
                isProjectEvaluated = false; // ถือว่ายังไม่ประเมิน
            } else {
                System.out.println("Project: " + project.getProjectId() + " | Instructors: " + instructors);

                for (ProjectInstructorRole instructorRole : instructors) {
                    if (instructorRole.getRole().equals("Advisor") || instructorRole.getRole().equals("Committee")) {
                        int totalEvaluations = proposalEvaluationRepository.countByProjectInstructorRoleAndProject(instructorRole, project);
                        int totalStudents = project.getStudentProjects().size();

                        if (totalEvaluations < totalStudents) {
                            isProjectEvaluated = false;
                            break;
                        }
                    }
                }
            }

            if (isProjectEvaluated) {
                completedProjects++;
                System.out.println("✅ Project: " + project.getProjectId() + " | Evaluation Completed!");
            } else {
                System.out.println("❌ Project: " + project.getProjectId() + " | Evaluation Not Completed");
            }
        }

        return new EvaluationStatusResponse(totalProjects, completedProjects);
    }

    public EvaluationStatusResponse getDefenseEvaluationStatus(String param) {
        List<Project> allProjects;

        // ตรวจสอบค่าของ param
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findAll();
        } else {
            allProjects = projectRepository.findByProgram(param); // ดึงโครงการตามโปรแกรม
        }

        int totalProjects = allProjects.size();
        int completedProjects = 0;

        for (Project project : allProjects) {
            boolean isProjectEvaluated = true; // สมมติว่าเสร็จแล้วก่อนตรวจสอบ

            Project currentProject = projectRepository.findByProjectId(project.getProjectId());

            // ดึง Instructor ที่มีบทบาทเป็น Advisor หรือ Committee ในโครงการนี้
            List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole(currentProject);

            // เช็คถ้า instructors เป็น null หรือไม่มีรายการเลย
            if (instructors == null || instructors.isEmpty()) {
                System.out.println("⚠️ Project: " + project.getProjectId() + " | No assigned instructors for evaluation.");
                isProjectEvaluated = false; // ถือว่ายังไม่ประเมิน
            } else {
                System.out.println("Project: " + project.getProjectId() + " | Instructors: " + instructors);

                for (ProjectInstructorRole instructorRole : instructors) {
                    if (instructorRole.getRole().equals("Advisor") || instructorRole.getRole().equals("Committee")) {
                        int totalEvaluations = defenseEvaluationRepository.countByDefenseInstructorIdAndProjectId(instructorRole, project);
                        int totalStudents = project.getStudentProjects().size();

                        if (totalEvaluations < totalStudents) {
                            isProjectEvaluated = false;
                            break;
                        }
                    }
                }
            }

            if (isProjectEvaluated) {
                completedProjects++;
                System.out.println("✅ Project: " + project.getProjectId() + " | Evaluation Completed!");
            } else {
                System.out.println("❌ Project: " + project.getProjectId() + " | Evaluation Not Completed");
            }
        }

        return new EvaluationStatusResponse(totalProjects, completedProjects);
    }

    public EvaluationStatusResponse getPosterEvaluationStatus(String param) {
        List<Project> allProjects;

        // ตรวจสอบค่าของ param
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findAll();
        } else {
            allProjects = projectRepository.findByProgram(param); // ดึงโครงการตามโปรแกรม
        }

        int totalProjects = allProjects.size();
        int completedProjects = 0;

        for (Project project : allProjects) {
            boolean isProjectEvaluated = true; // สมมติว่าเสร็จแล้วก่อนตรวจสอบ

            Project currentProject = projectRepository.findByProjectId(project.getProjectId());

            // ดึง Instructor ที่มีบทบาทเป็น Advisor หรือ Committee ในโครงการนี้
            List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole(currentProject);

            // เช็คถ้า instructors เป็น null หรือไม่มีรายการเลย
            if (instructors == null || instructors.isEmpty()) {
                System.out.println("⚠️ Project: " + project.getProjectId() + " | No assigned instructors for evaluation.");
                isProjectEvaluated = false; // ถือว่ายังไม่ประเมิน
            } else {
                System.out.println("Project: " + project.getProjectId() + " | Instructors: " + instructors);

                for (ProjectInstructorRole instructorRole : instructors) {
                    if (instructorRole.getRole().equals("Poster-Committee") || instructorRole.getRole().equals("Committee")) {
                        int totalEvaluations = defenseEvaluationRepository.countByDefenseInstructorIdAndProjectId(instructorRole, project);
                        int totalStudents = project.getStudentProjects().size();

                        if (totalEvaluations < totalStudents) {
                            isProjectEvaluated = false;
                            break;
                        }
                    }
                }
            }

            if (isProjectEvaluated) {
                completedProjects++;
                System.out.println("✅ Project: " + project.getProjectId() + " | Evaluation Completed!");
            } else {
                System.out.println("❌ Project: " + project.getProjectId() + " | Evaluation Not Completed");
            }
        }

        return new EvaluationStatusResponse(totalProjects, completedProjects);
    }

}


// if param == All
// List<Project> allProject = projectRepository.findAll()
// Long totalProject = allProject.size()
// หาจำนวน instructor ในแต่ละ Project จาก projectInstructorRole *เอาแค่ role [Advisor, Committee]
//                public class ProjectInstructorRole {
//                    private String instructorId;
//                    private LocalDateTime assignDate;
//                    private String role;
//                    private Project projectIdRole;
//                    private Instructor instructor;
//                    private List<DefenseEvaluation> defenseEvaluations;
//                    private List<PosterEvaluation> posterEvaluations;
//                    private List<ProposalEvaluation> proposalEvaluations;
//                }
// ตรวจสอบว่า instructor คนนั้นให้คะแนนครบทุกนักศึกษารึยังในแต่ละ Project จาก ProposalEvaluation
// สมมตินะว่า Project001 มีนักศึกษา 3 คน มี Advisor 1 คน Committee 2 คน จะนับว่า project ประเมินครบแล้วโดย advisor และ committee ต้องประเมินครบทุกนักศึกษา
// อยาก return (จำนวน project ทั้งหมด, จำนวน project ที่ประเมินครบแล้ว)

// else if param == ICT
// ทำเหมือนของ All แต่ Project ต้องเช็คว่ามี program == ICT
// else if param == DST
// ทำเหมือนของ All แต่ Project ต้องเช็คว่ามี program == DST
