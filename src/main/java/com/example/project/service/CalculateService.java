package com.example.project.service;

import com.example.project.DTO.Score.ScoreDTO;
import com.example.project.DTO.Score.ScoreDetail;
import com.example.project.DTO.Score.StudentScoreDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculateService {

    @Autowired
    private ProposalEvaluationRepository evaluationRepository;
    @Autowired
    private ProposalEvalScoreRepository scoreRepository;
    @Autowired
    private CriteriaRepository criteriaRepository;
    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;
    @Autowired
    private DefenseEvalScoreRepository defenseEvalScoreRepository;
    @Autowired
    private PosterEvaRepository posterEvaRepository;
    @Autowired
    private PosterEvaScoreRepository posterEvaScoreRepository;
    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;




    @Transactional
    public void saveEvaluation(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores) {
        System.out.println("[Service] Inside saveEvaluation");

        // ค้นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูลตาม instructor, project และ student
        ProposalEvaluation evaluation = evaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(instructor, project, student);

        // ถ้าไม่มี ProposalEvaluation อยู่แล้ว ให้สร้างใหม่
        if (evaluation == null) {
            evaluation = new ProposalEvaluation()
                    .setProjectInstructorRole(instructor)
                    .setProject(project)
                    .setStudent(student);

            // บันทึก ProposalEvaluation ใหม่
            evaluation = evaluationRepository.save(evaluation);
            if (evaluation.getProposalId() == null) {
                throw new IllegalArgumentException("Proposal ID cannot be null");
            }
            System.out.println("[Service] Saved ProposalEvaluation with ID: " + evaluation.getProposalId());
        } else {
            // ถ้ามีอยู่แล้ว ให้ทำการ update หรือเพิ่มการอัปเดตที่ต้องการ
            System.out.println("[Service] Found existing ProposalEvaluation with ID: " + evaluation.getProposalId());
        }

        String proposalId = evaluation.getProposalId();

        // บันทึกหรืออัปเดตคะแนนลง ProposalEvalScore
        for (ScoreDTO score : scores) {
            // ดึง Criteria จากฐานข้อมูลตาม scoreCriteriaId
            System.out.println("[Service] Score: " + score.getScore());
            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());

            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));

            // ค้นหาคะแนนที่มีอยู่แล้วในฐานข้อมูล (ใช้ evaId เป็น key)
            ProposalEvalScore evalScore = scoreRepository.findByEvaId(proposalId + "_" + score.getScoreCriteriaId());

            if (evalScore == null) {
                // ถ้ายังไม่มีคะแนนในฐานข้อมูล ให้ทำการสร้างใหม่
                evalScore = new ProposalEvalScore()
                        .setEvaId(proposalId + "_" + score.getScoreCriteriaId())
                        .setScore(score.getScore())
                        .setCriteria(criteria)
                        .setProposalEvaluation(evaluation);

                // บันทึกคะแนนใหม่
                scoreRepository.save(evalScore);
                System.out.println("[Service] Saved ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
            } else {
                // ถ้ามีคะแนนอยู่แล้วในฐานข้อมูล ให้ทำการอัปเดต
                evalScore.setScore(score.getScore());
                scoreRepository.save(evalScore);
                System.out.println("[Service] Updated ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
            }
        }
    }

    @Transactional
    public void saveDefenseEvaluation(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores) {
        System.out.println("[Service] Inside saveDefenseEvaluation");

        // ค้นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูลตาม instructor, project และ student
        DefenseEvaluation evaluation = defenseEvaluationRepository.findByDefenseInstructorIdAndProjectIdAndStudentDefense(instructor, project, student);

        // ถ้าไม่มี ProposalEvaluation อยู่แล้ว ให้สร้างใหม่
        if (evaluation == null) {
            evaluation = new DefenseEvaluation()
                    .setDefenseInstructorId(instructor)
                    .setProjectId (project)
                    .setStudentDefense(student);

            // บันทึก ProposalEvaluation ใหม่
            evaluation = defenseEvaluationRepository.save(evaluation);
            if (evaluation.getDefenseEvaId() == null) {
                throw new IllegalArgumentException("Defense ID cannot be null");
            }
            System.out.println("[Service] Saved DefenseEvaluation with ID: " + evaluation.getDefenseEvaId());
        } else {
            // ถ้ามีอยู่แล้ว ให้ทำการ update หรือเพิ่มการอัปเดตที่ต้องการ
            System.out.println("[Service] Found existing DefenseEvaluation with ID: " + evaluation.getDefenseEvaId());
        }

        String defenseId = evaluation.getDefenseEvaId();

        // บันทึกหรืออัปเดตคะแนนลง ProposalEvalScore
        for (ScoreDTO score : scores) {
            // ดึง Criteria จากฐานข้อมูลตาม scoreCriteriaId
            System.out.println("[Service] Score: " + score.getScore());
            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());

            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));

            // ค้นหาคะแนนที่มีอยู่แล้วในฐานข้อมูล (ใช้ evaId เป็น key)
            DefenseEvalScore evalScore = defenseEvalScoreRepository.findByEvalId(defenseId + "_" + score.getScoreCriteriaId());

            if (evalScore == null) {
                // ถ้ายังไม่มีคะแนนในฐานข้อมูล ให้ทำการสร้างใหม่
                evalScore = new DefenseEvalScore()
                        .setEvalId(defenseId + "_" + score.getScoreCriteriaId())
                        .setScore(score.getScore().intValue())
                        .setCriteria(criteria)
                        .setDefenseEvaluation(evaluation);

                // บันทึกคะแนนใหม่
                defenseEvalScoreRepository.save(evalScore);
                System.out.println("[Service] Saved ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
            } else {
                // ถ้ามีคะแนนอยู่แล้วในฐานข้อมูล ให้ทำการอัปเดต
                evalScore.setScore(score.getScore().intValue());
                defenseEvalScoreRepository.save(evalScore);
                System.out.println("[Service] Updated ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
            }
        }
    }

    @Transactional
    public void savePosterEvaluation(ProjectInstructorRole instructor, Project project, List<ScoreDTO> scores) {
        System.out.println("[Service] Inside savePosterEvaluation");

        // ค้นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูลตาม instructor, project และ student
        PosterEvaluation evaluation = posterEvaRepository.findByInstructorIdPosterAndProjectIdPoster(instructor, project);

        // ถ้าไม่มี ProposalEvaluation อยู่แล้ว ให้สร้างใหม่
        if (evaluation == null) {
            evaluation = new PosterEvaluation()
                    .setInstructorIdPoster(instructor)
                    .setProjectIdPoster(project);

            // บันทึก PosterEvaluation ใหม่
            evaluation = posterEvaRepository.save(evaluation);
            if (evaluation.getPosterId() == null) {
                throw new IllegalArgumentException("PosterEvaluation ID cannot be null");
            }
            System.out.println("[Service] Saved PosterEvaluation with ID: " + evaluation.getPosterId());
        } else {
            // ถ้ามีอยู่แล้ว ให้ทำการ update หรือเพิ่มการอัปเดตที่ต้องการ
            System.out.println("[Service] Found existing PosterEvaluation with ID: " + evaluation.getPosterId());
        }

        String posterId = evaluation.getPosterId();

        // บันทึกหรืออัปเดตคะแนนลง ProposalEvalScore
        for (ScoreDTO score : scores) {
            // ดึง Criteria จากฐานข้อมูลตาม scoreCriteriaId
            System.out.println("[Service] Score: " + score.getScore());
            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());

            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));

            // ค้นหาคะแนนที่มีอยู่แล้วในฐานข้อมูล (ใช้ evaId เป็น key)
            PosterEvaluationScore evalScore = posterEvaScoreRepository.findByPosterEvaId(posterId + "_" + score.getScoreCriteriaId());

            if (evalScore == null) {
                // ถ้ายังไม่มีคะแนนในฐานข้อมูล ให้ทำการสร้างใหม่
                evalScore = new PosterEvaluationScore()
                        .setPosterEvaId(posterId + "_" + score.getScoreCriteriaId())
                        .setScore(score.getScore().intValue())
                        .setCriteriaPoster(criteria)
                        .setPosterEvaluation(evaluation);

                // บันทึกคะแนนใหม่
                posterEvaScoreRepository.save(evalScore);
                System.out.println("[Service] Saved PosterEvalScore for criteria: " + score.getScoreCriteriaId());
            } else {
                // ถ้ามีคะแนนอยู่แล้วในฐานข้อมูล ให้ทำการอัปเดต
                evalScore.setScore(score.getScore().intValue());
                posterEvaScoreRepository.save(evalScore);
                System.out.println("[Service] Updated PosterEvalScore for criteria: " + score.getScoreCriteriaId());
            }
        }
    }

    public StudentScoreDTO calculateTotalScorePoster(ProjectInstructorRole instructor, Project project) {
        PosterEvaluation evaluation = posterEvaRepository.findByInstructorIdPosterAndProjectIdPoster(instructor, project);

        if (evaluation == null) {
            throw new EntityNotFoundException("Poster evaluation not found");
        }

        double rawTotalScore = 0.0;
        List<ScoreDetail> scoreDetails = new ArrayList<>();

        for (PosterEvaluationScore score : evaluation.getPosterEvaluationScores()) {
            rawTotalScore += score.getScore(); // บวกคะแนนเต็มก่อนคิด %

            ScoreDetail detail = new ScoreDetail(score.getCriteriaPoster().getCriteriaId(), score.getScore());
            scoreDetails.add(detail);
        }

        double totalScore = rawTotalScore * 0.1; // แปลงเป็น 10%

        return new StudentScoreDTO(scoreDetails, rawTotalScore, totalScore);
    }

    public StudentScoreDTO calculateTotalScoreProposal(ProjectInstructorRole instructor, Project project, Student student) {
        ProposalEvaluation evaluation = evaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(instructor, project, student);

        if (evaluation == null) {
            throw new EntityNotFoundException("Proposal evaluation not found");
        }

        double rawTotalScore = 0.0;
        List<ScoreDetail> scoreDetails = new ArrayList<>();

        for (ProposalEvalScore score : evaluation.getProposalEvalScores()) {
            double weightedScore = score.getScore().doubleValue() * score.getCriteria().getWeight(); // แปลง BigDecimal เป็น double ก่อนคูณ
            rawTotalScore += weightedScore;

            ScoreDetail detail = new ScoreDetail(score.getCriteria().getCriteriaId(), weightedScore);
            scoreDetails.add(detail);
        }

        double totalScore = rawTotalScore * 0.4; // แปลงเป็น 40%

        return new StudentScoreDTO(scoreDetails, rawTotalScore, totalScore);
    }


