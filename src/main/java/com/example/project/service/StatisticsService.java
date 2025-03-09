package com.example.project.service;

import com.example.project.DTO.Dashboard.GradingStatisticsDTO;
import com.example.project.DTO.Dashboard.EvaluationStatusResponse;
import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    private ScoringPeriodsRepository scoringPeriodsRepository;

    @Autowired
    public StatisticsService(
            ProjectRepository projectRepository,
            GradingProposalEvaluationRepository gradingProposalEvaluationRepository,
            GradingDefenseEvaluationRepository defenseEvaluationRepository) {
        this.projectRepository = projectRepository;
        this.gradingProposalEvaluationRepository = gradingProposalEvaluationRepository;
        this.gradingDefenseEvaluationRepository = defenseEvaluationRepository;
    }

    public List<String> getAllAcademicYears() {
        return projectRepository.findAll()
                .stream()
                .map(Project::getSemester) // ดึงเฉพาะค่าปีการศึกษา
                .distinct()
                .collect(Collectors.toList());
    }

    // ----- Grade Proposal and Grade Defense ----- //
    public GradingStatisticsDTO getGradingStatistics(String year) {
        System.out.println("📆 year service: " + year);
        // ค้นหาทุกโปรเจกต์ที่ตรงกับปีที่เลือก
        List<Project> projects = projectRepository.findBySemester(year);  // ฟังก์ชันใน ProjectRepository
        System.out.println("👽 projects: " + projects.size());

        // คำนวณ totalStudent โดยการใช้ projectId ที่ได้จาก projects
        List<String> studentIds = new ArrayList<>();
        List<Project> projectIds = new ArrayList<>();

        for (Project project : projects) {
            projectIds.add(project);  // Add projectId to projectIds list

            List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(project.getProjectId());
            for (StudentProject studentProject : studentProjects) {
                studentIds.add(studentProject.getStudent().getStudentId());
            }
        }
        long totalStudent = studentIds.size();  // นับจำนวน student ที่ไม่ซ้ำกัน
        long totalProjects = projectIds.size();

        System.out.println("totalStudent: " + totalStudent);
        System.out.println("totalProjects: " + totalProjects);

        // คำนวณ completedProposals โดยการใช้ projectId จาก projectIds
        long completedProposals = gradingProposalEvaluationRepository.countDistinctProjectByProjectIn(projectIds);
        System.out.println("Completed proposals: " + completedProposals);

        // คำนวณ completedDefenses โดยการใช้ projectId จาก projectIds
        long completedDefenses = gradingDefenseEvaluationRepository.countDistinctProjectIdByProjectIdIn(projectIds);
        System.out.println("Completed defenses: " + completedDefenses);

        // ค้นหานักศึกษาจาก studentIds ที่ได้จากการกรอง
        List<Student> students = studentRepository.findByStudentIdIn(new ArrayList<>(studentIds));
        System.out.println("students: " + students.size());

        // กรองข้อมูลการประเมินข้อเสนอ (Proposal) และการประเมินการป้องกัน (Defense)
        List<GradingProposalEvaluation> proposalGrade = gradingProposalEvaluationRepository.findByProjectIn(projectIds);  // ใช้ projectIdIn
        List<GradingDefenseEvaluation> defenseGrade = gradingDefenseEvaluationRepository.findByProjectIdIn(projectIds);  // ใช้ projectIdIn
        System.out.println("🎞️ proposalGrade: " + proposalGrade.size());
        System.out.println("🎞️ defenseGrade: " + defenseGrade.size());

        // ส่งข้อมูลทั้งหมดกลับไป
        return new GradingStatisticsDTO(totalStudent, completedProposals, completedDefenses, students, proposalGrade, defenseGrade);
    }

    // ----- Proposal Evaluation ----- //
    public EvaluationStatusResponse getProposalEvaluationStatus(String param, String year) {
        List<Project> allProjects;

        // ตรวจสอบค่าของ param
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findBySemester(year);
            System.out.println("allProjects: " + allProjects.size());
        } else {
            // allProjects = projectRepository.findByProgram(param);
            allProjects = projectRepository.findBySemesterAndProgram(year, param);
            System.out.println("allProjects: " + allProjects.size());
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

    // ----- Defense Evaluation ----- //
    public EvaluationStatusResponse getDefenseEvaluationStatus(String param, String year) {
        List<Project> allProjects;

        // ตรวจสอบค่าของ param
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findBySemester(year);
            System.out.println("allProjects: " + allProjects.size());
        } else {
            // allProjects = projectRepository.findByProgram(param);
            allProjects = projectRepository.findBySemesterAndProgram(year, param);
            System.out.println("allProjects: " + allProjects.size());
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

    // ----- Poster Evaluation ----- //
    public EvaluationStatusResponse getPosterEvaluationStatus(String param, String year) {
        List<Project> allProjects;

        // ตรวจสอบค่าของ param
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findBySemester(year);
            System.out.println("allProjects: " + allProjects.size());
        } else {
            allProjects = projectRepository.findBySemesterAndProgram(year, param);
            System.out.println("allProjects: " + allProjects.size());
        }

        int totalProjects = allProjects.size();
        int completedProjects = 0;

        for (Project project : allProjects) {
            boolean isProjectEvaluated = true; // สมมติว่าเสร็จแล้วก่อนตรวจสอบ

            Project currentProject = projectRepository.findByProjectId(project.getProjectId());

            // ดึง Instructor ที่มีบทบาทเป็น Poster-Committee หรือ Committee ในโครงการนี้
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

    // --------- Grade Distribute ------------ //
    public Map<String, Integer> getGradeDistribution(String program, String year, String evaType) {
        List<Project> allProjects;

        // ดึงข้อมูลโครงงานตาม program และ year
        if ("All".equalsIgnoreCase(program)) {
            allProjects = projectRepository.findBySemester(year);
        } else {
            allProjects = projectRepository.findBySemesterAndProgram(year, program);
        }

        System.out.println("allProjects [grade distribute]: " + allProjects.size());

        // ใช้ HashMap ในการเก็บเฉพาะเกรดที่มีอยู่จริง
        Map<String, Integer> gradeDistribution = new HashMap<>();

        for (Project project : allProjects) {
            List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(project.getProjectId());

            for (StudentProject studentProject : studentProjects) {
                String studentId = studentProject.getStudent().getStudentId();
                String grade = null;

                try {
                    if ("Proposal Evaluation".equalsIgnoreCase(evaType)) {
                        grade = gradingProposalEvaluationRepository
                                .findGradeResultByProjectAndStudent_StudentId(project, studentId)
                                .getGradeResult();
                    } else if ("Defense Evaluation".equalsIgnoreCase(evaType)) {
                        grade = gradingDefenseEvaluationRepository
                                .findGradeResultByProjectIdAndStudentId_StudentId(project, studentId)
                                .getGradeResult();
                    } else if ("Poster Exhibition".equalsIgnoreCase(evaType)) {
                        return new HashMap<>(); // ไม่มีการให้เกรดสำหรับ Poster Exhibition
                    } else {
                        throw new IllegalArgumentException("Invalid evaType: " + evaType);
                    }
                } catch (NullPointerException e) {
                    // ถ้าเกิด NullPointerException ให้ถือว่าเป็นเกรด "I"
                    grade = "I";
                }

                // ถ้า grade เป็น null หรือว่าง ให้ถือว่าเป็น "I"
                if (grade == null || grade.isEmpty()) {
                    grade = "I";
                }

                // เพิ่มเข้า gradeDistribution
                gradeDistribution.put(grade, gradeDistribution.getOrDefault(grade, 0) + 1);
            }
        }

        return gradeDistribution;
    }

    public List<ScoringPeriods> getAllScoringPeriods() {
        return scoringPeriodsRepository.findAll();
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
