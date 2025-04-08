package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="student")
public class Student {

    @Id
    @Column(name="student_id")
    private String studentId;

    @Column(name="student_name")
    private String studentName;

    @Column(name="program")
    private String program;

    @Column(name="section")
    private Byte section;

    @Column(name="track")
    private String track;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;


    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentProject> studentProjects;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonBackReference
    private GradingProposalEvaluation gradingProposalEvaluation;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvaluation> proposalEvaluations;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseEvaluation> defenseEvaluations;

    public Student() {

    }

    public Student(String studentId, String studentName, String program, Byte section, String track, String email, String phone, List<StudentProject> studentProjects) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.program = program;
        this.section = section;
        this.track = track;
        this.email = email;
        this.phone = phone;
        this.studentProjects = studentProjects;
    }

    public Student(String studentId, String studentName, String program, String section, String track, Project project) {
    }

    public Student(String studentId, String studentName, String program, String section, String track) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.program = program;
        this.section = Byte.valueOf(section);
        this.track = track;
    }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Byte getSection() {
        return section;
    }

    public void setSection(Byte section) {
        this.section = section;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<StudentProject> getStudentProjects() {
        return studentProjects;
    }

    public void setStudentProjects(List<StudentProject> studentProjects) {
        this.studentProjects = studentProjects;
    }

    public GradingProposalEvaluation getGradingProposalEvaluation() {
        return gradingProposalEvaluation;
    }

    public void setGradingProposalEvaluation(GradingProposalEvaluation gradingProposalEvaluation) {
        this.gradingProposalEvaluation = gradingProposalEvaluation;
    }

    public List<ProposalEvaluation> getProposalEvaluations() {
        return proposalEvaluations;
    }

    public void setProposalEvaluations(List<ProposalEvaluation> proposalEvaluations) {
        this.proposalEvaluations = proposalEvaluations;
    }

    public List<DefenseEvaluation> getDefenseEvaluations() {
        return defenseEvaluations;
    }

    public void setDefenseEvaluations(List<DefenseEvaluation> defenseEvaluations) {
        this.defenseEvaluations = defenseEvaluations;
    }


    public void setStatus(String active) {

    }
}
