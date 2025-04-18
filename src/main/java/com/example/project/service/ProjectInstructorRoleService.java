package com.example.project.service;

import com.example.project.DTO.InstructorProjectListDTO;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.repository.ProjectInstructorRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInstructorRoleService {

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public List<ProjectInstructorRole> findByProjectId(String projectId) {
        return projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
    }

    // บันทึกบทบาทของอาจารย์ในโปรเจกต์
    public void saveProfessorRole(ProjectInstructorRole role) {
        projectInstructorRoleRepository.save(role);
    }



}
