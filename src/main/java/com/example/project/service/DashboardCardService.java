package com.example.project.service;

import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.*;
import com.example.project.service.ManageSchedule.ManageProposalScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardCardService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;

    @Autowired
    private ProposalGradeService proposalGradeService;

    @Autowired
    private GradingProposalEvaluationRepository gradingProposalEvaluationRepository;

    @Autowired
    private PosterEvaluationService posterEvaluationService;

    @Autowired
    private PosterEvaRepository posterEvaRepository;

    @Autowired
    private DefenseEvaluationService defenseEvaluationService;

    @Autowired
    private DefenseGradeService defenseGradeService;

    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;

    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProposalSchedRepository proposalSchedRepository;

    @Autowired
    private ManageProposalScheduleService manageProposalScheduleService;

    @Autowired
    private InstructorRepository instructorRepository;

    //=========================================== USE ===================================================


    // Proposal
    public Map<String, Object> checkGroupEvaStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        List<Criteria> allPropEvaCriteria = proposalEvaluationService.getProposalCriteria();
//        List<Criteria> allPropGradeCriteria = proposalGradeService.getProposalCriteria();

        long totalProjects = projectInstructorRoles.stream()
                .filter(role -> ("Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())) && role.getInstructor().getAccount().getUsername().equals(username))
                .count();

        List<String> successfulProjects = new ArrayList<>();

        long instructorPropoSuccessEva = projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(i -> {

                    List<StudentProject> studentProjectsList = i.getProjectIdRole().getStudentProjects();
                    System.out.println("👩🏻‍🎓 List of students: " + studentProjectsList);

                    // get projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    System.out.println("📝 ProjectId: " + projectId);

                    // get eva
                    List<ProposalEvaluation> proposalEvaluationList = proposalEvaluationRepository.findByProject_ProjectId(projectId);
                    List<GradingProposalEvaluation> gradingProposalEvaluationList = gradingProposalEvaluationRepository.findByProject_ProjectId(projectId);

                    // each student each criteria
                    boolean allStudentsComplete = studentProjectsList.stream()
                            .filter(student -> "Active".equals(student.getStatus()))
                            .allMatch(student -> {

                                Optional<ProposalEvaluation> evaluation = proposalEvaluationList.stream()
                                        .filter(e -> e.getProjectInstructorRole().getInstructor().getAccount().getUsername().equals(username) &&
                                                e.getStudent().getStudentId().equals(student.getStudent().getStudentId()))
                                        .findFirst();

                                // if student have Evaluation and check criteria
                                boolean hasAllEvaScores = evaluation.map(proposalEvaluation ->
                                        allPropEvaCriteria.stream().allMatch(criteria ->
                                                proposalEvaluation.getProposalEvalScores().stream()
                                                        .anyMatch(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()) &&
                                                                s.getScore() != null))
                                ).orElse(false);

                                return hasAllEvaScores;
                            });

                    if (allStudentsComplete) {
                        successfulProjects.add(projectId); // ใช้ List แทน Map
                        System.out.println("🙊 ProjectId success: " + projectId);
                    }

                    return allStudentsComplete;
                }).count(); // all project success

        System.out.println(successfulProjects);
        successfulProjects.forEach(projectId -> System.out.println("🎀 ProjectId: " + projectId));


        System.out.println("📊 Instructor Total Project: " + totalProjects);
        System.out.println("✅ Instructor Proposal Evaluations Success: " + instructorPropoSuccessEva);

//        return "✅ instructorPropoSuccessEva: " + instructorPropoSuccessEva + " From 📊 totalProjects: " + totalProjects;

        Map<String, Object> result = new HashMap<>();
        result.put("totalProjects", (int) totalProjects);
        result.put("instructorPropoSuccessEva", (int) instructorPropoSuccessEva);

        return result;
    }

    // Poster
    public Map<String, Object> checkGroupPosterEvaStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        List<Criteria> allPosterEvaCriteria = posterEvaluationService.getPosterCriteria();

        long totalProjects = projectInstructorRoles.stream()
                .filter(role -> ("Poster-Committee".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())) && role.getInstructor().getAccount().getUsername().equals(username))
                .count();

        long instructorPosterSuccessEva = projectInstructorRoles.stream()
                .filter(i -> "Poster-Committee".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(i -> {

                    // get projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    System.out.println("📝 ProjectId: " + projectId);

                    // get eva
                    List<PosterEvaluation> posterEvaluationList = posterEvaRepository.findByProjectIdPoster_ProjectId(projectId);
                    List<StudentProject> studentProjectsList = i.getProjectIdRole().getStudentProjects();


                    boolean allStudentsComplete = studentProjectsList.stream()
                            .filter(student -> "Active".equals(student.getStatus()))
                            .allMatch(student -> {
                                // each criteria
                                Optional<PosterEvaluation> evaluation = posterEvaluationList.stream()
                                        .filter(e -> e.getInstructorIdPoster().getInstructor().getAccount().getUsername().equals(username))
                                        .findFirst();

                                boolean hasAllEvaScores = evaluation.map(posterEvaluation ->
                                        allPosterEvaCriteria.stream().allMatch(criteria ->
                                                posterEvaluation.getPosterEvaluationScores().stream()
                                                        .anyMatch(s -> s.getCriteriaPoster().getCriteriaId().equals(criteria.getCriteriaId())))
                                ).orElse(false);

                                return hasAllEvaScores;
                            });

                    return allStudentsComplete;

                }).count();
        System.out.println("📊 Instructor Total Project: " + totalProjects);
        System.out.println("✅ Instructor Poster Evaluations Success: " + instructorPosterSuccessEva);

//        return "✅ instructorPosterSuccessEva: " + instructorPosterSuccessEva + " From 📊 totalProjects: " + totalProjects;
        Map<String, Object> result = new HashMap<>();
        result.put("totalProjects", (int) totalProjects);
        result.put("instructorPropoSuccessEva", (int) instructorPosterSuccessEva);

        return result;
    }

    // Defense
    public Map<String, Object> checkGroupDefenseEvaStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        List<Criteria> allDefenseEvaCriteria = defenseEvaluationService.getDefenseCriteria();
