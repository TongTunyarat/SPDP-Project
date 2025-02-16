package com.example.project.controller.ShowProject;

import com.example.project.DTO.InstructorProjectDTO;
import com.example.project.DTO.ShowProject.*;
import com.example.project.DTO.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    DefenseEvaluationRepository defenseEvaluationRepository;

    @Autowired
    DefenseGradeService defenseGradeService;

    @Autowired
    GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;

    @Autowired
    PosterEvaluationService posterEvaluationService;

    @Autowired
    PosterEvaRepository posterEvaRepository;

    public ProjectController(ProjectService projectService, ProposalEvaluationRepository proposalEvaluationRepository, ProposalEvaluationService proposalEvaluationService, GradingProposalEvaluationRepository gradingProposalEvaluationRepository, ProposalGradeService proposalGradeService, DefenseEvaluationService defenseEvaluationService, DefenseEvaluationRepository defenseEvaluationRepository, DefenseGradeService defenseGradeService, GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository, PosterEvaluationService posterEvaluationService, PosterEvaRepository posterEvaRepository) {
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
                            isAllComplete
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

//        // collect all criteria in eva
//        Set<String> scoreCriteriaId = evaluation.get().getProposalEvalScores().stream()
//                .map(score -> score.getCriteria().getCriteriaId())
//                .collect(Collectors.toSet());
//
//        return allPropEvaCriteria.stream().allMatch(criteria -> scoreCriteriaId.contains(criteria.getCriteriaId()));
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
        // get all criteria
        List<Criteria> allPropGradeCriteria = proposalGradeService.getProposalCriteria();

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    List<GradingProposalEvaluation> gradingProposalEvaluationList = gradingProposalEvaluationRepository.findByProject_ProjectId(projectId);

                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectPropGradeDTO> studentProjectPropGradeDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentPropGradeStatus(
                                        studentProject.getStudent().getStudentId(),
                                        gradingProposalEvaluationList
                                );

                                return new StudentProjectPropGradeDTO(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        isComplete);
                            })
                            .toList();

                    if (studentProjectPropGradeDTOS.isEmpty()) {
                        return null;
                    }

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
                            isAllComplete
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean checkStudentPropGradeStatus(String studentId, List<GradingProposalEvaluation> gradingProposalEvaluationList) {

        // find instructor & student
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
                            isAllComplete
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

//        // collect all criteria in eva
//        Set<String> scoreCriteriaId = evaluation.get().getProposalEvalScores().stream()
//                .map(score -> score.getCriteria().getCriteriaId())
//                .collect(Collectors.toSet());
//
//        return allPropEvaCriteria.stream().allMatch(criteria -> scoreCriteriaId.contains(criteria.getCriteriaId()));
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
        // get all criteria
//        List<Criteria> allDefenseGradeCriteria = defenseGradeService.getDefenseCriteria();

        // return JSON
        return projectInstructorRoles.stream()
                .filter(i -> "Advisor".equalsIgnoreCase(i.getRole()))
                .map(i -> {

                    // eva by projectId
                    String projectId = i.getProjectIdRole().getProjectId();
                    List<GradingDefenseEvaluation> gradingDefenseEvaluationList = gradingDefenseEvaluationRepository.findByProjectId_ProjectId(projectId);

                    // getStudentProjects -> เอา studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectDefenseGradeDTO> studentProjectDefenseGradeDTOList = i.getProjectIdRole().getStudentProjects().stream()
                            .filter(studentProject -> "Active".equals(studentProject.getStatus()))
                            .map(studentProject -> {
                                boolean isComplete = checkStudentDefenseGradeStatus(
                                        studentProject.getStudent().getStudentId(),
                                        gradingDefenseEvaluationList
                                );

                                return new StudentProjectDefenseGradeDTO(
                                        // getStudent() -> ใน (StudentProjects Entity)
                                        studentProject.getStudent().getStudentId(),
                                        studentProject.getStudent().getStudentName(),
                                        studentProject.getStatus(),
                                        isComplete);
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
                            isAllComplete
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean checkStudentDefenseGradeStatus(String studentId, List<GradingDefenseEvaluation> gradingDefenseEvaluationList) {

            return gradingDefenseEvaluationList.stream()
                    .anyMatch(e -> e.getStudentId() != null && studentId.equals(e.getStudentId().getStudentId()));

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
                            isAllComplete
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

        // collect all criteria in eva
        Set<String> scoreCriteriaId = evaluation.get().getPosterEvaluationScores().stream()
                .map(score -> score.getCriteriaPoster().getCriteriaId())
                .collect(Collectors.toSet());

        return allPosterEvaCriteria.stream().allMatch(criteria -> scoreCriteriaId.contains(criteria.getCriteriaId()));
    }

//    @GetMapping("/instructor/prop")
//    @ResponseBody
//    public List<ProposalEvaluation> getPropEva() {
//        return proposalEvaluationRepository.findAll();
//    }

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

    //=========================================== See Result (Not Use) ===================================================
    // ข้อมูล instructor project
    @GetMapping("/instructor/project")
    public ResponseEntity<?> getInstructorProject() {
        try {
            List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
            if (projectInstructorRoles.isEmpty()){
                return ResponseEntity.ok("No project data available.");
            }
            return ResponseEntity.ok(projectInstructorRoles);
        } catch (UsernameNotFoundException ex) {
            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
        }
    }

    // ดึงข้อมูลผู้ใช้เมื่อ login เข้าระบบ
    @GetMapping("/instructor/details")
    public ResponseEntity<Instructor> getInstructorUserDetails() {
        try {
            Instructor instructor = projectService.getInstructorUserDetails();
            return ResponseEntity.ok(instructor);
        } catch (UsernameNotFoundException ex) {
            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
        }
    }

    @GetMapping("/admin/details")
    @ResponseBody
    public ResponseEntity<Admin> getAdminUserDetails() {
        try {
            Admin admin = projectService.getAdminUserDetails();
            return ResponseEntity.ok(admin);
        } catch (UsernameNotFoundException ex) {
            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
        }
    }
}