package com.example.project.service;

import com.example.project.DTO.Score.*;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    // ---------------------- Proposal Eva -------------------------//
//    @Transactional
//    public void saveEvaluation(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores) {
//        System.out.println("[Service] Inside saveEvaluation");
//
//        // ค้นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูลตาม instructor, project และ student
//        ProposalEvaluation evaluation = evaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(instructor, project, student);
//
//        // ถ้าไม่มี ProposalEvaluation อยู่แล้ว ให้สร้างใหม่
//        if (evaluation == null) {
//            evaluation = new ProposalEvaluation()
//                    .setProjectInstructorRole(instructor)
//                    .setProject(project)
//                    .setStudent(student);
//
//            // บันทึก ProposalEvaluation ใหม่
//            evaluation = evaluationRepository.save(evaluation);
//            if (evaluation.getProposalId() == null) {
//                throw new IllegalArgumentException("Proposal ID cannot be null");
//            }
//            System.out.println("[Service] Saved ProposalEvaluation with ID: " + evaluation.getProposalId());
//        } else {
//            // ถ้ามีอยู่แล้ว ให้ทำการ update หรือเพิ่มการอัปเดตที่ต้องการ
//            System.out.println("[Service] Found existing ProposalEvaluation with ID: " + evaluation.getProposalId());
//        }
//
//        String proposalId = evaluation.getProposalId();
//
//        // บันทึกหรืออัปเดตคะแนนลง ProposalEvalScore
//        for (ScoreDTO score : scores) {
//            // ดึง Criteria จากฐานข้อมูลตาม scoreCriteriaId
//            System.out.println("[Service] Score: " + score.getScore());
//            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());
//
//            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
//                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));
//
//            // ค้นหาคะแนนที่มีอยู่แล้วในฐานข้อมูล (ใช้ evaId เป็น key)
//            ProposalEvalScore evalScore = scoreRepository.findByEvaId(proposalId + "_" + score.getScoreCriteriaId());
//
//            if (evalScore == null) {
//                // ถ้ายังไม่มีคะแนนในฐานข้อมูล ให้ทำการสร้างใหม่
//                evalScore = new ProposalEvalScore()
//                        .setEvaId(proposalId + "_" + score.getScoreCriteriaId())
//                        .setScore(score.getScore())
//                        .setCriteria(criteria)
//                        .setProposalEvaluation(evaluation);
//
//                // บันทึกคะแนนใหม่
//                scoreRepository.save(evalScore);
//                System.out.println("[Service] Saved ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
//            } else {
//                // ถ้ามีคะแนนอยู่แล้วในฐานข้อมูล ให้ทำการอัปเดต
//                evalScore.setScore(score.getScore());
//                scoreRepository.save(evalScore);
//                System.out.println("[Service] Updated ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
//            }
//        }
//    }
    @Transactional
    public void saveEvaluation(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores, String comment) {
        System.out.println("[Service] Inside saveEvaluation");

        System.out.println("💬 Comment: " + comment);
        System.out.println("💬 Score: " + scores);

        // ค้ นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูล
        ProposalEvaluation evaluation = evaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(instructor, project, student);

        // ถ้าไม่มีให้สร้างใหม่
        if (evaluation == null) {
            evaluation = new ProposalEvaluation()
                    .setProjectInstructorRole(instructor)
                    .setProject(project)
                    .setComment(comment)
                    .setStudent(student);
        } else {
            // อัปเดตข้อมูลเดิม
            System.out.println("[Service] Found existing ProposalEvaluation with ID: " + evaluation.getProposalId());
            evaluation.setComment(comment);
        }

        // ✅ คำนวณคะแนนรวม
        // ✅ ใช้ .intValue() เพื่อแปลง BigDecimal เป็น int
        int totalScore = scores.stream()
                .map(ScoreDTO::getScore)  // ดึงค่า BigDecimal
                .mapToInt(BigDecimal::intValue)  // แปลงเป็น int
                .sum();

        evaluation.setTotalScore(BigDecimal.valueOf(totalScore));

        // ✅ บันทึก ProposalEvaluation
        evaluation = evaluationRepository.save(evaluation);
        String proposalId = evaluation.getProposalId();
        System.out.println("[Service] Saved/Updated ProposalEvaluation with ID: " + proposalId);

        // ✅ บันทึกหรืออัปเดตคะแนนแต่ละ criteria
        for (ScoreDTO score : scores) {
            System.out.println("[Service] Score: " + score.getScore());
            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());

            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));

            ProposalEvalScore evalScore = scoreRepository.findByEvaId(proposalId + "_" + score.getScoreCriteriaId());

            if (evalScore == null) {
                evalScore = new ProposalEvalScore()
                        .setEvaId(proposalId + "_" + score.getScoreCriteriaId())
                        .setScore(score.getScore())
                        .setCriteria(criteria)
                        .setProposalEvaluation(evaluation);

                scoreRepository.save(evalScore);
                System.out.println("[Service] Saved ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
            } else {
                evalScore.setScore(score.getScore());
                scoreRepository.save(evalScore);
                System.out.println("[Service] Updated ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
            }
        }
    }

    public StudentScoreDTO calculateTotalScoreProposal(ProjectInstructorRole instructor, Project project, Student student) {
        // 🔍 ค้นหา Evaluation ของนักศึกษา
        ProposalEvaluation evaluation = evaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(instructor, project, student);

        if (evaluation == null) {
            throw new EntityNotFoundException("Proposal evaluation not found");
        }

        double rawTotalScore = 0.0;
        List<ScoreDetail> scoreDetails = new ArrayList<>();

        for (ProposalEvalScore score : evaluation.getProposalEvalScores()) {
            // ✅ ดึงค่า Max Score และแปลงเป็น Double
            String maxScoreStr = score.getCriteria().getMaxScore();
            double maxScore = (maxScoreStr != null && !maxScoreStr.isEmpty()) ?
                    Double.parseDouble(maxScoreStr) : 10.0; // Default เป็น 10

            // ✅ ดึงค่า Weight และแปลงเป็น Double
            Float weightObj = score.getCriteria().getWeight();
            double finalWeight = (weightObj != null) ? weightObj.doubleValue() : 1.0;

            // ✅ ดึงค่า Score และตรวจสอบ null
            Float scoreObj = score.getScore().floatValue();
            double scoreValue = (scoreObj != null) ? scoreObj.doubleValue() : 0.0;

            // ✅ คำนวณ Weighted Score โดยให้ Weight เป็นคะแนนจริง
            double weightedScore = (scoreValue / maxScore) * (finalWeight * 100.0);
            rawTotalScore += weightedScore;

            // ✅ Debug log เพื่อตรวจสอบค่าที่ใช้คำนวณ
            System.out.println("📌 Criteria: " + score.getCriteria().getCriteriaId());
            System.out.println("📌 Score: " + scoreValue + ", Max Score: " + maxScore + ", Weight: " + finalWeight + ", Weighted Score: " + weightedScore);

            scoreDetails.add(new ScoreDetail(score.getCriteria().getCriteriaId(), weightedScore));
        }

        // ✅ คำนวณคะแนนรวมให้เป็น 40%
        double totalScore = Math.min(rawTotalScore, 40.0);

        // ✅ บันทึกค่า rawTotalScore ลงใน Column `totalScore` ของ Evaluation
        evaluation.setTotalScore(BigDecimal.valueOf(rawTotalScore));
        evaluationRepository.save(evaluation);

        // ✅ Debug ค่า rawTotalScore และ totalScore
        System.out.println("📌 Raw Total Score: " + rawTotalScore);
        System.out.println("📌 Final Total Score (40%): " + totalScore);

        return new StudentScoreDTO(scoreDetails, rawTotalScore, totalScore);
    }


    // ---------------- Defense Eva ------------------------ //
