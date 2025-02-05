package com.example.project.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class PageController {

    @GetMapping("/scoring-periods")
    public String index() {
        return "ScorePeriodSettings";
    }

    @GetMapping("/show-proposal-eva-projects")
    public String showProposalEvaProject() {
        return "ShowProposalEvaProject";
    }

    @GetMapping("/show-poster-projects")
    public String showPosterEvaProject() {
        return "ShowPosterEvaProject";
    }

    @GetMapping("/give-poster-eva-projects")
    public String givePosterEvaScore() {
        return "GiveScore/GivePosterScore";
    }

    @GetMapping("/show-defense-eva-projects")
    public String showDefenseEvaProject() {
        return "ShowDefenseEvaProject";
    }

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

