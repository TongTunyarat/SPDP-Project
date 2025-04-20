package com.example.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/admin-timeliness-scoring")
    public String timelinessScoring() {return "/GiveScore/GiveTimelineScore"; }
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


    // ---------- Project Management ---------- //
    @GetMapping("/project-overview")
    public String projectOverview() {return "ProjectManagement/projectOverview";}

    @GetMapping("/project-committee")
    public String projectCommittee() {return "ProjectManagement/projectCommittee";}

    @GetMapping("/project-details")
    public String projectDetails() {return "ProjectManagement/projectDetails";}

    @GetMapping("/project-poster-committee")
    public String projectPosterCommittee() {return "ProjectManagement/projectPosterCommittee";}

    @GetMapping("/project-add-new-project")
    public String projectAddNewProject() {return "ProjectManagement/addNewProject";}

    @GetMapping("/project-edit-committee")
    public String projectEditCommittee() {return "ProjectManagement/editCommittee";}

    @GetMapping("/project-edit-poster-committee")
    public String projectEditPosterCommittee() {return "ProjectManagement/editPosterCommittee";}

    @GetMapping("/project-edit-project")
    public String projectEditProject() {return "ProjectManagement/editProjectDetails";}

    // -------------------- Project Management -------------------- //
    // ---------- (send project when click edit) ---------- //
    @GetMapping("/admin/editProjectDetails")
    public String getEditProjectDetails(@RequestParam String projectId, Model model) {

        model.addAttribute("projectId", projectId);

        return "ProjectManagement/editProjectDetails";
    }

    @GetMapping("/admin/editCommittee")
    public String getEditCommittee(@RequestParam String projectId, Model model) {

        model.addAttribute("projectId", projectId);

        return "ProjectManagement/editCommittee";
    }

}

