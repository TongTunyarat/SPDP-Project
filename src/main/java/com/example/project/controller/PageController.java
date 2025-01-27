package com.example.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scoring-periods")
public class PageController {

    @GetMapping
    public String index() {
        return "ScorePeriodSettings";
    }
}

