package com.example.project.controller;

import com.example.project.DTO.Criteria.ShowProposalCriteriaDTO;
import com.example.project.DTO.PosterEvaScoreDTO;
import com.example.project.DTO.ProposalEvalScoreDTO;
import com.example.project.entity.Criteria;
import com.example.project.entity.PosterEvaluation;
import com.example.project.entity.PosterEvaluationScore;
import com.example.project.entity.ProposalEvalScore;
import com.example.project.repository.CriteriaRepository;
import com.example.project.repository.PosterEvaScoreRepository;
import com.example.project.service.PosterEvaluationService;
import com.example.project.service.ProposalEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Locale.filter;

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
    @Autowired
    private PosterEvaScoreRepository posterEvaScoreRepository;
    @Autowired
    private CriteriaRepository criteriaRepository;
    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    public PosterEvaluationController(PosterEvaluationService posterEvaluationService, PosterEvaScoreRepository posterEvaScoreRepository) {
        this.posterEvaluationService = posterEvaluationService;
        this.posterEvaScoreRepository = posterEvaScoreRepository;
    }

    // get criteria DTO
    @GetMapping("/instructor/criteriaPoster")
    @ResponseBody
    public List<ShowProposalCriteriaDTO> getCriteriaDTO() {

        List<Criteria> criteriaList = posterEvaluationService.getPosterCriteria();

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

    //     get score DTO
    @GetMapping("/instructor/showScorePoster")
    @ResponseBody
    public List<PosterEvaScoreDTO> getScorePoster(@RequestParam String projectId) {
        // ดึงข้อมูล ProposalEvalScore ตาม projectId
        List<PosterEvaluationScore> posterEvaluationList = posterEvaluationService.getPosterEvalScoresByProjectId(projectId);

        // ดึงข้อมูล ProposalEvalScore ตาม projectId
        List<ProposalEvalScore> proposalEvalScoreList = proposalEvaluationService.getProposalEvalScoresByProjectId(projectId);

        return posterEvaluationList.stream()
                .map(score -> {
                    // ดึงคะแนนจาก score
                    Double scoreValue = (double) score.getScore();

                    return new PosterEvaScoreDTO(
                            score.getPosterEvaId(),
                            score.getPosterEvaluation().getPosterId(),
                            score.getCriteriaPoster().getCriteriaId(),
                            score.getCriteriaPoster().getCriteriaName(),
                            scoreValue,
                            score.getCriteriaPoster().getWeight(),
                            score.getCriteriaPoster().getMaxScore()
                    );

                }).collect(Collectors.toList());
    }

}