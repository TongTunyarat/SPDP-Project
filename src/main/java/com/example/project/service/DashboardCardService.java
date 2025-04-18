package com.example.project.service;

import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.*;
import com.example.project.service.ManageSchedule.DefenseSchedule.ManageDefenseService;
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
    private ProposalSchedRepository proposalSchedRepository;

    @Autowired
    private ManageProposalScheduleService manageProposalScheduleService;

    @Autowired
    private ManageDefenseService manageDefenseService;

    @Autowired
    private InstructorRepository instructorRepository;

    //=========================================== USE ===================================================


    // Proposal
    public Map<String, Object> checkGroupEvaStatus(String year) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        List<Criteria> allPropEvaCriteria = proposalEvaluationService.getProposalCriteria();
//        List<Criteria> allPropGradeCriteria = proposalGradeService.getProposalCriteria();

        long totalProjects = projectInstructorRoles.stream()
                .filter(role -> ("Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())) && role.getInstructor().getAccount().getUsername().equals(username))
                .filter(i -> year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
                .count();

        List<String> successfulProjects = new ArrayList<>();

        long instructorPropoSuccessEva = projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(i -> year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
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
    public Map<String, Object> checkGroupPosterEvaStatus(String year) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        List<Criteria> allPosterEvaCriteria = posterEvaluationService.getPosterCriteria();

        long totalProjects = projectInstructorRoles.stream()
                .filter(role -> ("Poster-Committee".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())) && role.getInstructor().getAccount().getUsername().equals(username))
                .filter(i -> year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
                .count();

        long instructorPosterSuccessEva = projectInstructorRoles.stream()
                .filter(i -> "Poster-Committee".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(i -> year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
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
        result.put("instructorPosterSuccessEva", (int) instructorPosterSuccessEva);

        return result;
    }

    // Defense
    public Map<String, Object> checkGroupDefenseEvaStatus(String year) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        List<Criteria> allDefenseEvaCriteria = defenseEvaluationService.getDefenseCriteria();
//        List<Criteria> allDefenseGradeCriteria = defenseGradeService.getDefenseCriteria();

        long totalProjects = projectInstructorRoles.stream()
                .filter(role -> ("Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())) && role.getInstructor().getAccount().getUsername().equals(username))
                .filter(i -> year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
                .count();

        long instructorDefenseSuccessEva = projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .filter(i -> year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
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
        result.put("instructorDefenseSuccessEva", (int) instructorDefenseSuccessEva);

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
            // เช็กว่า project นี้ มี instructor ที่มี role == Advisor ไหม
            boolean hasAdvisorRole = project.getProjectInstructorRoles().stream()
                    .anyMatch(role -> "Advisor".equalsIgnoreCase(role.getRole()) &&
                            username.equals(role.getInstructor().getAccount().getUsername()));

            if (!hasAdvisorRole) {
                continue; // ข้ามถ้าไม่ใช่ Advisor
            }

            List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(project.getProjectId());
            System.out.println("studentProjects [grade distribute]: " + studentProjects.size() + ", " + project.getProjectId());

            for (StudentProject studentProject : studentProjects) {
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
                            return new HashMap<>();
                        } else {
                            throw new IllegalArgumentException("Invalid evaType: " + evaType);
                        }
                    } catch (NullPointerException e) {
                        grade = "I";
                    }

                    if (grade == null || grade.isEmpty()) {
                        grade = "I";
                    }
                    gradeDistribution.put(grade, gradeDistribution.getOrDefault(grade, 0) + 1);
                }
            }
        }


        return gradeDistribution;
    }

    // get proposchedule instructor
    public Map<String, List<PreviewProposalDTO>> getProposalSchedule(String year) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Account username: " + username);

        List<Project> projectList = projectRepository.findProjectsByUsername(username).stream()
                .filter(y -> y.getSemester().equalsIgnoreCase(year)).collect(Collectors.toList());

        Optional<Instructor> instructorOpt = instructorRepository.findByAccountUsername(username);
        String professorName = instructorOpt.map(Instructor::getProfessorName).orElse("Unknown");
        System.out.println("👩🏻‍🏫professorName " + professorName);

        List<String> projectIds = projectList.stream()
                .map(Project::getProjectId).collect(Collectors.toList());

        List<PreviewProposalDTO> proposals = manageProposalScheduleService.getDataPreviewSchedule(year).stream()
                .filter(p -> projectIds.contains(p.getProjectId()))
                .filter(p -> p.getInstructorNames().values().stream()
                        .anyMatch(list -> list.stream().anyMatch(name -> name.equals(professorName)))).collect(Collectors.toList());

        System.out.println("🌼 Proposal ");
        System.out.println("project list: "+projectIds.size());
        System.out.println("projectIds list: "+projectIds);
        System.out.println("year: "+year);

        List<PreviewProposalDTO> defenses = manageDefenseService.getDataDefensePreviewSchedule(year).stream()
                .filter(p -> projectIds.contains(p.getProjectId()))
                .filter(p -> p.getInstructorNames().values().stream()
                        .anyMatch(list -> list.stream().anyMatch(name -> name.equals(professorName)))).collect(Collectors.toList());

        System.out.println("⭐️ Defense");
        System.out.println("project list: "+projectIds.size());
        System.out.println("projectIds list: "+projectIds);
        System.out.println("year: "+year);

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

            System.out.println(proposal);
        }


        for(PreviewProposalDTO defense : defenses ){

            Map<String, List<String>> instructorNames = defense.getInstructorNames();

            Map<String, List<String>> colletctName =new HashMap<>();

            instructorNames.forEach((role, names) -> {

                if(names.contains(professorName)) {

                    List<String> filterName = new ArrayList<>();
                    filterName.add(professorName);

                    colletctName.put(role, filterName);
                }
            });

            // เอาไปทับทที่
            defense.setInstructorNames(colletctName);

            System.out.println(defense);
        }

        Map<String, List<PreviewProposalDTO>> result = new HashMap<>();
        result.put("Proposal", proposals);
        result.put("Defense", defenses);

        return result;
    }

    // get project foe each instructor
    public Map<String, Long> getProjectByInstructor(List<ProjectInstructorRole> list, String year) {

        Map<String, Long> result = new HashMap<>();

        String[][] conditions = {
                {"DST", "Advisor", "programDST"},
                {"DST", "Committee", "committeeProgramDST"},
                {"DST", "Co-Advisor", "coProgramDST"},
                {"ICT", "Advisor", "programICT"},
                {"ICT", "Committee", "committeeProgramICT"},
                {"ICT", "Co-Advisor", "coProgramICT"}

        };

        for(String[] condition : conditions) {

            String program = condition[0];
            String role = condition[1];
            String keyName = condition[2];

            long count = list.stream()
                    .filter(i -> program.equalsIgnoreCase(i.getProjectIdRole().getProgram()) && year.equalsIgnoreCase(i.getProjectIdRole().getSemester()))
                    .filter(i -> role.equalsIgnoreCase(i.getRole()))
                    .map(i -> {

                        List<StudentProject> studentProjects = i.getProjectIdRole().getStudentProjects();
                        if (studentProjects == null || studentProjects.isEmpty()) return false;

                        boolean hasActive = studentProjects.stream()
                                .anyMatch(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()));

                        boolean allExited = studentProjects.stream()
                                .allMatch(studentProject -> "Exited".equalsIgnoreCase(studentProject.getStatus()));

                        return hasActive && !allExited;

                    }).count();

            result.put(keyName, count);

        }

        return result;
    }
}

