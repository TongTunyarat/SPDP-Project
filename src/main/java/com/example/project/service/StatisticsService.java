package com.example.project.service;

import com.example.project.DTO.Dashboard.GradingStatisticsDTO;
import com.example.project.DTO.Dashboard.EvaluationStatusResponse;
import com.example.project.DTO.Dashboard.TeacherScoringDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private PosterEvaRepository posterEvaRepository;

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
//    public EvaluationStatusResponse getPosterEvaluationStatus(String param, String year) {
//        List<Project> allProjects;
//
//        // ตรวจสอบค่าของ param
//        if ("All".equalsIgnoreCase(param)) {
//            allProjects = projectRepository.findBySemester(year);
//            System.out.println("allProjects: " + allProjects.size());
//        } else {
//            allProjects = projectRepository.findBySemesterAndProgram(year, param);
//            System.out.println("allProjects: " + allProjects.size());
//        }
//
//        int totalProjects = allProjects.size();
//        int completedProjects = 0;
//
//        for (Project project : allProjects) {
//            boolean isProjectEvaluated = true; // สมมติว่าเสร็จแล้วก่อนตรวจสอบ
//
//            Project currentProject = projectRepository.findByProjectId(project.getProjectId());
//
//            // ดึง Instructor ที่มีบทบาทเป็น Poster-Committee หรือ Committee ในโครงการนี้
//            List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole(currentProject);
//
//            // เช็คถ้า instructors เป็น null หรือไม่มีรายการเลย
//            if (instructors == null || instructors.isEmpty()) {
//                System.out.println("⚠️ Project: " + project.getProjectId() + " | No assigned instructors for evaluation.");
//                isProjectEvaluated = false; // ถือว่ายังไม่ประเมิน
//            } else {
//                System.out.println("Project: " + project.getProjectId() + " | Instructors: " + instructors);
//
//                for (ProjectInstructorRole instructorRole : instructors) {
//                    if (instructorRole.getRole().equals("Poster-Committee") || instructorRole.getRole().equals("Committee")) {
////                        ProjectInstructorRole instructorRole =
//                        int totalEvaluations = posterEvaRepository.countByInstructorIdPosterAndProjectIdPoster(instructorRole, project);
//                        int totalStudents = project.getStudentProjects().size();
//
//                        if (totalEvaluations < totalStudents) {
//                            isProjectEvaluated = false;
//                            break;
//                        }
//                    }
//                }
//            }
//
//            if (isProjectEvaluated) {
//                completedProjects++;
//                System.out.println("✅ Project: " + project.getProjectId() + " | Evaluation Completed!");
//            } else {
//                System.out.println("❌ Project: " + project.getProjectId() + " | Evaluation Not Completed");
//            }
//        }
//
//        return new EvaluationStatusResponse(totalProjects, completedProjects);
//    }
    public EvaluationStatusResponse getPosterEvaluationStatus(String param, String year) {
        List<Project> allProjects;

        // กรองโปรเจกต์ตาม param (All หรือ Program)
        if ("All".equalsIgnoreCase(param)) {
            allProjects = projectRepository.findBySemester(year);
        } else {
            allProjects = projectRepository.findBySemesterAndProgram(year, param);
        }

        int totalProjects = allProjects.size();
        int completedProjects = 0;

        for (Project project : allProjects) {
            Project currentProject = projectRepository.findByProjectId(project.getProjectId());

            // นักศึกษาทั้งหมดในโปรเจกต์นี้
//            int totalStudents = currentProject.getStudentProjects().size();

            // ดึง instructor ทั้งหมดในโปรเจกต์
            List<ProjectInstructorRole> allRoles = projectInstructorRoleRepository.findByProjectIdRole(currentProject);

            // กรองเฉพาะ instructor ที่มีบทบาท Poster-Committee หรือ Committee
            List<ProjectInstructorRole> evaluators = allRoles.stream()
                    .filter(role -> "Poster-Committee".equals(role.getRole()) || "Committee".equals(role.getRole()))
                    .toList();

            if (evaluators.isEmpty()) {
                System.out.println("⚠️ Project: " + project.getProjectId() + " | No evaluators assigned.");
                continue; // ข้ามโปรเจกต์นี้
            }

            int expectedEvaluations = evaluators.size();
            int actualEvaluations = 0;

            // รวมจำนวนใบประเมินจากอาจารย์แต่ละคน
            for (ProjectInstructorRole evaluator : evaluators) {
                int evalCount = posterEvaRepository.countByInstructorIdPosterAndProjectIdPoster(evaluator, currentProject);
                actualEvaluations += evalCount;
            }

            if (actualEvaluations >= expectedEvaluations) {
                completedProjects++;
                System.out.println("✅ Project: " + project.getProjectId() + " | Evaluation Completed!");
            } else {
                System.out.println("❌ Project: " + project.getProjectId() + " | Incomplete. Expected: "
                        + expectedEvaluations + ", Found: " + actualEvaluations);
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

    public List<ScoringPeriods> getAllScoringPeriods(String year) {
//        String currentYear = String.valueOf(LocalDate.now().getYear());
        return scoringPeriodsRepository.findByYear(year);
    }

    public Map<String, Long> getStudentProposalEvaluationStatus(String evaType, String year, String program) {
        // Filter projects by year and program
        List<StudentProject> studentProjects;

        if ("All".equalsIgnoreCase(program)) {
            studentProjects = studentProjectRepository.findByProject_Semester(year);
        } else {
            studentProjects = studentProjectRepository.findByProject_SemesterAndProject_Program(year, program);
        }

        long totalStudents = studentProjects.size();
        long completed = 0;
        long partial = 0;
        long notEvaluated = 0;

        System.out.println("Total students: " + totalStudents);

        for (StudentProject studentProject : studentProjects) {
            Project project = studentProject.getProject();
            System.out.println("Evaluating student: " + studentProject.getStudent().getStudentName() + ", Project ID: " + project.getProjectId());

            // Get instructors with roles based on evaType
            List<ProjectInstructorRole> instructors;
            switch (evaType) {
                case "Proposal":
                case "Defense":
                    // For "Proposal" and "Defense" evaluations, check "Advisor" or "Committee" roles
                    instructors = projectInstructorRoleRepository.findByProjectIdRole(project)
                            .stream()
                            .filter(i -> "Advisor".equals(i.getRole()) || "Committee".equals(i.getRole()))
                            .toList();
                    break;
                case "Poster":
                    // For "Poster" evaluations, check "Committee" or "Poster-Committee" roles
                    instructors = projectInstructorRoleRepository.findByProjectIdRole(project)
                            .stream()
                            .filter(i -> "Committee".equals(i.getRole()) || "Poster-Committee".equals(i.getRole()))
                            .toList();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid evaluation type: " + evaType);
            }

            System.out.println("Total instructors for project " + project.getProjectId() + ": " + instructors.size());

            // Check if there are no instructors or evaluations
            if (instructors.isEmpty()) {
                notEvaluated++;
                System.out.println("No instructors for project " + project.getProjectId() + ", marked as Not Evaluated.");
                continue;
            }

            long totalInstructors = instructors.size();
            long evaluationsCount = 0;

            // Retrieve evaluations based on evaType
            switch (evaType) {
                case "Proposal":
                    evaluationsCount = instructors.stream()
                            .flatMap(i -> i.getProposalEvaluations().stream())
                            .filter(e -> e.getStudent().equals(studentProject.getStudent()))
                            .count();
                    System.out.println("Proposal evaluations count for student " + studentProject.getStudent().getStudentName() + ": " + evaluationsCount);
                    break;
                case "Poster":
                    evaluationsCount = instructors.stream()
                            .flatMap(i -> i.getPosterEvaluations().stream())
                            .peek(evaluation -> System.out.println("Instructor " + evaluation.getInstructorIdPoster().getInstructorId() + " evaluated poster for project " + project.getProjectId()))
                            .count();  // No need to filter by student
                    System.out.println("Poster evaluations count for project " + project.getProjectId() + ": " + evaluationsCount);
                    break;
                case "Defense":
                    evaluationsCount = instructors.stream()
                            .flatMap(i -> i.getDefenseEvaluations().stream())
                            .filter(e -> e.getStudent().equals(studentProject.getStudent()))
                            .count();
                    System.out.println("Defense evaluations count for student " + studentProject.getStudent().getStudentName() + ": " + evaluationsCount);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid evaluation type: " + evaType);
            }

            // If there are no evaluations, mark as not evaluated
            if (evaluationsCount == 0) {
                notEvaluated++;
                System.out.println("No evaluations for student " + studentProject.getStudent().getStudentName() + ", marked as Not Evaluated.");
            } else {
                // Categorize evaluation statuses
                if (evaluationsCount == totalInstructors) {
                    completed++;
                    System.out.println("Completed for student " + studentProject.getStudent().getStudentName());
                } else {
                    partial++;
                    System.out.println("Partial for student " + studentProject.getStudent().getStudentName());
                }
            }
        }

        System.out.println("Completed: " + completed);
        System.out.println("Partial: " + partial);
        System.out.println("Not Evaluated: " + notEvaluated);

        return Map.of(
                "completed", completed,
                "partial", partial,
                "notEvaluated", notEvaluated,
                "totalStudents", totalStudents
        );
    }

//    public List<TeacherScoringDTO> getTeachersWithRemainingScores(String evaType, String year, String program) {
//        // Get all instructor roles for the given year and program
//        List<ProjectInstructorRole> instructorRoles;
//
//        if ("All".equalsIgnoreCase(program)) {
//            instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_Semester(year);
//        } else {
//            instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_SemesterAndProjectIdRole_Program(year, program);
//        }
//        System.out.println("Total instructors: " + instructorRoles.size());
//
//        // List to hold all TeacherScoringDTO objects
//        List<TeacherScoringDTO> instructorScoreList = new ArrayList<>();
//
//        for (ProjectInstructorRole role : instructorRoles) {
//            String professorId = role.getInstructor().getProfessorId();
//            String professorName = role.getInstructor().getProfessorName();
//
//            System.out.println("Professor ID: " + professorId);
//            System.out.println("Professor Name: " + professorName);
//
//            // Check if the professor already exists in the list
//            TeacherScoringDTO professorData = null;
//            for (TeacherScoringDTO dto : instructorScoreList) {
//                if (dto.getInstructorId().equals(professorId)) {
//                    professorData = dto;
//                    break;
//                }
//            }
//
//            // If professor not found, create a new entry
//            if (professorData == null) {
//                professorData = new TeacherScoringDTO(professorName, professorId, 0, 0, 0, 0);
//                instructorScoreList.add(professorData);
//            }
//
//            // Check if this role is relevant for the evaluation type
//            boolean isRelevantRole = false;
//
//            if ("Proposal".equalsIgnoreCase(evaType)) {
//                isRelevantRole = role.getRole().equalsIgnoreCase("Advisor") || role.getRole().equalsIgnoreCase("Committee");
//            } else if ("Poster".equalsIgnoreCase(evaType)) {
//                isRelevantRole = role.getRole().equalsIgnoreCase("Committee") || role.getRole().equalsIgnoreCase("Poster-Committee");
//            } else if ("Defense".equalsIgnoreCase(evaType)) {
//                isRelevantRole = role.getRole().equalsIgnoreCase("Advisor") || role.getRole().equalsIgnoreCase("Committee");
//            }
//
//            if (!isRelevantRole) {
//                continue;
//            }
//
//            // Count students in this project
//            Project project = role.getProjectIdRole();
//            int studentsInProject = studentProjectRepository.countByProject(project);
//            professorData.setTotalAssigned(professorData.getTotalAssigned() + studentsInProject);
//
//            System.out.println("🎞️" + professorData.toString());
//
//            // Count completed evaluations for this project
//            int completedForProject = 0;
//
//            if ("Proposal".equalsIgnoreCase(evaType)) {
//                completedForProject = proposalEvaluationRepository.countByProjectInstructorRole_Instructor_ProfessorIdAndProjectInstructorRole_ProjectIdRole(
//                        professorId, project);
//            } else if ("Poster".equalsIgnoreCase(evaType)) {
//                completedForProject = posterEvaRepository.countByInstructorIdPoster_Instructor_ProfessorIdAndProjectIdPoster(
//                        professorId, project);
//            } else if ("Defense".equalsIgnoreCase(evaType)) {
//                completedForProject = defenseEvaluationRepository.countByDefenseInstructorId_Instructor_ProfessorIdAndProjectId(
//                        professorId, project);
//            }
//
//            professorData.setCompletedCount(professorData.getCompletedCount() + completedForProject);
//
//            System.out.println("Total: " + instructorScoreList.size());
//            System.out.println("Completed: " + completedForProject);
//        }
//
//        // Log the instructorScoreList before filtering
//        System.out.println("Before filtering: " + instructorScoreList.size());
//        instructorScoreList.forEach(dto -> {
//            System.out.println("Professor ID: " + dto.getInstructorId() + ", Remaining: " + (dto.getTotalAssigned() - dto.getCompletedCount()));
//        });
//
//        // Process final results
//        List<TeacherScoringDTO> result = instructorScoreList.stream()
//                .map(score -> {
//                    // Calculate remainingCount
//                    int remainingCount = score.getTotalAssigned() - score.getCompletedCount();
//                    score.setRemainingCount(remainingCount);
//                    return score;
//                })
//                .filter(score -> score.getTotalAssigned() > 0)  // Only include those with assignments
//                .sorted(Comparator.comparing(TeacherScoringDTO::getRemainingCount).reversed())
//                .limit(5)  // Limiting the results to top 5
//                .collect(Collectors.toList());
//
//        // Log final result after filtering
//        System.out.println("After filtering: " + result.size());
//        result.forEach(dto -> {
//            System.out.println("Professor ID: " + dto.getInstructorId() + ", Remaining: " + dto.getRemainingCount());
//        });
//
//        return result;
//    }

    public List<TeacherScoringDTO> getTeachersWithRemainingScores(String evaType, String year, String program) {
        // Get all instructor roles for the given year and program
        List<ProjectInstructorRole> instructorRoles;

        if ("All".equalsIgnoreCase(program)) {
            instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_Semester(year);
        } else {
            instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_SemesterAndProjectIdRole_Program(year, program);
        }
        System.out.println("Total instructors: " + instructorRoles.size());

        // List to hold all TeacherScoringDTO objects
        List<TeacherScoringDTO> instructorScoreList = new ArrayList<>();

        for (ProjectInstructorRole role : instructorRoles) {
            String professorId = role.getInstructor().getProfessorId();
            String professorName = role.getInstructor().getProfessorName();

            System.out.println("Professor ID: " + professorId);
            System.out.println("Professor Name: " + professorName);

            // ค้นหาข้อมูลอาจารย์ใน instructorScoreList ที่มี professorId ตรงกัน
            TeacherScoringDTO professorData = instructorScoreList.stream()
                    .filter(dto -> dto.getInstructorId().equals(professorId))
                    .findFirst()
                    .orElse(null);  // ถ้าไม่พบ ให้ professorData เป็น null

            // ถ้าไม่พบข้อมูลของอาจารย์ใน list ให้สร้างข้อมูลใหม่
            if (professorData == null) {
                professorData = new TeacherScoringDTO(professorName, professorId, 0, 0, 0, 0);
                instructorScoreList.add(professorData);
            }

            // ตรวจสอบว่า role นี้เป็น role ที่เกี่ยวข้องกับการประเมินประเภทนี้หรือไม่
            boolean isRelevantRole = false;
            if ("Proposal".equalsIgnoreCase(evaType)) {
                isRelevantRole = role.getRole().equalsIgnoreCase("Advisor") || role.getRole().equalsIgnoreCase("Committee");
            } else if ("Poster".equalsIgnoreCase(evaType)) {
                isRelevantRole = role.getRole().equalsIgnoreCase("Committee") || role.getRole().equalsIgnoreCase("Poster-Committee");
            } else if ("Defense".equalsIgnoreCase(evaType)) {
                isRelevantRole = role.getRole().equalsIgnoreCase("Advisor") || role.getRole().equalsIgnoreCase("Committee");
            }

            if (!isRelevantRole) {
                continue;
            }

            // นับจำนวน students ในโปรเจกต์นี้
//            Project project = role.getProjectIdRole();
//            int studentsInProject = studentProjectRepository.countByProject(project);
//            professorData.setTotalAssigned(professorData.getTotalAssigned() + studentsInProject);
//
//            System.out.println("🎞️" + professorData.toString());

            // นับจำนวน students หรือ projects ตามประเภทการประเมิน
            Project project = role.getProjectIdRole();
            int studentsInProject = studentProjectRepository.countByProject(project);

            if ("Poster".equalsIgnoreCase(evaType)) {
                // ✅ Poster → นับเป็น 1 โปรเจกต์
                professorData.setTotalAssigned(professorData.getTotalAssigned() + 1);
            } else {
                // ✅ Proposal & Defense → นับเป็นจำนวน student
                professorData.setTotalAssigned(professorData.getTotalAssigned() + studentsInProject);
            }

            // นับจำนวนการประเมินที่เสร็จสมบูรณ์สำหรับโปรเจกต์นี้
            int completedForProject = 0;
            if ("Proposal".equalsIgnoreCase(evaType)) {
                completedForProject = proposalEvaluationRepository.countByProjectInstructorRole_Instructor_ProfessorIdAndProjectInstructorRole_ProjectIdRole(
                        professorId, project);
            } else if ("Poster".equalsIgnoreCase(evaType)) {
                completedForProject = posterEvaRepository.countByInstructorIdPoster_Instructor_ProfessorIdAndProjectIdPoster(
                        professorId, project);
            } else if ("Defense".equalsIgnoreCase(evaType)) {
                completedForProject = defenseEvaluationRepository.countByDefenseInstructorId_Instructor_ProfessorIdAndProjectId(
                        professorId, project);
            }

            professorData.setCompletedCount(professorData.getCompletedCount() + completedForProject);

            System.out.println("Total: " + instructorScoreList.size());
            System.out.println("Completed: " + completedForProject);
        }

        // Log the instructorScoreList before filtering
        System.out.println("Before filtering: " + instructorScoreList.size());
        instructorScoreList.forEach(dto -> {
            System.out.println("Professor ID: " + dto.getInstructorId() + ", Remaining: " + (dto.getTotalAssigned() - dto.getCompletedCount()));
        });

        // Process final results
        List<TeacherScoringDTO> result = instructorScoreList.stream()
                .map(score -> {
                    // คำนวณ remainingCount
                    int remainingCount = score.getTotalAssigned() - score.getCompletedCount();
                    score.setRemainingCount(remainingCount);
                    return score;
                })
                .filter(score -> score.getTotalAssigned() > 0)  // เฉพาะอาจารย์ที่มีการมอบหมาย
                .sorted(Comparator.comparing(TeacherScoringDTO::getRemainingCount).reversed())
//                .limit(5)  // จำกัดผลลัพธ์เป็น 5 คน
                .collect(Collectors.toList());

        // Log final result after filtering
        System.out.println("After filtering: " + result.size());
        result.forEach(dto -> {
            System.out.println("Professor ID: " + dto.getInstructorId() + ", Remaining: " + dto.getRemainingCount());
        });

        return result;
    }


}

