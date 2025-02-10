package com.example.project.service;


import com.example.project.entity.Criteria;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import com.example.project.entity.StudentProject;
import com.example.project.repository.CriteriaRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.StudentProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProposalEvaluationService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public ProposalEvaluationService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
    }

    //=========================================== USE ===================================================

    // get proposal criteria
    public List<Criteria> getProposalCriteria() {
        List<Criteria> criteriaList = criteriaRepository.findByEvaluationPhase("Proposal Evaluation");
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