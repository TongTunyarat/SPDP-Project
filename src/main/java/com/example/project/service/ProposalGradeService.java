package com.example.project.service;

import com.example.project.entity.Criteria;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;
import com.example.project.repository.CriteriaRepository;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.StudentProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProposalGradeService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public ProposalGradeService(CriteriaRepository criteriaRepository, StudentProjectRepository studentProjectRepository, ProjectRepository projectRepository) {
        this.criteriaRepository = criteriaRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.projectRepository = projectRepository;
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