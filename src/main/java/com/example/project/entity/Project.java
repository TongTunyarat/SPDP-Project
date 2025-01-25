//package com.example.project.entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//// <<<<<<< Nref
//import java.util.List;
//// =======
//// >>>>>>> Tong
//
//@Data
//@Entity
//@Table(name = "Project")
//public class Project {
//
//    @Id
//    @Column(name = "project_id")
//    private String projectId;
//
//    @Column(name = "program")
//    private String program;
//
//    @Column(name = "semester")
//    private String semester;
//
//    @Column(name = "project_title")
//    private String projectTitle;
//
//    @Column(name = "project_category")
//    private String projectCategory;
//
//    @Column(name = "project_description")
//    private String projectDescription;
//
//    @Column(name = "recorded_on")
//    private LocalDateTime recordedOn;
//
//    @Column(name = "edited_on")
//    private LocalDateTime editedOn;
//
////    @ManyToOne
////    @JsonBackReference
////    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
////    private Account recordedBy;
////
////    @ManyToOne
////    @JsonBackReference
////    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
////    private Account editedBy;
////
////    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
////    @JsonBackReference
////    private List<ProjectInstructorRole> projectInstructorRoles;
////
////    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
////    @JsonBackReference
////    private List<StudentProject> studentProjects;
////
////    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
////    @JsonBackReference
////    private List<GradingProposalEvaluation> gradingProposalEvaluations;
////
////    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
////    @JsonBackReference
////    private List<ProposalEvaluation> proposalEvaluations;
//
//    @ManyToOne
//    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
//    private Account editedBy;
//
//}
//