//    @Transactional
//    public void saveDefenseEvaluation(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores) {
//        System.out.println("[Service] Inside saveDefenseEvaluation");
//
//        // ค้นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูลตาม instructor, project และ student
//        DefenseEvaluation evaluation = defenseEvaluationRepository.findByDefenseInstructorIdAndProjectIdAndStudent(instructor, project, student);
//
//        // ถ้าไม่มี ProposalEvaluation อยู่แล้ว ให้สร้างใหม่
//        if (evaluation == null) {
//            evaluation = new DefenseEvaluation()
//                    .setDefenseInstructorId(instructor)
//                    .setProjectId (project)
//                    .setStudent(student);
//
//            // บันทึก ProposalEvaluation ใหม่
//            evaluation = defenseEvaluationRepository.save(evaluation);
//            if (evaluation.getDefenseEvaId() == null) {
//                throw new IllegalArgumentException("Defense ID cannot be null");
//            }
//            System.out.println("[Service] Saved DefenseEvaluation with ID: " + evaluation.getDefenseEvaId());
//        } else {
//            // ถ้ามีอยู่แล้ว ให้ทำการ update หรือเพิ่มการอัปเดตที่ต้องการ
//            System.out.println("[Service] Found existing DefenseEvaluation with ID: " + evaluation.getDefenseEvaId());
//        }
//
//        String defenseId = evaluation.getDefenseEvaId();
//
//        // บันทึกหรืออัปเดตคะแนนลง ProposalEvalScore
//        for (ScoreDTO score : scores) {
//            // ดึง Criteria จากฐานข้อมูลตาม scoreCriteriaId
//            System.out.println("[Service] Score: " + score.getScore());
//            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());
//
//            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
//                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));
//
//            // ค้นหาคะแนนที่มีอยู่แล้วในฐานข้อมูล (ใช้ evaId เป็น key)
//            DefenseEvalScore evalScore = defenseEvalScoreRepository.findByEvalId(defenseId + "_" + score.getScoreCriteriaId());
//
//            if (evalScore == null) {
//                // ถ้ายังไม่มีคะแนนในฐานข้อมูล ให้ทำการสร้างใหม่
//                evalScore = new DefenseEvalScore()
//                        .setEvalId(defenseId + "_" + score.getScoreCriteriaId())
//                        .setScore(score.getScore().intValue())
//                        .setCriteria(criteria)
//                        .setDefenseEvaluation(evaluation);
//
//                // บันทึกคะแนนใหม่
//                defenseEvalScoreRepository.save(evalScore);
//                System.out.println("[Service] Saved ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
//            } else {
//                // ถ้ามีคะแนนอยู่แล้วในฐานข้อมูล ให้ทำการอัปเดต
//                evalScore.setScore(score.getScore().intValue());
//                defenseEvalScoreRepository.save(evalScore);
//                System.out.println("[Service] Updated ProposalEvalScore for criteria: " + score.getScoreCriteriaId());
//            }
//        }
//    }

    @Transactional
    public void saveDefenseEvaluation(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores, String comment) {
        System.out.println("[Service] Inside saveDefenseEvaluation");

        System.out.println("💬 Comment: " + comment);
        System.out.println("💬 Score: " + scores);

        DefenseEvaluation evaluation = defenseEvaluationRepository.findByDefenseInstructorIdAndProjectIdAndStudent(instructor, project, student);

        if (evaluation == null) {
            evaluation = new DefenseEvaluation()
                    .setDefenseInstructorId(instructor)
                    .setProjectId(project)
                    .setComment(comment)
                    .setStudent(student);

            evaluation = defenseEvaluationRepository.save(evaluation);
            if (evaluation.getDefenseEvaId() == null) {
                throw new IllegalArgumentException("Defense ID cannot be null");
            }
            System.out.println("[Service] Saved DefenseEvaluation with ID: " + evaluation.getDefenseEvaId());
        } else {
            System.out.println("[Service] Found existing DefenseEvaluation with ID: " + evaluation.getDefenseEvaId());
            evaluation.setComment(comment);
        }

        // ✅ คำนวณคะแนนรวม (เก็บทศนิยมไว้)
        BigDecimal totalScore = scores.stream()
                .map(ScoreDTO::getScore) // ดึงค่า BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add); // ใช้ BigDecimal.sum เพื่อรักษาค่าทศนิยม

        evaluation.setTotalScore(totalScore);

        // ✅ บันทึก DefenseEvaluation
        evaluation = defenseEvaluationRepository.save(evaluation);
        String defenseId = evaluation.getDefenseEvaId();

        // ✅ บันทึกหรืออัปเดตคะแนนลง DefenseEvalScore
        for (ScoreDTO score : scores) {
            System.out.println("[Service] Score: " + score.getScore());
            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());

            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));

            DefenseEvalScore evalScore = defenseEvalScoreRepository.findByEvalId(defenseId + "_" + score.getScoreCriteriaId());

            if (evalScore == null) {
                evalScore = new DefenseEvalScore()
                        .setEvalId(defenseId + "_" + score.getScoreCriteriaId())
                        .setScore(score.getScore().floatValue()) // ✅ ใช้ floatValue() เพื่อรักษาค่าทศนิยม
                        .setCriteria(criteria)
                        .setDefenseEvaluation(evaluation);


                defenseEvalScoreRepository.save(evalScore);
                System.out.println("[Service] Saved DefenseEvalScore for criteria: " + score.getScoreCriteriaId());
            } else {
                evalScore.setScore(score.getScore().floatValue()); // ✅ ใช้ floatValue() แทน intValue()
                defenseEvalScoreRepository.save(evalScore);
                System.out.println("[Service] Updated DefenseEvalScore for criteria: " + score.getScoreCriteriaId());
            }
        }
    }


    public StudentScoreDTO calculateTotalScoreDefense(ProjectInstructorRole instructor, Project project, Student student) {
        // 🔍 ค้นหา Evaluation ของนักศึกษา
        DefenseEvaluation evaluation = defenseEvaluationRepository.findByDefenseInstructorIdAndProjectIdAndStudent(instructor, project, student);

        if (evaluation == null) {
            throw new EntityNotFoundException("Defense evaluation not found");
        }

        double rawTotalScore = 0.0;
        List<ScoreDetail> scoreDetails = new ArrayList<>();

        for (DefenseEvalScore score : evaluation.getDefenseEvalScore()) {
            // ✅ ดึงค่า Max Score และแปลงเป็น Double
            String maxScoreStr = score.getCriteria().getMaxScore();
            double maxScore = (maxScoreStr != null && !maxScoreStr.isEmpty()) ?
                    Double.parseDouble(maxScoreStr) : 10.0; // Default เป็น 10

            // ✅ ดึงค่า Weight และแปลงเป็น Double
            Float weightObj = score.getCriteria().getWeight();
            double finalWeight = (weightObj != null) ? weightObj.doubleValue() : 1.0;

            // ✅ ดึงค่า Score และตรวจสอบ null
            BigDecimal scoreObj = score.getScore();
            double scoreValue = (scoreObj != null) ? scoreObj.doubleValue() : 0.0;

            // ✅ คำนวณ Weighted Score โดยให้ Weight เป็นคะแนนจริง
            double weightedScore = (scoreValue / maxScore) * (finalWeight * 100.0);
            rawTotalScore += weightedScore;

            // ✅ Debug log เพื่อตรวจสอบค่าที่ใช้คำนวณ
            System.out.println("📌 Criteria: " + score.getCriteria().getCriteriaId());
            System.out.println("📌 Score: " + scoreValue + ", Max Score: " + maxScore + ", Weight: " + finalWeight + ", Weighted Score: " + weightedScore);

            scoreDetails.add(new ScoreDetail(score.getCriteria().getCriteriaId(), weightedScore));
        }

        // ✅ คำนวณคะแนนรวมให้เป็น 40%
        double totalScore = Math.min(rawTotalScore, 40.0);

        evaluation.setTotalScore(BigDecimal.valueOf(rawTotalScore));
        defenseEvaluationRepository.save(evaluation);

        // ✅ Debug ค่า rawTotalScore และ totalScore
        System.out.println("📌 Raw Total Score: " + rawTotalScore);
        System.out.println("📌 Final Total Score (40%): " + totalScore);


        return new StudentScoreDTO(scoreDetails, rawTotalScore, totalScore);
    }


    // ---------------- Poster Eva ------------------------ //
