package com.example.project.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterResponseDTO {

    @JsonProperty("studentId")
    private String studentId;

    @JsonProperty("studentName")
    private String studentName;

    @JsonProperty("program")
    private String program;

    @JsonProperty("role")
    private String role;

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("projectTitle")
    private String projectTitle;

    // Constructor
    public FilterResponseDTO(String studentId,String studentName, String program, String role, String projectId, String projectTitle) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.program = program;
        this.role = role;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
    }
}