//        ใน evaluation จะแสดง
//        {
//            "proposalId": "3ee7dec2-19ce-4564-a51b-1547a7d4aa87",
//            "comment": null,
//            "projectId": {
//                "projectId": "DST SP2024-04",
//            },
//            "instructorId": {
//                "instructorId": "INST002",
//                "projectIdRole":{
//                    "projectId":"DST SP2024-04",
//                }
//            },
//            "instructor": {
//                "professorId": "PROF001",
//                "professorName": "Aj.Akara",
//            },
//            "EvaluationScores": [
//                {
//                    "EvaId": "3ee7dec2-19ce-4564-a51b-1547a7d4aa87_CRIT013",
//                    "score": 10.0,
//                    "criteria": {
//                        "criteriaId": "CRIT013",
//                        "maxScore": "5.00",
//                        "weight": 0.5,
//                    }
//                },
//                {
//                    "EvaId": "3ee7dec2-19ce-4564-a51b-1547a7d4aa87_CRIT014",
//                    "score": 10.0,
//                    "criteria": {
//                        "criteriaId": "CRIT014",
//                        "maxScore": "10.00",
//                        "weight": 0.5,
//                    }
//                },
//                ...
//            ]
//        }
//        ให้คำนวณคะแนนรวมว่าได้ทั้งหมดเท่าไหร่ โดยคิดคะแนนรวมเป็น 10%


