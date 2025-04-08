package com.example.project.DTO.ManageSchedule;

import com.example.project.entity.Project;

import java.util.List;

public class ProjectWithInstructorsDTO {

    private Project project;
    private List<String> instructorUsernames;

    public ProjectWithInstructorsDTO(Project project, List<String> instructorUsernames) {
        this.project = project;
        this.instructorUsernames = instructorUsernames;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<String> getInstructorUsernames() {
        return instructorUsernames;
    }

    public void setInstructorUsernames(List<String> instructorUsernames) {
        this.instructorUsernames = instructorUsernames;
    }

    @Override
    public String toString() {
        return "ProjectWithInstructorsDTO{" +
                "project=" + project.getProjectId() +
                ", instructorUsernames=" + instructorUsernames +
                '}';
    }
}
