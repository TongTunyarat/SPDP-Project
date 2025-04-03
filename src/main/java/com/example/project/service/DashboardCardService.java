package com.example.project.service;

import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private ProposalSchedRepository proposalSchedRepository;

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

//                                boolean hasAllGradeScores = false; // default
//                                if ("Advisor".equalsIgnoreCase(i.getRole())) {
//
//                                    Optional<GradingProposalEvaluation> gradingEvaluation = gradingProposalEvaluationList.stream()
//                                            .filter(g -> g.getStudent().getStudentId().equals(student.getStudent().getStudentId()))
//                                            .findFirst();
//
//                                    hasAllGradeScores = gradingEvaluation.map(gradingProposalEvaluation ->
//                                                    gradingProposalEvaluation.getEvaluateScore() != null)
//                                            .orElse(false);
//                                }
//
//                                return "Advisor".equalsIgnoreCase(i.getRole())
//                                        ? (hasAllEvaScores && hasAllGradeScores)
//                                        : hasAllEvaScores;
                                return  hasAllEvaScores;
                            });

                    return allStudentsComplete;
                }).count(); // all project success

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

//    public void getProposalSchedule() {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        System.out.println("💌 Account username: " + username);
//
//        List<Project> ProjectList = projectRepository.;
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
//        // get project
//        List<ProposalSchedule> proposalSchedulesPreview = proposalSchedRepository.findPreviewProject(projectIdsWithMaxSemester);
//
//
//    }

}
