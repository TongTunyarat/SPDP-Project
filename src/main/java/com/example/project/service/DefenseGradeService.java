package com.example.project.service;

import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefenseGradeService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    private DefenseEvalScoreRepository defenseEvalScoreRepository;

    public DefenseGradeService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
    }

    // get defense criteria
    public List<Criteria> getDefenseCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Defense Grading");
//        List<Criteria> criteriaList = criteriaRepository.findAll();
        return criteriaList;
    }

    public List<ProjectInstructorRole> getInstructorProject(String projectId) {
        List<ProjectInstructorRole> instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
        return instructorRoles;
    }

    // get student criteria
    public List<StudentProject> getStudentCriteria(String projectId) {

        Project project = projectRepository.findByProjectId(projectId);

        List<StudentProject> studentProjectList = project.getStudentProjects();
        return studentProjectList;
    }

    // getProposalEvaScore
    public List<DefenseEvalScore> getFilterDefenseEvaScore(String projectId, String instructorName, String role) {

        List<DefenseEvaluation> defenselEvaluationList = defenseEvaluationRepository.findByProjectId_ProjectId(projectId);

        if (defenselEvaluationList.isEmpty()) {
            System.out.println("Hi not found for projectId: " + projectId);
            return Collections.emptyList();
        }

        return defenselEvaluationList.stream()
                .flatMap(evaluation -> evaluation.getDefenseEvalScore().stream())
                .filter(score ->
                        score.getDefenseEvaluation()
                                .getDefenseInstructorId()
                                .getInstructor()
                                .getProfessorName().equals(instructorName) &&
                                score.getDefenseEvaluation()
                                        .getDefenseInstructorId().getRole().equals(role)
                ).collect(Collectors.toList());
    }

    // get student score
    public List<DefenseEvalScore> getDefenseEvalScoresByProjectId(String projectId) {
        return defenseEvalScoreRepository.findByDefenseEvaluation_ProjectId_ProjectId(projectId);
    }
}