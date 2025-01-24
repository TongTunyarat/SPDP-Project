package com.example.project.service;

import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.ProposalEvalScore;
import com.example.project.entity.ProposalEvaluation;
import com.example.project.repository.GradingProposalEvaluationRepository;
import com.example.project.repository.ProposalEvalScoreRepository;
import com.example.project.repository.ProposalEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProposalEvaluationService {

    private final GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    private final ProposalEvaluationRepository proposalEvaluationRepository;

    private final ProposalEvalScoreRepository proposalEvalScoreRepository;

    public ProposalEvaluationService(GradingProposalEvaluationRepository gradingProposalEvaluationRepository, ProposalEvaluationRepository proposalEvaluationRepository, ProposalEvalScoreRepository proposalEvalScoreRepository) {
        this.gradingProposalEvaluationRepository = gradingProposalEvaluationRepository;
        this.proposalEvaluationRepository = proposalEvaluationRepository;
        this.proposalEvalScoreRepository = proposalEvalScoreRepository;
    }

    @Autowired


    // grading
    public List<GradingProposalEvaluation> getAllGrade() {
        return gradingProposalEvaluationRepository.findAll();
    }

    public List<ProposalEvaluation> getAllEvaulation() {
        return proposalEvaluationRepository.findAll();
    }

    public List<ProposalEvalScore> getAllEvalScore() {
        return proposalEvalScoreRepository.findAll();
    }
}
