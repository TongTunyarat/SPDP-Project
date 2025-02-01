package com.example.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/scoring-periods")
public class PageController {

    @GetMapping("/scoring-periods")
    public String index() {
        return "ScorePeriodSettings";
    }

    @GetMapping("/eva")
    public String eva() {
        return "ProposalEva";
    }
}