//    @Transactional
//    public void savePosterEvaluation(ProjectInstructorRole instructor, Project project, List<ScoreDTO> scores) {
//        System.out.println("[Service] Inside savePosterEvaluation");
//
//        // ค้นหา ProposalEvaluation ที่มีอยู่แล้วในฐานข้อมูลตาม instructor, project และ student
//        PosterEvaluation evaluation = posterEvaRepository.findByInstructorIdPosterAndProjectIdPoster(instructor, project);
//
//        // ถ้าไม่มี ProposalEvaluation อยู่แล้ว ให้สร้างใหม่
//        if (evaluation == null) {
//            evaluation = new PosterEvaluation()
//                    .setInstructorIdPoster(instructor)
//                    .setProjectIdPoster(project);
//
//            // บันทึก PosterEvaluation ใหม่
//            evaluation = posterEvaRepository.save(evaluation);
//            if (evaluation.getPosterId() == null) {
//                throw new IllegalArgumentException("PosterEvaluation ID cannot be null");
//            }
//            System.out.println("[Service] Saved PosterEvaluation with ID: " + evaluation.getPosterId());
//        } else {
//            // ถ้ามีอยู่แล้ว ให้ทำการ update หรือเพิ่มการอัปเดตที่ต้องการ
//            System.out.println("[Service] Found existing PosterEvaluation with ID: " + evaluation.getPosterId());
//        }
//
//        String posterId = evaluation.getPosterId();
//
//        // บันทึกหรืออัปเดตคะแนนลง ProposalEvalScore
//        for (ScoreDTO score : scores) {
//            // ดึง Criteria จากฐานข้อมูลตาม scoreCriteriaId
//            System.out.println("[Service] Score: " + score.getScore());
//            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());
//
//            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
//                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));
//
//            // ค้นหาคะแนนที่มีอยู่แล้วในฐานข้อมูล (ใช้ evaId เป็น key)
//            PosterEvaluationScore evalScore = posterEvaScoreRepository.findByPosterEvaId(posterId + "_" + score.getScoreCriteriaId());
//
//            if (evalScore == null) {
//                // ถ้ายังไม่มีคะแนนในฐานข้อมูล ให้ทำการสร้างใหม่
//                evalScore = new PosterEvaluationScore()
//                        .setPosterEvaId(posterId + "_" + score.getScoreCriteriaId())
//                        .setScore(score.getScore().intValue())
//                        .setCriteriaPoster(criteria)
//                        .setPosterEvaluation(evaluation);
//
//                // บันทึกคะแนนใหม่
//                posterEvaScoreRepository.save(evalScore);
//                System.out.println("[Service] Saved PosterEvalScore for criteria: " + score.getScoreCriteriaId());
//            } else {
//                // ถ้ามีคะแนนอยู่แล้วในฐานข้อมูล ให้ทำการอัปเดต
//                evalScore.setScore(score.getScore().intValue());
//                posterEvaScoreRepository.save(evalScore);
//                System.out.println("[Service] Updated PosterEvalScore for criteria: " + score.getScoreCriteriaId());
//            }
//        }
//    }
    @Transactional
    public void savePosterEvaluation(ProjectInstructorRole instructor, Project project, List<ScoreDTO> scores, String comment) {
        System.out.println("[Service] Inside savePosterEvaluation");

        PosterEvaluation evaluation = posterEvaRepository.findByInstructorIdPosterAndProjectIdPoster(instructor, project);

        if (evaluation == null) {
            evaluation = new PosterEvaluation()
                    .setInstructorIdPoster(instructor)
                    .setProjectIdPoster(project);
        }

        // บันทึกคอมเมนต์ลงฐานข้อมูล
        evaluation.setComment(comment);

        evaluation = posterEvaRepository.save(evaluation);
        System.out.println("[Service] Saved PosterEvaluation with ID: " + evaluation.getPosterId());

        String posterId = evaluation.getPosterId();

        for (ScoreDTO score : scores) {
            System.out.println("[Service] Score: " + score.getScore());
            System.out.println("[Service] Criteria: " + score.getScoreCriteriaId());

            Criteria criteria = criteriaRepository.findById(score.getScoreCriteriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Criteria not found for id: " + score.getScoreCriteriaId()));

            PosterEvaluationScore evalScore = posterEvaScoreRepository.findByPosterEvaId(posterId + "_" + score.getScoreCriteriaId());

            if (evalScore == null) {
                evalScore = new PosterEvaluationScore()
                        .setPosterEvaId(posterId + "_" + score.getScoreCriteriaId())
                        .setScore(score.getScore().floatValue())
                        .setCriteriaPoster(criteria)
                        .setPosterEvaluation(evaluation);

                posterEvaScoreRepository.save(evalScore);
                System.out.println("[Service] Saved PosterEvalScore for criteria: " + score.getScoreCriteriaId());
            } else {
                evalScore.setScore(score.getScore().floatValue());
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

        evaluation.setTotalScore(BigDecimal.valueOf(rawTotalScore));
        posterEvaRepository.save(evaluation);

        return new StudentScoreDTO(scoreDetails, rawTotalScore, totalScore);
    }



    // ---------------------- GRADE ---------------------- //
    @Transactional
    public String saveProposalGrade(Project project, Student student, ScoreRequestDTO scoreRequest) {
        System.out.println("🪄 [Service] Inside saveProposalGrade");

        System.out.println("[Service] scoreRequest: " + scoreRequest.getScores().toString());

        // Extracting values from DTO
        BigDecimal evaluateScore = scoreRequest.getScores().stream()
                .map(ScoreDTO::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgScoreProposal = BigDecimal.valueOf(scoreRequest.getScoreProposal());
        BigDecimal totalScore = BigDecimal.valueOf(scoreRequest.getTotalScore());
        String gradeResult = scoreRequest.getGrade();

        System.out.println("[Service] Saving proposal avgScoreProposal: " + avgScoreProposal);
        System.out.println("[Service] Saving proposal evaluateScore: " + evaluateScore);
        System.out.println("[Service] Saving proposal totalScore: " + totalScore);
        System.out.println("[Service] Saving proposal grade: " + gradeResult);

        // 🔍 ค้นหา GradingProposalEvaluation ที่มีอยู่แล้ว
        GradingProposalEvaluation existingGrading = gradingProposalEvaluationRepository.findByProjectAndStudent(project, student);

        if (existingGrading != null) {
            // ✅ อัปเดตข้อมูลเดิม
            existingGrading.setDateTime(LocalDateTime.now());
            existingGrading.setAvgScoreProposal(avgScoreProposal);
            existingGrading.setEvaluateScore(evaluateScore);
            existingGrading.setTotalScore(totalScore);
            existingGrading.setGradeResult(gradeResult);

            gradingProposalEvaluationRepository.save(existingGrading);
            System.out.println("✅ [Service] Proposal Grade: "+ existingGrading.toString());
            System.out.println("✅ [Service] Proposal Grade Updated Successfully!");

            return existingGrading.getGradeResult();
        } else {
            // ✅ สร้างใหม่ถ้ายังไม่มี
            GradingProposalEvaluation grading = new GradingProposalEvaluation();
            grading.setProposalGradeId(UUID.randomUUID().toString());
            grading.setDateTime(LocalDateTime.now());
            grading.setAvgScoreProposal(avgScoreProposal);
            grading.setEvaluateScore(evaluateScore);
            grading.setTotalScore(totalScore);
            grading.setGradeResult(gradeResult);
            grading.setProject(project);
            grading.setStudent(student);

            gradingProposalEvaluationRepository.save(grading);
            System.out.println("✅ [Service] Proposal Grade Created Successfully!");

            return grading.getGradeResult();
        }
    }

    @Transactional
    public String saveDefenseGrade(Project project, Student student, DefenseScoreRequestDTO scoreRequest) {

        System.out.println("🪄 [Service] Inside saveProposalGrade");

        // ตรวจสอบข้อมูลที่ได้รับ
        System.out.println("[Service] scoreRequest: " + scoreRequest.getScores().toString());

        // ✅ ดึงคะแนน Evaluate และ Extra จาก scoreRequest
        BigDecimal advisorScore = BigDecimal.ZERO;
        BigDecimal extraScore = BigDecimal.ZERO;

        // ดึงข้อมูลจาก scoreRequest เพื่อเก็บคะแนนที่ตรงกับ CRIT022 และ CRIT023
        for (ScoreDTO score : scoreRequest.getScores()) {
            if ("CRIT022".equals(score.getScoreCriteriaId())) {
                advisorScore = score.getScore(); // คะแนนจากการประเมิน
            } else if ("CRIT023".equals(score.getScoreCriteriaId())) {
                extraScore = score.getScore(); // คะแนนเพิ่มเติม
            }
        }

        BigDecimal posterScore = BigDecimal.valueOf(scoreRequest.getPosterScore());
        BigDecimal defenseScore = BigDecimal.valueOf(scoreRequest.getDefenseScore());
        BigDecimal totalScore = BigDecimal.valueOf(scoreRequest.getTotalScore());
        String gradeResult = scoreRequest.getGrade();

        System.out.println("[Service] Saving proposal posterScore: " + posterScore);
        System.out.println("[Service] Saving proposal defenseScore: " + defenseScore);
        System.out.println("[Service] Saving proposal advisorScore: " + advisorScore);
        System.out.println("[Service] Saving proposal extraScore: " + extraScore);
        System.out.println("[Service] Saving proposal totalScore: " + totalScore);
        System.out.println("[Service] Saving proposal grade: " + gradeResult);

        // 🔍 ค้นหา GradingDefenseEvaluation
        GradingDefenseEvaluation gradingDefense = gradingDefenseEvaluationRepository.findByProjectIdAndStudentId(project, student);
        if (gradingDefense == null) {
            gradingDefense = new GradingDefenseEvaluation();
            gradingDefense.setDefenseGradeEvalId(UUID.randomUUID().toString());
            gradingDefense.setDatetime(LocalDateTime.now());
            gradingDefense.setProjectId(project);
            gradingDefense.setStudentId(student);
            gradingDefense.setAvgScoreDefense(defenseScore.doubleValue());
            gradingDefense.setAvgPosterScore(posterScore.doubleValue());
            gradingDefense.setEvaluateScore(advisorScore.doubleValue());
            gradingDefense.setExtraScore(extraScore.doubleValue());
            gradingDefense.setTotalScore(totalScore.doubleValue());
            gradingDefense.setGradeResult(gradeResult);
        }

        // ✅ อัปเดตค่าต่างๆ และบันทึกลง Database
        gradingDefense.setAvgScoreDefense(defenseScore.doubleValue());
        gradingDefense.setAvgPosterScore(posterScore.doubleValue());
        gradingDefense.setEvaluateScore(advisorScore.doubleValue());
        gradingDefense.setExtraScore(extraScore.doubleValue());
        gradingDefense.setTotalScore(totalScore.doubleValue());
        gradingDefense.setGradeResult(gradeResult);

        gradingDefenseEvaluationRepository.save(gradingDefense);

        System.out.println("✅ Assigned Grade: " + gradeResult);
        return gradeResult;
    }


//    @Transactional
//    public String saveProposalGrade(Project project, Student student, List<ScoreDTO> scores) {
//        System.out.println("🪄 [Service] Inside saveProposalGrade");
//
//        // 🔍 ดึงรายการ ProjectInstructorRole ทั้งหมดที่เกี่ยวข้องกับ Project นี้
//        List<ProjectInstructorRole> projectList = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(project.getProjectId());
//        System.out.println("📌 Total ProjectInstructorRole: " + projectList.size());
//
//        // 🔍 ค้นหา ProposalEvaluation ทั้งหมดที่เกี่ยวข้องกับ Project และ Student
//        List<ProposalEvaluation> evaluations = evaluationRepository.findByProjectAndStudent(project, student);
//        System.out.println("📑 Found evaluations: " + evaluations.size());
//
//
//        // ✅ คำนวณค่าเฉลี่ย Proposal Score (ถ้ามีข้อมูล)
//        BigDecimal avgScoreProposal = BigDecimal.ZERO;
//        int totalEvaluators = projectList.size(); // ใช้จำนวนคนที่ต้องให้คะแนนแทน
//
//        if (!evaluations.isEmpty()) {
//            avgScoreProposal = evaluations.stream()
//                    .map(evaluation -> evaluation.getTotalScore() != null ? evaluation.getTotalScore() : BigDecimal.ZERO)
//                    .peek(score -> System.out.println("📌 Processed Score: " + score))
//                    .reduce(BigDecimal.ZERO, BigDecimal::add)
//                    .divide(BigDecimal.valueOf(totalEvaluators), 2, RoundingMode.HALF_UP); // ใช้จำนวนกรรมการทั้งหมดเป็นตัวหาร
//        }
//        System.out.println("💯 avgScoreProposal: " + avgScoreProposal);
//
//        // ✅ คำนวณ evaluateScore จาก `scores`
//        BigDecimal evaluateScore = scores.stream()
//                .map(ScoreDTO::getScore)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        System.out.println("💯 evaluateScore (เต็ม 10): " + evaluateScore);
//
//        // ✅ คำนวณ Total Score (รวม Proposal + Evaluate) **แต่ไม่ให้เกิน 100**
//        BigDecimal totalScore = avgScoreProposal.add(evaluateScore);
//        totalScore = totalScore.min(new BigDecimal("100")); // จำกัดคะแนนที่ 100
//        System.out.println("💯 totalScore (capped at 100): " + totalScore);
//
//        // ✅ ถ้าจำนวน projectList และ evaluations ไม่ตรงกัน → บันทึก "I" ในฐานข้อมูลก่อน return
//        if (projectList.size() != evaluations.size()) {
//            System.out.println("⚠️ Project list size does not match evaluations! Saving grade 'I'...");
//
//            // 🔍 ค้นหา GradingProposalEvaluation ที่มีอยู่แล้ว
//            GradingProposalEvaluation existingGrading = gradingProposalEvaluationRepository.findByProjectAndStudent(project, student);
//
//            if (existingGrading != null) {
//                // ✅ อัปเดตข้อมูลเดิมเป็น "I"
//                existingGrading.setDateTime(LocalDateTime.now());
//                existingGrading.setAvgScoreProposal(avgScoreProposal);
//                existingGrading.setEvaluateScore(evaluateScore);
//                existingGrading.setTotalScore(totalScore);
//                existingGrading.setGradeResult("I");
//
//                gradingProposalEvaluationRepository.save(existingGrading);
//                System.out.println("✅ [Service] Updated existing grade to 'I'");
//            } else {
//                // ✅ สร้างใหม่ถ้ายังไม่มี
//                GradingProposalEvaluation grading = new GradingProposalEvaluation();
//                grading.setProposalGradeId(UUID.randomUUID().toString());
//                grading.setDateTime(LocalDateTime.now());
//                existingGrading.setDateTime(LocalDateTime.now());
//                existingGrading.setAvgScoreProposal(avgScoreProposal);
//                existingGrading.setEvaluateScore(evaluateScore);
//                existingGrading.setTotalScore(totalScore);
//                grading.setGradeResult("I");
//                grading.setProject(project);
//                grading.setStudent(student);
//
//                gradingProposalEvaluationRepository.save(grading);
//                System.out.println("✅ [Service] Created new grade entry with 'I'");
//            }
//
//            return "I";
//        }
//
//        // ✅ คำนวณเกรดตามเงื่อนไข
//        String gradeResult = calculateGrade(totalScore);
//        System.out.println("🅰️ gradeResult: " + gradeResult);
//
//        // 🔍 ค้นหา GradingProposalEvaluation ที่มีอยู่แล้ว
//        GradingProposalEvaluation existingGrading = gradingProposalEvaluationRepository.findByProjectAndStudent(project, student);
//
//        if (existingGrading != null) {
//            // ✅ อัปเดตข้อมูลเดิม
//            existingGrading.setDateTime(LocalDateTime.now());
//            existingGrading.setAvgScoreProposal(avgScoreProposal);
//            existingGrading.setEvaluateScore(evaluateScore);
//            existingGrading.setTotalScore(totalScore);
//            existingGrading.setGradeResult(gradeResult);
//
//            gradingProposalEvaluationRepository.save(existingGrading);
//            System.out.println("✅ [Service] Proposal Grade Updated Successfully!");
//
//            return existingGrading.getGradeResult();
//        } else {
//            // ✅ สร้างใหม่ถ้ายังไม่มี
//            GradingProposalEvaluation grading = new GradingProposalEvaluation();
//            grading.setProposalGradeId(UUID.randomUUID().toString());
//            grading.setDateTime(LocalDateTime.now());
//            grading.setAvgScoreProposal(avgScoreProposal);
//            grading.setEvaluateScore(evaluateScore);
//            grading.setTotalScore(totalScore);
//            grading.setGradeResult(gradeResult);
//            grading.setProject(project);
//            grading.setStudent(student);
//
//            gradingProposalEvaluationRepository.save(grading);
//            System.out.println("✅ [Service] Proposal Grade Created Successfully!");
//
//            return grading.getGradeResult();
//        }
//    }

//    @Transactional
//    public String saveDefenseGrade(ProjectInstructorRole instructor, Project project, Student student, List<ScoreDTO> scores) {
//        System.out.println("🪄 [Service] Inside saveDefenseGrade");
//
//        // 🔍 ดึงรายการ ProjectInstructorRole
//        List<ProjectInstructorRole> allInstructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(project.getProjectId());
//        long committeeAdvisorCount = allInstructors.stream()
//                .filter(role -> "Committee".equals(role.getRole()) || "Advisor".equals(role.getRole()))
//                .count();
//        long posterCommitteeCount = allInstructors.stream()
//                .filter(role -> "Committee".equals(role.getRole()) || "Poster-Committee".equals(role.getRole()))
//                .count();
//
//        System.out.println("📌 Total Committee & Advisor: " + committeeAdvisorCount);
//        System.out.println("📌 Total Committee & Poster-Committee: " + posterCommitteeCount);
//
//        // 🔍 ค้นหา DefenseEvaluation
//        List<DefenseEvaluation> defenseEvaluations = defenseEvaluationRepository.findByProjectIdAndStudent(project, student);
//        if (defenseEvaluations.isEmpty()) {
//            throw new EntityNotFoundException("Defense evaluation not found");
//        }
//        System.out.println("💯 Total Defense Evaluation: " + defenseEvaluations.size());
//
//        BigDecimal avgScoreDefense = defenseEvaluations.stream()
//                .map(DefenseEvaluation::getTotalScore)
//                .filter(Objects::nonNull)
//                .peek(score -> System.out.println("📌 Processed Defense Score: " + score))
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .divide(BigDecimal.valueOf(defenseEvaluations.size()), RoundingMode.HALF_UP);
//        System.out.println("💯 avgScoreDefense: " + avgScoreDefense);
//
//        // 🔍 ค้นหา PosterEvaluation
//        List<PosterEvaluation> posterEvaluations = posterEvaRepository.findByProjectIdPoster(project);
//        if (posterEvaluations.isEmpty()) {
//            throw new EntityNotFoundException("Poster evaluation not found");
//        }
//        System.out.println("💯 Total Poster Evaluation: " + posterEvaluations.size());
//
//        BigDecimal avgPosterScore = posterEvaluations.stream()
//                .map(PosterEvaluation::getTotalScore)
//                .filter(Objects::nonNull)
//                .peek(score -> System.out.println("📌 Processed Poster Score: " + score))
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .divide(BigDecimal.valueOf(posterEvaluations.size()), RoundingMode.HALF_UP);
//        System.out.println("💯 avgPosterScore: " + avgPosterScore);
//
//        // ✅ ดึงคะแนน Evaluate และ Extra
//        BigDecimal advisorScore = BigDecimal.ZERO;
//        BigDecimal extraScore = BigDecimal.ZERO;
//
//        for (ScoreDTO score : scores) {
//            if ("CRIT022".equals(score.getScoreCriteriaId())) {
//                advisorScore = score.getScore();
//            } else if ("CRIT023".equals(score.getScoreCriteriaId())) {
//                extraScore = score.getScore();
//            }
//        }
//        System.out.println("💯 advisorScore: " + advisorScore);
//        System.out.println("💯 extraScore: " + extraScore);
//
//        // 🔹 Normalize คะแนน Defense และ Poster เป็นเปอร์เซ็นต์ของ 100
//        BigDecimal normalizedDefenseScore = avgScoreDefense.multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
//        BigDecimal normalizedPosterScore = avgPosterScore.multiply(BigDecimal.valueOf(4));
//
//        // ✅ คำนวณตามเปอร์เซ็นต์จริง
//        BigDecimal weightedDefenseScore = normalizedDefenseScore.multiply(BigDecimal.valueOf(0.30)); // 30%
//        BigDecimal weightedPosterScore = normalizedPosterScore.multiply(BigDecimal.valueOf(0.10));  // 10%
////        BigDecimal weightedAdvisorScore = advisorScore.multiply(BigDecimal.valueOf(1));  // 60%
////        BigDecimal weightedExtraScore = extraScore.multiply(BigDecimal.valueOf(1));      // 10%
//
//        BigDecimal totalScore = weightedDefenseScore
//                .add(weightedPosterScore)
//                .add(advisorScore)
//                .add(extraScore);
//
//
//        System.out.println("💯 weightedDefenseScore: " + weightedDefenseScore);
//        System.out.println("💯 weightedPosterScore: " + weightedPosterScore);
////        System.out.println("💯 weightedAdvisorScore: " + weightedAdvisorScore);
////        System.out.println("💯 weightedExtraScore: " + weightedExtraScore);
//        System.out.println("💯 totalScore: " + totalScore);
//
//        // 🔍 ค้นหา GradingDefenseEvaluation
//        GradingDefenseEvaluation gradingDefense = gradingDefenseEvaluationRepository.findByProjectIdAndStudentId(project, student);
//        if (gradingDefense == null) {
//            gradingDefense = new GradingDefenseEvaluation();
//            gradingDefense.setDefenseGradeEvalId(UUID.randomUUID().toString());
//            gradingDefense.setDatetime(LocalDateTime.now());
//            gradingDefense.setProjectId(project);
//            gradingDefense.setStudentId(student);
//        }
//
//        // ✅ อัปเดตค่าต่างๆ และบันทึกลง Database
//        gradingDefense.setAvgScoreDefense(avgScoreDefense.doubleValue());
//        gradingDefense.setAvgPosterScore(avgPosterScore.doubleValue());
//        gradingDefense.setEvaluateScore(advisorScore.doubleValue());
//        gradingDefense.setExtraScore(extraScore.doubleValue());
//        gradingDefense.setTotalScore(totalScore.doubleValue());
//
//        // ✅ ตรวจสอบว่าคะแนนครบทุกหมวดหมู่ก่อนให้เกรด "I"
//        if (defenseEvaluations.size() < committeeAdvisorCount ||
//                posterEvaluations.size() < posterCommitteeCount ||
//                advisorScore.compareTo(BigDecimal.ZERO) == 0) { // ถ้าไม่มีคะแนนจาก Advisor
//            gradingDefense.setGradeResult("I");
//            gradingDefenseEvaluationRepository.save(gradingDefense);
//            System.out.println("❌ Assigned Grade: I (Incomplete)");
//            return "I";
//        }
//
//        // ✅ คำนวณเกรดตามเกณฑ์
//        String grade = calculateGrade(totalScore);
//        gradingDefense.setGradeResult(grade);
//        gradingDefenseEvaluationRepository.save(gradingDefense);
//
//        System.out.println("✅ Assigned Grade: " + grade);
//        return grade;
//    }



    private String calculateGrade(BigDecimal totalScore) {
        if (totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "A";
        } else if (totalScore.compareTo(BigDecimal.valueOf(75)) >= 0) {
            return "B+";
        } else if (totalScore.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "B";
        } else if (totalScore.compareTo(BigDecimal.valueOf(65)) >= 0) {
            return "C+";
        } else if (totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "C";
        } else if (totalScore.compareTo(BigDecimal.valueOf(55)) >= 0) {
            return "D+";
        } else if (totalScore.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "D";
        } else {
            return "F";
        }
    }

}