//        ใน evaluation จะแสดง
//        {
//            "posterId": "3ee7dec2-19ce-4564-a51b-1547a7d4aa87",
//            "comment": null,
//            "projectIdPoster": {
//                "projectId": "DST SP2024-04",
//            },
//            "instructorIdPoster": {
//                "instructorId": "INST002",
//                "projectIdRole":{
//                    "projectId":"DST SP2024-04",
//                }
//            },
//            "instructor": {
//                "professorId": "PROF001",
//                "professorName": "Aj.Akara",
//            },
//            "posterEvaluationScores": [
//                {
//                    "posterEvaId": "3ee7dec2-19ce-4564-a51b-1547a7d4aa87_CRIT013",
//                    "score": 10.0,
//                    "criteriaPoster": {
//                        "criteriaId": "CRIT013",
//                        "maxScore": "5.00",
//                        "weight": 0.0,
//                    }
//                },
//                {
//                    "posterEvaId": "3ee7dec2-19ce-4564-a51b-1547a7d4aa87_CRIT014",
//                    "score": 10.0,
//                    "criteriaPoster": {
//                        "criteriaId": "CRIT014",
//                        "maxScore": "5.00",
//                        "weight": 0.0,
//                    }
//                },
//                ...
//            ]
//        }
//        ให้คำนวณคะแนนรวมว่าได้ทั้งหมดเท่าไหร่ โดยคิดคะแนนรวมเป็น 10%


