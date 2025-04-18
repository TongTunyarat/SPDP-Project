package com.example.project.service;

import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefenseEvaluationService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;
    @Autowired
    private DefenseEvalScoreRepository defenseEvalScoreRepository;

    public DefenseEvaluationService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository, DefenseEvaluationRepository defenseEvaluationRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
        this.defenseEvaluationRepository = defenseEvaluationRepository;
        this.defenseEvalScoreRepository = defenseEvalScoreRepository;
    }

    //=========================================== USE ===================================================

    // get proposal criteria
    public List<Criteria> getDefenseCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Defense Evaluation")
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

    public List<DefenseEvalScore> getDefenseEvalScoresByProjectId(String projectId) {
        return defenseEvalScoreRepository.findByDefenseEvaluation_ProjectId_ProjectId(projectId);
    }

    // get student score
//    public List<DefenseEvalScore> getDefenseEvaScoresByProjectId(String projectId) {
//        // สมมุติว่า ProposalEvalScore มีการเชื่อมโยงกับ ProposalEvaluation และ Project
//        return defenseEvalScoreRepository.findByDefenseEvaluation_ProjectId_ProjectId(projectId);
//    }


}