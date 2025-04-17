package com.example.project.controller.ShowProject;

import com.example.project.DTO.InstructorProjectDTO;
import com.example.project.DTO.ShowProject.*;
import com.example.project.DTO.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.*;
import org.aspectj.lang.annotation.DeclareError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    @Autowired
    private GradingProposalEvaluationRepository gradingProposalEvaluationRepository;

    @Autowired
    private ProposalGradeService proposalGradeService;

    @Autowired
    private DefenseEvaluationService defenseEvaluationService;

    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;

    @Autowired
    private DefenseGradeService defenseGradeService;

    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;

    @Autowired
    private PosterEvaluationService posterEvaluationService;

    @Autowired
    private PosterEvaRepository posterEvaRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public ProjectController(ProjectService projectService, ProposalEvaluationRepository proposalEvaluationRepository, ProposalEvaluationService proposalEvaluationService, GradingProposalEvaluationRepository gradingProposalEvaluationRepository, ProposalGradeService proposalGradeService, DefenseEvaluationService defenseEvaluationService, DefenseEvaluationRepository defenseEvaluationRepository, DefenseGradeService defenseGradeService, GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository, PosterEvaluationService posterEvaluationService, PosterEvaRepository posterEvaRepository, ProjectInstructorRoleRepository projectInstructorRoleRepository) {
        this.projectService = projectService;
        this.proposalEvaluationRepository = proposalEvaluationRepository;
        this.proposalEvaluationService = proposalEvaluationService;
        this.gradingProposalEvaluationRepository = gradingProposalEvaluationRepository;
        this.proposalGradeService = proposalGradeService;
        this.defenseEvaluationService = defenseEvaluationService;
        this.defenseEvaluationRepository = defenseEvaluationRepository;
        this.defenseGradeService = defenseGradeService;
        this.gradingDefenseEvaluationRepository = gradingDefenseEvaluationRepository;
        this.posterEvaluationService = posterEvaluationService;
        this.posterEvaRepository = posterEvaRepository;
        this.projectInstructorRoleRepository = projectInstructorRoleRepository;
    }


    //=========================================== USE ===================================================

    // show project list page
    @GetMapping("/instructor/view")
    public String viewInstructorProjectPage() {
        System.out.println("📌 Show default instructor page");

        return "DashboardInstructor"; // html
    }

    // project list by user (Proposal)
    @GetMapping("/instructor/projectList")
    @ResponseBody
    public List<InstructorProjectDTONew> getInstructorData() {
        System.out.println("👀Find project list");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Check authentication: " + authentication);
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        // get all criteria
        List<Criteria> allPropEvaCriteria = proposalEvaluationService.getProposalCriteria();

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    String year = i.getProjectIdRole().getSemester();

                    List<ProposalEvaluation> proposalEvaluationList = proposalEvaluationRepository.findByProject_ProjectId(projectId);

                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectDTONew> studentProjectDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentEvaStatus(
                                        username,
                                        studentProject.getStudent().getStudentId(),
                                        allPropEvaCriteria,
                                        proposalEvaluationList
                                );

                                return new StudentProjectDTONew(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        isComplete);
                            })
                            .toList();

                    if (studentProjectDTOS.isEmpty()) {
                        return null;
                    }

                    boolean isAllComplete = studentProjectDTOS.stream()
                            .allMatch(StudentProjectDTONew::isEvaluationComplete);

                    return new InstructorProjectDTONew(
                            // i -> projectInstructorRoles
                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectDTOS,
                            isAllComplete,
                            i.getProjectIdRole().getSemester()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean checkStudentEvaStatus(String instructorUsername, String studentId, List<Criteria> allPropEvaCriteria, List<ProposalEvaluation> proposalEvaluationList) {

        // find instructor & student
        Optional<ProposalEvaluation> evaluation = proposalEvaluationList.stream()
                .filter(e -> e.getProjectInstructorRole().getInstructor().getAccount().getUsername().equals(instructorUsername) &&
                        e.getStudent().getStudentId().equals(studentId))
                .findFirst();

        if (evaluation.isEmpty()) {
            return false;
        }

        ProposalEvaluation proposalEvaluation = evaluation.get();

        // ให้คะแนนครบทุก criteria & score != null ?
        boolean hasAllScores = allPropEvaCriteria.stream()
                .allMatch(criteria -> {
                    Optional<ProposalEvalScore> score = proposalEvaluation.getProposalEvalScores().stream()
                            .filter(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()))
                            .findFirst();

                    return score.isPresent() && score.get().getScore() != null;
                });

        return hasAllScores;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // project list by user (Proposal Grade)
    @GetMapping("/instructor/projectPropGradeList")
    @ResponseBody
    public List<InstructorProjectPropGradeDTO> getInstructorPropGradeData() {
        System.out.println("👀Find project list");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Check authentication: " + authentication);
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    List<GradingProposalEvaluation> gradingProposalEvaluationList = gradingProposalEvaluationRepository.findByProject_ProjectId(projectId);

                    // get all project instructor
                    List<ProposalEvaluation> proposalEvaluationList = proposalEvaluationRepository.findByProject_ProjectId(projectId);
                    List<ProjectInstructorRole> projectInstructorRoleList = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
                    List<Criteria> allPropEvaCriteria = proposalEvaluationService.getProposalCriteria();

                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectPropGradeDTO> studentProjectPropGradeDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentPropGradeStatus(
                                        studentProject.getStudent().getStudentId(),
                                        gradingProposalEvaluationList
                                );

                                // check status for each instructor
                                InstructorEvaluationStatusDTO instructorEvaStatus = checkInstructorEva(
                                        studentProject.getStudent().getStudentId(),
                                        allPropEvaCriteria,
                                        proposalEvaluationList,
                                        projectInstructorRoleList
                                );

                                String gradeResult = getGradePropResult(studentProject.getStudent().getStudentId(), gradingProposalEvaluationList);

                                return new StudentProjectPropGradeDTO(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        // การประเมินจาก user คนนั้น
                                        isComplete,
                                        instructorEvaStatus,
                                        gradeResult);
                            })
                            .toList();

                    if (studentProjectPropGradeDTOS.isEmpty()) {
                        return null;
                    }

                    // ทุกค่า isComplete ใน DTO มีค่า
                    boolean isAllComplete = studentProjectPropGradeDTOS.stream()
                            .allMatch(StudentProjectPropGradeDTO::isEvaluationComplete);

                    return new InstructorProjectPropGradeDTO(
                            // i -> projectInstructorRoles
                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectPropGradeDTOS,
                            isAllComplete,
                            i.getProjectIdRole().getSemester()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean checkStudentPropGradeStatus(String studentId, List<GradingProposalEvaluation> gradingProposalEvaluationList) {

        // find student
        Optional<GradingProposalEvaluation> evaluation = gradingProposalEvaluationList.stream()
                .filter(e -> e.getStudent().getStudentId().equals(studentId))
                .findFirst();

        if (evaluation.isEmpty()) {
            return false;
        }

        // get score
        BigDecimal scoreCriteriaId = evaluation.get().getEvaluateScore();

        return scoreCriteriaId != null;
    }


    // proposal eva status instructor
    private InstructorEvaluationStatusDTO checkInstructorEva(String studentId, List<Criteria> allPropEvaCriteria, List<ProposalEvaluation> proposalEvaluationList, List<ProjectInstructorRole> projectInstructorRoleList) {

//        long allinstructor = projectInstructorRoleList.size();

        long allinstructor = projectInstructorRoleList.stream()
                .filter(role -> ("Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())))
                .count();

        // loop projectInstructorRole
        long instructorSuccessEva = projectInstructorRoleList.stream()
                .filter(instructor ->
                        "Advisor".equalsIgnoreCase(instructor.getRole()) || "Committee".equalsIgnoreCase(instructor.getRole())
                )
                .filter(instructor -> {

                    Optional<ProposalEvaluation> instructorEvaCheck = proposalEvaluationList.stream()
                            .filter(e -> e.getStudent().getStudentId().equals(studentId) &&
                                    // eva == projectInstructorRole
                                    e.getProjectInstructorRole().getInstructorId().equals(instructor.getInstructorId()))
                            .findFirst();

                    // not create
                    if (instructorEvaCheck.isEmpty()) {
                        return false;
                    }

                    // pull instructor have eva
                    ProposalEvaluation proposalEvaluation = instructorEvaCheck.get();

                    // ให้คะแนนครบทุก criteria & score != null ?
                    boolean hasAllScores = allPropEvaCriteria.stream()
                            .allMatch(criteria -> {
                                Optional<ProposalEvalScore> score = proposalEvaluation.getProposalEvalScores().stream()
                                        .filter(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()))
                                        .findFirst();

                                return score.isPresent() && score.get().getScore() != null;
                            });

                    return hasAllScores;

                }).count();

        System.out.println("👨‍🏫 Total Instructors Proposal: " + allinstructor);
        System.out.println("✅ Instructors with complete proposal evaluation: " + instructorSuccessEva);

        return new InstructorEvaluationStatusDTO(allinstructor, instructorSuccessEva);

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // project list by user (Defense)
    @GetMapping("/instructor/projectDefenseList")
    @ResponseBody
    public List<InstructorProjectDefenseDTO> getInstructorDefenseData() {
        System.out.println("👀Find project list");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Check authentication: " + authentication);
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        // get all criteria
        List<Criteria> allDefenseEvaCriteria = defenseEvaluationService.getDefenseCriteria();

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    List<DefenseEvaluation> defenseEvaluationList = defenseEvaluationRepository.findByProjectId_ProjectId(projectId);

                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectDefenseDTO> studentProjectDefenseDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentDefenseEvaStatus(
                                        username,
                                        studentProject.getStudent().getStudentId(),
                                        allDefenseEvaCriteria,
                                        defenseEvaluationList
                                );

                                return new StudentProjectDefenseDTO(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        isComplete);
                            })
                            .toList();

                    if (studentProjectDefenseDTOS.isEmpty()) {
                        return null;
                    }

                    boolean isAllComplete = studentProjectDefenseDTOS.stream()
                            .allMatch(StudentProjectDefenseDTO::isEvaluationComplete);

                    return new InstructorProjectDefenseDTO(
                            // i -> projectInstructorRoles
                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectDefenseDTOS,
                            isAllComplete,
                            i.getProjectIdRole().getSemester()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getGradePropResult(String studentId, List<GradingProposalEvaluation> gradingProposalEvaluationList) {
        return gradingProposalEvaluationList.stream()
                .filter(eva -> eva.getStudent() != null && studentId.equals(eva.getStudent().getStudentId()))
                .map(eva -> Optional.ofNullable(eva.getGradeResult()).orElse("-"))
                .findFirst()
                .orElse("-");
    }

    private String getGradeDefenseResult(String studentId, List<GradingDefenseEvaluation> gradingDefenseEvaluationList) {
        return gradingDefenseEvaluationList.stream()
                .filter(eva -> eva.getStudentId() != null && studentId.equals(eva.getStudentId().getStudentId()))
                .map(eva -> Optional.ofNullable(eva.getGradeResult()).orElse("-"))
                .findFirst()
                .orElse("-");
    }


    private boolean checkStudentDefenseEvaStatus(String instructorUsername, String studentId, List<Criteria> allDefenseEvaCriteria, List<DefenseEvaluation> defenseEvaluationList) {

        // find instructor & student
        Optional<DefenseEvaluation> evaluation = defenseEvaluationList.stream()
                .filter(e -> e.getDefenseInstructorId().getInstructor().getAccount().getUsername().equals(instructorUsername) &&
                        e.getStudent().getStudentId().equals(studentId))
                .findFirst();

        if (evaluation.isEmpty()) {
            return false;
        }

        DefenseEvaluation defenseEvaluation = evaluation.get();

        // ให้คะแนนครบทุก criteria & score != null ?
        boolean hasAllScores = allDefenseEvaCriteria.stream()
                .allMatch(criteria -> {
                    Optional<DefenseEvalScore> score = defenseEvaluation.getDefenseEvalScore().stream()
                            .filter(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()))
                            .findFirst();

                    return score.isPresent() && score.get().getScore() != null;
                });

        return hasAllScores;

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // project list by user (Defense Grade)
    @GetMapping("/instructor/projectDefeseGradeList")
    @ResponseBody
    public List<InstructorProjectDefenseGradeDTO> getInstructorDefenseGradeData() {
        System.out.println("👀Find project list");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Check authentication: " + authentication);
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();


        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    List<GradingDefenseEvaluation> gradingDefenseEvaluationList = gradingDefenseEvaluationRepository.findByProjectId_ProjectId(projectId);

                    // get all project instructor
                    System.out.println("📋Project: " + projectId);
                    List<DefenseEvaluation> defenseEvaluationList = defenseEvaluationRepository.findByProjectId_ProjectId(projectId);
                    List<PosterEvaluation> posterEvaluationList = posterEvaRepository.findByProjectIdPoster_ProjectId(projectId);
                    List<ProjectInstructorRole> projectInstructorRoleList = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
                    List<Criteria> allDefenseCriteria = defenseEvaluationService.getDefenseCriteria();
                    List<Criteria> allPosterCriteria = posterEvaluationService.getPosterCriteria();
//                    System.out.println("📝Criteria: " + allDefenseGradeCriteria);


                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectDefenseGradeDTO> studentProjectDefenseGradeDTOList = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentDefenseGradeStatus(
                                        studentProject.getStudent().getStudentId(),
                                        gradingDefenseEvaluationList
                                );

                                // check status for each instructor
                                InstructorEvaluationDefenseStatusDTO instructorEvaStatus = checkInstructorDefenseEva(
                                        studentProject.getStudent().getStudentId(),
                                        allDefenseCriteria,
//                                        allPosterCriteria,
                                        defenseEvaluationList,
//                                        posterEvaluationList,
                                        projectInstructorRoleList
                                );

                                List<GradingDefenseEvaluation> gradingDefenseEvaluationList1 = gradingDefenseEvaluationRepository.findByProjectId_ProjectId(projectId);

                                String gradeResult = getGradeDefenseResult(studentProject.getStudent().getStudentId(), gradingDefenseEvaluationList1);

                                return new StudentProjectDefenseGradeDTO(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        isComplete,
                                        instructorEvaStatus,
                                        gradeResult);
                            })
                            .toList();

                    if (studentProjectDefenseGradeDTOList.isEmpty()) {
                        return null;
                    }

                    boolean isAllComplete = studentProjectDefenseGradeDTOList.stream()
                            .allMatch(StudentProjectDefenseGradeDTO::isEvaluationComplete);

                    return new InstructorProjectDefenseGradeDTO(
                            // i -> projectInstructorRoles
                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectDefenseGradeDTOList,
                            isAllComplete,
                            i.getProjectIdRole().getSemester()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean checkStudentDefenseGradeStatus(String studentId, List<GradingDefenseEvaluation> gradingDefenseEvaluationList) {

        return gradingDefenseEvaluationList.stream()
                .anyMatch(e -> e.getStudentId() != null && studentId.equals(e.getStudentId().getStudentId()));

    }

    // defense eva status instructor
//    private InstructorEvaluationDefenseStatusDTO checkInstructorDefenseEva(String studentId, List<Criteria> allDefenseCriteria, List<DefenseEvaluation> defenseEvaluationList, List<ProjectInstructorRole> projectInstructorRoleList) {
//
//        // param base on projectId
//        System.out.println("⭐️Check input: ");
//        System.out.println("StudentID: " + studentId);
//        System.out.println("All Criteria: " + allDefenseCriteria.size());
//        System.out.println("Defense Evaluation List: " + defenseEvaluationList.size());
//        System.out.println("Project Instructor Roles: " + projectInstructorRoleList.size());
//
////        long allInstructor = projectInstructorRoleList.size();
//
//        // loop projectInstructorRole
//        long instructorDefenseSuccessEva = projectInstructorRoleList.stream()
//                // each instructor
//                .filter(instructor -> {
//
//                    boolean hasDefenseEvaluation = false; // default
////                    boolean hasPosterEvaluation = false;  // default
//
//                    if ("Advisor".equalsIgnoreCase(instructor.getRole()) || "Committee".equalsIgnoreCase(instructor.getRole())) {
//
//                        // each eva
//                        Optional<DefenseEvaluation> instructorDefenseEvaCheck = defenseEvaluationList.stream()
//                                .filter(e -> e.getStudent().getStudentId().equals(studentId) &&
//                                        // eva == projectInstructorRole
//                                        e.getDefenseInstructorId().getInstructorId().equals(instructor.getInstructorId()))
//                                .findFirst();
//
//                        // ให้คะแนนครบทุก criteria & score != null ?
//                        hasDefenseEvaluation = instructorDefenseEvaCheck.map(defenseEvaluation ->
//                                allDefenseCriteria.stream().allMatch(criteria ->
//                                        defenseEvaluation.getDefenseEvalScore().stream()
//                                                .anyMatch(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()) && s.getScore() != null))
//                        ).orElse(false);
//
//                    }
//
////                    if ("Poster-Committee".equalsIgnoreCase(instructor.getRole()) || "Committee".equalsIgnoreCase(instructor.getRole())) {
////
////                        Optional<PosterEvaluation> instructorPosterCheck = posterEvaluationList.stream()
////                                .filter(pe -> pe.getInstructorIdPoster().getInstructorId().equals(instructor.getInstructorId()))
////                                .findFirst();
////
////                        // float
////                        hasPosterEvaluation = instructorPosterCheck.map(posterEvaluation ->
////                                allPosterCriteria.stream()
////                                        .allMatch(criteria -> posterEvaluation.getPosterEvaluationScores().stream()
////                                                .anyMatch(ps -> ps.getCriteriaPoster().getCriteriaId().equals(criteria.getCriteriaId())))
////                        ).orElse(false);
////
////                    }
//
//                    return ("Committee".equalsIgnoreCase(instructor.getRole()))
//                            ? (hasDefenseEvaluation && hasPosterEvaluation)
//                            : (hasDefenseEvaluation || hasPosterEvaluation);
//
//                }).count();
//
//        System.out.println("👨‍🏫 Total Instructors Defense: " + allInstructor);
//        System.out.println("✅ Instructors with complete defense evaluation: " + instructorDefenseSuccessEva);
//
//        return new InstructorEvaluationDefenseStatusDTO(allInstructor, instructorDefenseSuccessEva);
//
//    }

    private InstructorEvaluationDefenseStatusDTO checkInstructorDefenseEva(String studentId, List<Criteria> allDefenseCriteria, List<DefenseEvaluation> defenseEvaluationList, List<ProjectInstructorRole> projectInstructorRoleList) {

        // param base on projectId
        System.out.println("⭐️Check input: ");
        System.out.println("StudentID: " + studentId);
        System.out.println("All Criteria: " + allDefenseCriteria.size());
        System.out.println("Defense Evaluation List: " + defenseEvaluationList.size());
        System.out.println("Project Instructor Roles: " + projectInstructorRoleList.size());

//        long allInstructor = projectInstructorRoleList.size();

        long allinstructor = projectInstructorRoleList.stream()
                .filter(role -> ("Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole())))
                .count();

        // loop projectInstructorRole
        long instructorDefenseSuccessEva = projectInstructorRoleList.stream()
                // each instructor
                .filter(instructor ->"Advisor".equalsIgnoreCase(instructor.getRole()) || "Committee".equalsIgnoreCase(instructor.getRole()))
                .filter(instructor -> {

                        // each eva
                        Optional<DefenseEvaluation> instructorDefenseEvaCheck = defenseEvaluationList.stream()
                                .filter(e -> e.getStudent().getStudentId().equals(studentId) &&
                                        // eva == projectInstructorRole
                                        e.getDefenseInstructorId().getInstructorId().equals(instructor.getInstructorId()))
                                .findFirst();

                        // not create
                        if (instructorDefenseEvaCheck.isEmpty()) {
                            return false;
                        }

                        // pull instructor have eva
                        DefenseEvaluation defenselEvaluation = instructorDefenseEvaCheck.get();

                        // ให้คะแนนครบทุก criteria & score != null ?
                        boolean hasAllScores = allDefenseCriteria.stream()
                                .allMatch(criteria -> {
                                    Optional<DefenseEvalScore> score = defenselEvaluation.getDefenseEvalScore().stream()
                                            .filter(s -> s.getCriteria().getCriteriaId().equals(criteria.getCriteriaId()))
                                            .findFirst();

                                    return score.isPresent() && score.get().getScore() != null;
                                });

                    return hasAllScores;

                }).count();

        System.out.println("👨‍🏫 Total Instructors Defense: " + allinstructor);
        System.out.println("✅ Instructors with complete defense evaluation: " + instructorDefenseSuccessEva);

        return new InstructorEvaluationDefenseStatusDTO(allinstructor, instructorDefenseSuccessEva);

    }



    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // project list by user (Poster)
    @GetMapping("/instructor/projectPosterList")
    @ResponseBody
    public List<InstructorProjectPosterDTO> getInstructorPosterData() {
        System.out.println("👀Find project list");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("💌 Check authentication: " + authentication);
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
        // get all criteria
        List<Criteria> allPosterEvaCriteria = posterEvaluationService.getPosterCriteria();

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Poster-Committee".equalsIgnoreCase(i.getRole()) || "Committee".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    List<PosterEvaluation> posterEvaluationList = posterEvaRepository.findByProjectIdPoster_ProjectId(projectId);

                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectPosterDTO> studentProjectPosterDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentPosterStatus(
                                        username,
                                        allPosterEvaCriteria,
                                        posterEvaluationList
                                );

                                return new StudentProjectPosterDTO(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        isComplete);
                            })
                            .toList();

                    if (studentProjectPosterDTOS.isEmpty()) {
                        return null;
                    }

                    boolean isAllComplete = studentProjectPosterDTOS.stream()
                            .allMatch(StudentProjectPosterDTO::isEvaluationComplete);

                    return new InstructorProjectPosterDTO(
                            // i -> projectInstructorRoles
                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectPosterDTOS,
                            isAllComplete,
                            i.getProjectIdRole().getSemester()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean checkStudentPosterStatus(String instructorUsername, List<Criteria> allPosterEvaCriteria, List<PosterEvaluation> posterEvaluationList) {

        // find instructor & student
        Optional<PosterEvaluation> evaluation = posterEvaluationList.stream()
                .filter(e -> e.getInstructorIdPoster().getInstructor().getAccount().getUsername().equals(instructorUsername))
                .findFirst();

        if (evaluation.isEmpty()) {
            return false;
        }

        PosterEvaluation posterEvaluation = evaluation.get();

        // ให้คะแนนครบทุก criteria & score != null ?
        boolean hasAllScores = allPosterEvaCriteria.stream()
                .allMatch(criteria -> {
                    Optional<PosterEvaluationScore> score = posterEvaluation.getPosterEvaluationScores().stream()
                            .filter(s -> s.getCriteriaPoster().getCriteriaId().equals(criteria.getCriteriaId()))
                            .findFirst();

                    return score.isPresent();
                });

        return hasAllScores;

        // collect all criteria in eva
//        Set<String> scoreCriteriaId = evaluation.get().getPosterEvaluationScores().stream()
//                .map(score -> score.getCriteriaPoster().getCriteriaId())
//                .collect(Collectors.toSet());
//
//        return allPosterEvaCriteria.stream().allMatch(criteria -> scoreCriteriaId.contains(criteria.getCriteriaId()));
    }


    // project list by user filter Advisor
    @GetMapping("/advisor/projectList")
    @ResponseBody
    public List<InstructorProjectDTO> getAdvisorData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();

        System.out.println("Find project list");

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()))
                .map(i -> {
                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectDTO> studentProjectDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> new StudentProjectDTO(
                                    // getStudent() -> ใน (StudentProjects Entity)
                                    studentProject.getStudent().getStudentId(),
                                    studentProject.getStudent().getStudentName(),
                                    studentProject.getStatus()))
                            .toList();

                    if (studentProjectDTOS.isEmpty()) {
                        return null;
                    }

                    return new InstructorProjectDTO(
                            // i -> projectInstructorRoles

                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectDTOS
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @GetMapping("/instructor/getProjectDetails")
    @ResponseBody
    public Project getProjectDetails(@RequestParam String projectId) {
        // เรียกใช้ service เพื่อดึงข้อมูลจากฐานข้อมูล
        Project project = projectService.getProjectDetails(projectId);

        if (project == null) {
            // ถ้าไม่พบโปรเจกต์
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        // ส่งข้อมูลกลับเป็น JSON
        return project;
    }

    @GetMapping("/instructor/projectSemesterList")
    @ResponseBody
    public List<String> getAllSemesters() {
        // ดึงข้อมูลโปรเจกต์ทั้งหมดจากฐานข้อมูล
        List<Project> projects = projectRepository.findAll();  // ดึงข้อมูลโปรเจกต์ทั้งหมด
        Set<String> semesters = new HashSet<>();  // ใช้ Set เพื่อเก็บค่า semester ที่ไม่ซ้ำ

        // ดึงค่า semester จากแต่ละโปรเจกต์และเพิ่มลงใน Set
        for (Project project : projects) {
            String semester = project.getSemester();
            if (semester != null && !semester.trim().isEmpty()) {
                semesters.add(semester.trim());  // ใช้ trim() เพื่อลบช่องว่างออก
            }
        }

        // แปลง Set กลับเป็น List
        List<String> sortedSemesters = new ArrayList<>(semesters);

        // เรียงลำดับ semester (ตัวอย่างนี้ใช้เรียงจากมากไปน้อยตามปี)
        sortedSemesters.sort((s1, s2) -> Integer.compare(Integer.parseInt(s2), Integer.parseInt(s1)));  // เรียงจากมากไปน้อย

        return sortedSemesters;  // ส่งค่า semester ที่ไม่ซ้ำและเรียงลำดับ
    }














    //=========================================== NOT USE ===================================================


//    // send project when click edit
//    @GetMapping("/instructor/editProposalEvaluation")
//    @ResponseBody
//    public ResponseEntity<?> getProjectDetails(@RequestParam String projectId){
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Account username: " + authentication.getName());
//        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());
//
//        Project projectDetails = projectService.getProjectDetails(projectId);
//        if (projectDetails == null) {
//            System.out.println("Umm");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Project Details");
//        }
//        System.out.println("Project ID: " + projectId);
//        System.out.println("Project Detail: " + projectDetails);
//        return ResponseEntity.ok(projectDetails);
//    }

//    // ข้อมูล instructor project
//    @GetMapping("/instructor/project")
//    public ResponseEntity<?> getInstructorProject() {
//        try {
//            List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
//            if (projectInstructorRoles.isEmpty()){
//                return ResponseEntity.ok("No project data available.");
//            }
//            return ResponseEntity.ok(projectInstructorRoles);
//        } catch (UsernameNotFoundException ex) {
//            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
//        }
//    }
//
//    // ดึงข้อมูลผู้ใช้เมื่อ login เข้าระบบ
//    @GetMapping("/instructor/details")
//    public ResponseEntity<Instructor> getInstructorUserDetails() {
//        try {
//            Instructor instructor = projectService.getInstructorUserDetails();
//            return ResponseEntity.ok(instructor);
//        } catch (UsernameNotFoundException ex) {
//            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
//        }
//    }
//
//    @GetMapping("/admin/details")
//    @ResponseBody
//    public ResponseEntity<Admin> getAdminUserDetails() {
//        try {
//            Admin admin = projectService.getAdminUserDetails();
//            return ResponseEntity.ok(admin);
//        } catch (UsernameNotFoundException ex) {
//            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
//        }
//    }
}