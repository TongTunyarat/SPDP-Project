package com.example.project.service.ManageSchedule.DefenseSchedule;

import com.example.project.repository.DefenseSchedRepository;
import com.example.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageDefenseService {

    @Autowired
    DefenseSchedRepository defenseSchedRepository;
    @Autowired
    ProjectRepository projectRepository;

    // delete all
    public boolean deleteAllDefenseSchedule() {

        List<String> projectIds = projectRepository.findByProjectIdList();

        System.out.print("🪸List delete project: " + projectIds);

        if(projectIds.isEmpty()) {
            System.out.println("No projects found ");
            return false;
        }

        int deletedCount =  defenseSchedRepository.deleteAllByProgram(projectIds);

        return deletedCount > 0;

    }
}
