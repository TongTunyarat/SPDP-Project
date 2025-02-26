package com.example.project.service;

import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosterEvaluationService {
    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PosterEvaRepository posterEvaRepository;
    @Autowired
    private PosterEvaScoreRepository posterEvaScoreRepository;

    public PosterEvaluationService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository, PosterEvaRepository posterEvaRepository, PosterEvaScoreRepository posterEvaScoreRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
        this.posterEvaRepository = posterEvaRepository;
        this.posterEvaScoreRepository = posterEvaScoreRepository;
    }

//=========================================== USE ===================================================

    // get proposal criteria
    public List<Criteria> getPosterCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Poster Evaluation");
//        List<Criteria> criteriaList = criteriaRepository.findAll();
        return criteriaList;
    }

    // get student criteria
//    public List<StudentProject> getStudentCriteria(String projectId) {
//
//        Project project = projectRepository.findByProjectId(projectId);
//
//        List<StudentProject> studentProjectList = project.getStudentProjects();
//        return studentProjectList;
//    }

    // get student score
    public List<PosterEvaluationScore> getPosterEvalScoresByProjectId(String projectId) {

        return posterEvaScoreRepository.findByPosterEvaluation_ProjectIdPoster_ProjectId(projectId);
    }

}