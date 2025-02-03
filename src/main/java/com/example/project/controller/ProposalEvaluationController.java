package com.example.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProposalEvaluationController {


    // send project when click edit
    @GetMapping("/instructor/editProposalEvaluation")
    public String getProjectDetails(@RequestParam String projectId){
        return "GivePropEvaScore";
    }
}
