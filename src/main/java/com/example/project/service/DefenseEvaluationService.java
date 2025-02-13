package com.example.project.service;

import com.example.project.entity.*;
import com.example.project.repository.CriteriaRepository;
import com.example.project.repository.DefenseEvaluationRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.StudentProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public DefenseEvaluationService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository, DefenseEvaluationRepository defenseEvaluationRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
        this.defenseEvaluationRepository = defenseEvaluationRepository;
    }

    //=========================================== USE ===================================================

    // get proposal criteria
    public List<Criteria> getDefenseCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Defense Evaluation");
//        List<Criteria> criteriaList = criteriaRepository.findAll();
        return criteriaList;
    }

    // get student criteria
    public List<StudentProject> getStudentCriteria(String projectId) {

        Project project = projectRepository.findByProjectId(projectId);

        List<StudentProject> studentProjectList = project.getStudentProjects();
        return studentProjectList;
    }


}