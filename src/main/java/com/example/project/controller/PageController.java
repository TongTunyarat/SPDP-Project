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

    @GetMapping("/show-projects")
    public String showProject() {
        return "ShowProposalEvaProject";
    }
}

