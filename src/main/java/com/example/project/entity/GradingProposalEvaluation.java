package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Entity
@Table(name="gradingproposalevaluation")
public class GradingProposalEvaluation {

   @Id
   @Column(name="proposal_grade_id")
    private String proposalGradeId;

    @Column(name="datetime")
    private LocalDateTime dateTime;

    @Column(name="avg_score_proposal")
    private BigDecimal avgScoreProposal;

    @Column(name="evaluate_score")
    private BigDecimal evaluateScore;

    @Column(name="total_score")
    private BigDecimal totalScore;

    @Column(name="grade_result")
    private String gradeResult;

   // map for project
    @ManyToOne
    @JoinColumn(name="project_id")
    @JsonManagedReference
    private Project project;

    // map for student
    @OneToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private Student student;


    public GradingProposalEvaluation() {

    }

 @Override
 public String toString() {
  return "GradingProposalEvaluation{" +
          "proposalGradeId='" + proposalGradeId + '\'' +
          ", dateTime=" + dateTime +
          ", avgScoreProposal=" + avgScoreProposal +
          ", evaluateScore=" + evaluateScore +
          ", totalScore=" + totalScore +
          ", gradeResult='" + gradeResult + '\'' +
          ", project=" + project +
          ", student=" + student +
          '}';
 }

 public String getProposalGradeId() {
     return proposalGradeId;
    }

    public void setProposalGradeId(String proposalGradeId) {
     this.proposalGradeId = proposalGradeId;
    }

    public LocalDateTime getDateTime() {
     return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
     this.dateTime = dateTime;
    }

    public BigDecimal getAvgScoreProposal() {
     return avgScoreProposal;
    }

    public void setAvgScoreProposal(BigDecimal avgScoreProposal) {
     this.avgScoreProposal = avgScoreProposal;
    }

    public BigDecimal getEvaluateScore() {
     return evaluateScore;
    }

    public void setEvaluateScore(BigDecimal evaluateScore) {
     this.evaluateScore = evaluateScore;
    }

    public BigDecimal getTotalScore() {
     return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
     this.totalScore = totalScore;
    }

    public String getGradeResult() {
     return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
     this.gradeResult = gradeResult;
    }

    public Project getProject() {
     return project;
    }

    public void setProject(Project project) {
     this.project = project;
    }

    public Student getStudent() {
  return student;
 }

    public void setStudent(Student student) {
  this.student = student;
 }
}
