package com.example.project.service;


import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalEvaluationService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private final ProposalEvaluationRepository proposalEvaluationRepository;

    @Autowired
    private final ProposalEvalScoreRepository proposalEvalScoreRepository;

    @Autowired
    public ProposalEvaluationService(ProposalEvaluationRepository proposalEvaluationRepository, ProposalEvalScoreRepository proposalEvalScoreRepository) {
        this.proposalEvaluationRepository = proposalEvaluationRepository;
        this.proposalEvalScoreRepository = proposalEvalScoreRepository;
    }

    public ProposalEvaluationService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository, ProposalEvaluationRepository proposalEvaluationRepository, ProposalEvalScoreRepository proposalEvalScoreRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
        this.proposalEvaluationRepository = proposalEvaluationRepository;
        this.proposalEvalScoreRepository = proposalEvalScoreRepository;
    }

    //=========================================== USE ===================================================

    // get proposal criteria
    public List<Criteria> getProposalCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Proposal Evaluation")
                .stream().filter(name -> !"Timeliness".equalsIgnoreCase(name.getCriteriaName())).collect(Collectors.toList());
//        List<Criteria> criteriaList = criteriaRepository.findAll();
        return criteriaList;
    }

    // get student criteria
    public List<StudentProject> getStudentCriteria(String projectId) {

        Project project = projectRepository.findByProjectId(projectId);

        List<StudentProject> studentProjectList = project.getStudentProjects();
        return studentProjectList;
    }

   // get student score
   public List<ProposalEvalScore> getProposalEvalScoresByProjectId(String projectId) {
       // สมมุติว่า ProposalEvalScore มีการเชื่อมโยงกับ ProposalEvaluation และ Project
       return proposalEvalScoreRepository.findByProposalEvaluation_Project_ProjectId(projectId);
   }


}