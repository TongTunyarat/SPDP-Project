package com.example.project.controller;

import com.example.project.DTO.ShowProposalCriteriaDTO;
import com.example.project.entity.Criteria;
import com.example.project.service.PosterEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PosterEvaluationController {
    //=========================================== USE ===================================================

    // ShowPosterEvaProject
//    @GetMapping("/instructor/ShowPosterEvaProject")
//    public String getGiveGradePage() {
//        return "ShowPosterEvaProject";
//    }

    // send project when click edit
//    @GetMapping("/instructor/editGivePosterScore")
//    public String getProjectDetails(@RequestParam String projectId, Model model){
//        System.out.println("ID from click edit: "+ projectId);
//
//        model.addAttribute("projectId", projectId);
//        return "/GiveScore/GivePosterScore";
//    }

    @Autowired
    private PosterEvaluationService posterEvaluationService;

    public PosterEvaluationController(PosterEvaluationService posterEvaluationService) {
        this.posterEvaluationService = posterEvaluationService;
    }

    // get criteria DTO
    @GetMapping("/instructor/criteriaPoster")
    @ResponseBody
    public List<ShowProposalCriteriaDTO> getCriteriaDTO() {

        List<Criteria> criteriaList = posterEvaluationService.getProposalCriteria();

        return criteriaList.stream()
                .map(criteria ->
                        new ShowProposalCriteriaDTO(
                                criteria.getCriteriaId(),
                                criteria.getCriteriaName(),
                                criteria.getCriteriaNameTH(),
                                criteria.getMaxScore(),
                                criteria.getType()
                        )).collect(Collectors.toList());
    }

}