//    public StudentScoreDTO calculateTotalScore(ProjectInstructorRole instructor, Project project, Student student) {
//        List<ScoreDetail> allScores = new ArrayList<>();
//        double totalScore = 0.0;
//
//        // Get proposal evaluation scores
//        ProposalEvaluation proposalScores = proposalEvaluationRepository
//                .findByProjectInstructorRoleAndProjectAndStudent(instructor, project, student);
//        if (proposalScores != null) {
//            allScores.addAll(convertToScoreDetails(proposalScores.get));
//            totalScore += calculateSubTotal(proposalScores.getScores());
//        }
//
//        // Get defense evaluation scores
//        DefenseEvaluation defenseScores = defenseEvaRepository
//                .findByDefenseInstructorIdAndProjectIdAndStudentDefense(instructor, project, student);
//        if (defenseScores != null) {
//            allScores.addAll(convertToScoreDetails(defenseScores.getScores()));
//            totalScore += calculateSubTotal(defenseScores.getScores());
//        }
//
//        // Get poster evaluation scores
//        PosterEvaluation posterScores = posterEvaRepository
//                .findByInstructorIdPosterAndProjectIdPoster(instructor, project);
//        if (posterScores != null) {
//            allScores.addAll(convertToScoreDetails(posterScores.getScores()));
//            totalScore += calculateSubTotal(posterScores.getScores());
//        }
//
//        return new StudentScoreDTO(allScores, totalScore);
//    }

//    private List<ScoreDetail> convertToScoreDetails(List<EvaluationScore> scores) {
//        return scores.stream()
//                .map(score -> new ScoreDetail(
//                        score.getCriteria().getCriteriaId(),
//                        score.getCriteria().getCriteriaName(),
//                        score.getScore(),
//                        score.getCriteria().getMaxScore()
//                ))
//                .collect(Collectors.toList());
//    }
//
//    private double calculateSubTotal(List<EvaluationScore> scores) {
//        return scores.stream()
//                .mapToDouble(EvaluationScore::getScore)
//                .sum();
//    }

}