package com.example.project.DTO.Dashboard;


import com.example.project.entity.*;

import java.util.List;

public class GradingStatisticsDTO {

    private long totalGroups;
    private long completedProposals;
    private long completedDefenses;
    private List<Student> student;
    private List<GradingProposalEvaluation> proposalEvaluation;
    private List<GradingDefenseEvaluation> defenseEvaluation;

    public GradingStatisticsDTO(long totalGroups, long completedProposals, long completedDefenses, List<Student> student, List<GradingProposalEvaluation> proposalEvaluation, List<GradingDefenseEvaluation> defenseEvaluation) {
        this.totalGroups = totalGroups;
        this.completedProposals = completedProposals;
        this.completedDefenses = completedDefenses;
        this.student = student;
        this.proposalEvaluation = proposalEvaluation;
        this.defenseEvaluation = defenseEvaluation;
    }

    public long getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(long totalGroups) {
        this.totalGroups = totalGroups;
    }

    public long getCompletedProposals() {
        return completedProposals;
    }

    public void setCompletedProposals(long completedProposals) {
        this.completedProposals = completedProposals;
    }

    public long getCompletedDefenses() {
        return completedDefenses;
    }

    public void setCompletedDefenses(long completedDefenses) {
        this.completedDefenses = completedDefenses;
    }

    public List<Student> getStudent() {
        return student;
    }

    public void setStudent(List<Student> student) {
        this.student = student;
    }

    public List<GradingProposalEvaluation> getProposalEvaluation() {
        return proposalEvaluation;
    }

    public void setProposalEvaluation(List<GradingProposalEvaluation> proposalEvaluation) {
        this.proposalEvaluation = proposalEvaluation;
    }

    public List<GradingDefenseEvaluation> getDefenseEvaluation() {
        return defenseEvaluation;
    }

    public void setDefenseEvaluation(List<GradingDefenseEvaluation> defenseEvaluation) {
        this.defenseEvaluation = defenseEvaluation;
    }
}