//        List<Criteria> allDefenseGradeCriteria = defenseGradeService.getDefenseCriteria();

        long totalProjects = projectInstructorRoles.stream()
                .filter(role -> ("Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())) && role.getInstructor().getAccount().getUsername().equals(username))
                .count();

        long instructorDefenseSuccessEva = projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(i -> {

                    List<StudentProject> studentProjectsList = i.getProjectIdRole().getStudentProjects();
                    System.out.println("👩🏻‍🎓 List of students: " + studentProjectsList);

                    // get projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    System.out.println("📝 ProjectId: " + projectId);

                    List<DefenseEvaluation> defenseEvaluationList = defenseEvaluationRepository.findByProjectId_ProjectId(projectId);
                    List<GradingDefenseEvaluation> gradingDefenseEvaluationList = gradingDefenseEvaluationRepository.findByProjectId_ProjectId(projectId);
//                    List<PosterEvaluation> posterEvaluationList = posterEvaRepository.findByProjectIdPoster_ProjectId(projectId);

                    // each student each criteria
                    boolean allStudentsComplete = studentProjectsList.stream()
                            .filter(student -> "Active".equals(student.getStatus()))
                            .allMatch(student -> {
                                Optional<DefenseEvaluation> evaluation = defenseEvaluationList.stream()
                                        .filter(e -> e.getDefenseInstructorId().getInstructor().getAccount().getUsername().equals(username) &&
                                                e.getStudent().getStudentId().equals(student.getStudent().getStudentId()))
                                        .findFirst();

                                // if student have Evaluation and check criteria
                                boolean hasAllEvaScores = evaluation.map(defenseEvaluation ->
                                        allDefenseEvaCriteria.stream().allMatch(criteria ->
                                                defenseEvaluation.getDefenseEvalScore().stream()
                                                        .anyMatch(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()) &&
                                                                s.getScore() != null))
                                ).orElse(false);

//                                boolean hasAllGradeScores = false; // default
//                                if ("Advisor".equalsIgnoreCase(i.getRole())) {
//                                    Optional<GradingDefenseEvaluation> gradingEvaluation = gradingDefenseEvaluationList.stream()
//                                            .filter(g -> g.getStudentId().getStudentId().equals(student.getStudent().getStudentId()))
//                                            .findFirst();
//
//                                    hasAllGradeScores = gradingEvaluation.stream().anyMatch(eg -> eg.getStudentId().getStudentId().equals(student.getStudent().getStudentId()));
//                                }
//
//                                return "Advisor".equalsIgnoreCase(i.getRole())
//                                        ? (hasAllEvaScores && hasAllGradeScores)
//                                        : hasAllEvaScores;
                                return hasAllEvaScores;
                            });

                    return allStudentsComplete;
                }).count();

        System.out.println("📊 Instructor Total Project: " + totalProjects);
        System.out.println("✅ Instructor Defense Evaluations Success: " + instructorDefenseSuccessEva);

//        return "✅ instructorDefenseSuccessEva: " + instructorDefenseSuccessEva + " From 📊 totalProjects: " + totalProjects;

        Map<String, Object> result = new HashMap<>();
        result.put("totalProjects", (int) totalProjects);
        result.put("instructorPropoSuccessEva", (int) instructorDefenseSuccessEva);

        return result;
    }

  
  // --------- Grade Distribute ------------ //
    public Map<String, Integer> getGradeDistribution(String program, String year, String evaType) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<Project> allProjects;

        // ดึงข้อมูลโครงงานตาม program และ year
        if ("All".equalsIgnoreCase(program)) {
            allProjects = projectRepository.findBySemesterAndProjectInstructorRoles_Instructor_Account_Username(year, username);
        } else {
            allProjects = projectRepository.findBySemesterAndProgramAndProjectInstructorRoles_Instructor_Account_Username(year, program, year);
        }

        System.out.println("allProjects [grade distribute]: " + allProjects.size());
        allProjects.forEach(project -> System.out.println("project: " + project.getProjectId()));


        // ใช้ HashMap ในการเก็บเฉพาะเกรดที่มีอยู่จริง
        Map<String, Integer> gradeDistribution = new HashMap<>();

        for (Project project : allProjects) {
            List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(project.getProjectId());
            System.out.println("studentProjects [grade distribute]: " + studentProjects.size() + ", " + project.getProjectId());

            for (StudentProject studentProject : studentProjects) {
                // เพิ่มการเช็คว่า studentProjects.ต้องมีสถานะเท่ากับ active ถึงจะนับ
                if ("active".equalsIgnoreCase(studentProject.getStatus())) {
                    String studentId = studentProject.getStudent().getStudentId();
                    System.out.println("studentId: " + studentId);
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
        }

        return gradeDistribution;
    }
  
    public List<PreviewProposalDTO> getProposalSchedule() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<Project> projectList = projectRepository.findProjectsByUsername(username);

        Optional<Instructor> instructorOpt = instructorRepository.findByAccountUsername(username);
        String professorName = instructorOpt.map(Instructor::getProfessorName).orElse("Unknown");
        System.out.println("professorName " + professorName);

        List<String> projectIds = projectList.stream()
                .map(Project::getProjectId).collect(Collectors.toList());

        List<PreviewProposalDTO> proposals = manageProposalScheduleService.getDataPreviewSchedule().stream()
                .filter(p -> projectIds.contains(p.getProjectId()))
                .filter(p -> p.getInstructorNames().values().stream()
                        .anyMatch(list -> list.stream().anyMatch(name -> name.equals(professorName)))).collect(Collectors.toList());


        for(PreviewProposalDTO proposal : proposals ){

            Map<String, List<String>> instructorNames = proposal.getInstructorNames();

            Map<String, List<String>> colletctName =new HashMap<>();

            instructorNames.forEach((role, names) -> {

                if(names.contains(professorName)) {

                    List<String> filterName = new ArrayList<>();
                    filterName.add(professorName);

                    colletctName.put(role, filterName);
                }
            });

            // เอาไปทับทที่
            proposal.setInstructorNames(colletctName);
        }


        return proposals;
    }
  
}


