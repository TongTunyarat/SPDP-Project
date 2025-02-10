package com.example.project.service;

import com.example.project.DTO.FilterResponseDTO;
import com.example.project.entity.Project;
import com.example.project.repository.FilterRepository;
import com.example.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowProjectService {

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<FilterResponseDTO> filterData(String role, String program) {
        System.out.println("Filter Word Role: " + role + "\nProgram: " + program);
        return filterRepository.findFilteredData(role, program);
    }

    public List<Project> processWord(String word) {
        System.out.println("Search Word [Service]: " + word);
        return projectRepository.searchProjects(word);
    }
}