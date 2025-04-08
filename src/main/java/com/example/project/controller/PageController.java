package com.example.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class PageController {

    // ---------- Show Projects ---------- //
    @GetMapping("/dashboard-instructors")
    public String index() {
        return "DashboardInstructor";
    }

    // ---------- Admin ---------- //
    @GetMapping("/scoring-periods")
    public String scoringPeriods() {
        return "ScorePeriodSettings";
    }

    // ---------- Admin - Dashboard ---------- //
    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "/Dashboard&Report/Dashboard";
    }

    // ---------- Admin - Report ---------- //
    @GetMapping("/admin-proposal-report")
    public String adminPropReport() { return "/Dashboard&Report/ProposalGradeReport"; }
    @GetMapping("/admin-defense-report")
    public String adminDefReport() { return "/Dashboard&Report/DefenseGradeReport"; }
    @GetMapping("/admin-eva-traking")
    public String adminEvaTrack() { return "/Dashboard&Report/EvaluationTracking"; }



    // ---------- Show Projects ---------- //
    @GetMapping("/show-proposal-eva-projects")
    public String showProposalEvaProject() {
        return "/ShowProjects/ShowProposalEvaProject";
    }

    @GetMapping("/show-proposal-grade-projects")
    public String showProposalGradeProject() {
        return "/ShowProjects/ShowProposalGradeProject";
    }

    @GetMapping("/show-poster-projects")
    public String showPosterEvaProject() {
        return "/ShowProjects/ShowPosterEvaProject";
    }

    @GetMapping("/show-defense-eva-projects")
    public String showDefenseEvaProject() {
        return "/ShowProjects/ShowDefenseEvaProject";
    }

    @GetMapping("/show-defense-grade-projects")
    public String showDefenseGradeProject() {
        return "/ShowProjects/ShowDefenseGradeProject";
    }


    @GetMapping("/give-poster-eva-projects")
    public String givePosterEvaScore() {
        return "GiveScore/GivePosterScore";
    }


    // ---------- Give Score Projects ---------- //
    @GetMapping("/give-defense-eva-projects")
    public String giveDefenseEvaScore() {
        return "GiveScore/GiveDefEvaScore";
    }

    @GetMapping("/give-prop-grade-projects")
    public String giveProposalGradeScore() {
        return "GiveScore/GivePropGradeScore";
    }

    @GetMapping("/give-def-grade-projects")
    public String giveDefenseGradeScore() {
        return "GiveScore/GiveDefGradeScore";
    }


}

