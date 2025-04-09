package com.example.project.service;

import com.example.project.entity.*;
import com.example.project.repository.*;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalGradeService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public ProposalGradeService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository, ProposalEvaluationRepository proposalEvaluationRepository, ProjectInstructorRoleRepository projectInstructorRoleRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
        this.proposalEvaluationRepository = proposalEvaluationRepository;
        this.projectInstructorRoleRepository = projectInstructorRoleRepository;
    }

    // get proposal criteria
    public List<Criteria> getProposalCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Proposal Grading");
//        List<Criteria> criteriaList = criteriaRepository.findAll();
        return criteriaList;
    }


    public List<ProjectInstructorRole> getInstructorProject(String projectId) {
        List<ProjectInstructorRole> instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
        return instructorRoles;
    }


    // getProposalEvaScore
    public List<ProposalEvalScore> getFilterProposalEvaScore(String projectId,String instructorName, String role) {

        List<ProposalEvaluation> proposalEvaluationList = proposalEvaluationRepository.findByProject_ProjectId(projectId);

        if (proposalEvaluationList.isEmpty()) {
            System.out.println("Hi not found for projectId: " + projectId);
            return Collections.emptyList();
        }

        return proposalEvaluationList.stream()
                .flatMap(evaluation -> evaluation.getProposalEvalScores().stream())
                .filter(score ->
                        score.getProposalEvaluation()
                                .getProjectInstructorRole()
                                .getInstructor()
                                .getProfessorName().equals(instructorName) &&
                        score.getProposalEvaluation()
                                .getProjectInstructorRole().getRole().equals(role)
                ).collect(Collectors.toList());
    }












    //=========================================== NOT USE ===================================================
    // get student criteria
//    public List<StudentProject> getStudentCriteria(String projectId) {
//
//        Project project = projectRepository.findByProjectId(projectId);
//
//        List<StudentProject> studentProjectList = project.getStudentProjects();
//        return studentProjectList;
//    }
}