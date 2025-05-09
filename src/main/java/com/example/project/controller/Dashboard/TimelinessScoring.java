package com.example.project.controller.Dashboard;

import com.example.project.DTO.ScoringPeriodsRequest;
import com.example.project.DTO.TimelinessRequest;
import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class TimelinessScoring {

    private final ProposalEvaluationRepository proposalEvaluationRepository;
    private final ProjectRepository projectRepository;
    private final DefenseEvaluationRepository defenseEvaluationRepository;
    private final ProposalEvalScoreRepository proposalEvalScoreRepository;
    private final DefenseEvalScoreRepository defenseEvalScoreRepository;
    private final CriteriaRepository criteriaRepository;

    public TimelinessScoring(ProposalEvaluationRepository proposalEvaluationRepository, ProjectRepository projectRepository, DefenseEvaluationRepository defenseEvaluationRepository, ProposalEvalScoreRepository proposalEvalScoreRepository, DefenseEvalScoreRepository defenseEvalScoreRepository, CriteriaRepository criteriaRepository) {
        this.proposalEvaluationRepository = proposalEvaluationRepository;
        this.projectRepository = projectRepository;
        this.defenseEvaluationRepository = defenseEvaluationRepository;
        this.proposalEvalScoreRepository = proposalEvalScoreRepository;
        this.defenseEvalScoreRepository = defenseEvalScoreRepository;
        this.criteriaRepository = criteriaRepository;
    }

//    @GetMapping("/api/timeline-score")
//    public ResponseEntity<List<Map<String, String>>> getTimelineScore(
//            @RequestParam String year,
//            @RequestParam String evaType,
//            @RequestParam String program
//    ) {
//        System.out.println("🐽"+year);
//        System.out.println("🐽"+evaType);
//        System.out.println("🐽"+program);
//
//        List<Project> allProjects = projectRepository.findBySemester(year);
//
//        // 🔍 Filter ตาม program ที่รับมา
//        List<Project> projectList;
//        if ("All".equalsIgnoreCase(program)) {
//            projectList = allProjects;
//        } else {
//            projectList = allProjects.stream()
//                    .filter(p -> program.equalsIgnoreCase(p.getProgram()))
//                    .collect(Collectors.toList());
//        }
//
//        System.out.println("🎀 Project list: "+projectList.size());
//
//        List<Map<String, String>> responseList = new ArrayList<>();
//
//        if (evaType.equals("Proposal")) {
//            for (Project project : projectList) {
//                Map<String, String> responseItem = new HashMap<>();
//                responseItem.put("projectId", project.getProjectId());
//
//                System.out.println("👍 project id: "+project.getProjectId());
//
//                List<ProposalEvaluation> evaList = proposalEvaluationRepository.findByProject_ProjectId(project.getProjectId());
//                String scoreStr = "0"; // default
//                System.out.println("🐖 eva list "+evaList.size());
//
//                for (ProposalEvaluation eva : evaList) {
//                    ProposalEvalScore score = proposalEvalScoreRepository.findByProposalEvaluation_ProposalIdAndCriteria_CriteriaId(eva.getProposalId(), "CRIT006");
//
//                    System.out.println("😈 score "+score.getScore());
//
//                    // ตรวจสอบว่า score ไม่เป็น null
//                    if (score != null && score.getScore() != null) {
//                        scoreStr = score.getScore().toString();
//                        break;
//                    } else {
//                        // ถ้า score หรือ score.getScore() เป็น null ให้ตั้งค่า scoreStr เป็นค่าเริ่มต้น
//                        scoreStr = "0";  // หรือค่าอื่นที่ต้องการ
//                    }
//                }
//
//
//                responseItem.put("timelineScore", scoreStr);
//                responseList.add(responseItem);
//            }
//        } else if (evaType.equals("Defense")) {
//            for (Project project : projectList) {
//                Map<String, String> responseItem = new HashMap<>();
//                responseItem.put("projectId", project.getProjectId());
//
//                List<DefenseEvaluation> evaList = defenseEvaluationRepository.findByProjectId_ProjectId(project.getProjectId());
//                String scoreStr = "0"; // default
//
//                for (DefenseEvaluation eva : evaList) {
//                    DefenseEvalScore score = defenseEvalScoreRepository.findByDefenseEvaluation_DefenseEvaIdAndCriteria_CriteriaId(eva.getDefenseEvaId(), "CRIT012");
//                    if (score != null && score.getScore() != null) {
//                        scoreStr = score.getScore().toString();
//                        break;
//                    } else {
//                        // ถ้า score หรือ score.getScore() เป็น null ให้ตั้งค่า scoreStr เป็นค่าเริ่มต้น
//                        scoreStr = "0";  // หรือค่าอื่นที่ต้องการ
//                    }
//                }
//
//                responseItem.put("timelineScore", scoreStr);
//                responseList.add(responseItem);
//            }
//        } else {
//            return ResponseEntity.badRequest().body(Collections.singletonList(
//                    Map.of("error", "Invalid evaluation type")
//            ));
//        }
//
//        return ResponseEntity.ok(responseList);
//    }
    @GetMapping("/api/timeline-score")
    public ResponseEntity<List<Map<String, String>>> getTimelineScore(
            @RequestParam String year,
            @RequestParam String evaType,
            @RequestParam String program
    ) {
        List<Project> allProjects = projectRepository.findBySemester(year);

        // 🔍 Filter program
        List<Project> projectList = "All".equalsIgnoreCase(program) ? allProjects :
                allProjects.stream().filter(p -> program.equalsIgnoreCase(p.getProgram())).collect(Collectors.toList());

        List<Map<String, String>> responseList = new ArrayList<>();

        // หาคะแนนเต็มจาก Criteria ล่วงหน้า
        Criteria criteria = criteriaRepository.findById(
                evaType.equals("Proposal") ? "CRIT006" : "CRIT012"
        ).orElse(null);
        String maxScore = criteria != null && criteria.getMaxScore() != null ? criteria.getMaxScore().toString() : "0";

        if (evaType.equals("Proposal")) {
            for (Project project : projectList) {
                Map<String, String> responseItem = new HashMap<>();
                responseItem.put("projectId", project.getProjectId());

                List<ProposalEvaluation> evaList = proposalEvaluationRepository.findByProject_ProjectId(project.getProjectId());
                String scoreStr = "0"; // default

                for (ProposalEvaluation eva : evaList) {
                    ProposalEvalScore score = proposalEvalScoreRepository.findByProposalEvaluation_ProposalIdAndCriteria_CriteriaId(
                            eva.getProposalId(), "CRIT006");

                    if (score != null && score.getScore() != null) {
                        scoreStr = score.getScore().toString();
                        break;
                    }
                }

                responseItem.put("timelineScore", scoreStr);
                responseItem.put("maxScore", maxScore);
                responseList.add(responseItem);
            }
        } else if (evaType.equals("Defense")) {
            for (Project project : projectList) {
                Map<String, String> responseItem = new HashMap<>();
                responseItem.put("projectId", project.getProjectId());

                List<DefenseEvaluation> evaList = defenseEvaluationRepository.findByProjectId_ProjectId(project.getProjectId());
                String scoreStr = "0";

                for (DefenseEvaluation eva : evaList) {
                    DefenseEvalScore score = defenseEvalScoreRepository.findByDefenseEvaluation_DefenseEvaIdAndCriteria_CriteriaId(
                            eva.getDefenseEvaId(), "CRIT012");

                    if (score != null && score.getScore() != null) {
                        scoreStr = score.getScore().toString();
                        break;
                    }
                }

                responseItem.put("timelineScore", scoreStr);
                responseItem.put("maxScore", maxScore);
                responseList.add(responseItem);
            }
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonList(
                    Map.of("error", "Invalid evaluation type")
            ));
        }

        return ResponseEntity.ok(responseList);
    }

//    @PostMapping("/api/save/timeliness")
//    public ResponseEntity<String> saveTimelinessScore(@RequestBody List<TimelinessRequest> requests) {
//        try {
//            for (TimelinessRequest request : requests) {
//                String projectId = request.getProjectId();
//                String scoreStr = request.getScore();
//                String evaType = request.getEvaType();
//
//                if (evaType.equals("Proposal")) {
//                    List<ProposalEvaluation> existingEvas = proposalEvaluationRepository.findByProject_ProjectId(projectId);
//                    System.out.println("💩💩 existingEvas");
//                    System.out.println(existingEvas);
//
//                    if (existingEvas == null || existingEvas.isEmpty()) {
//                        continue;
//                    }
//
//                    // Loop through all ProposalEvaluation objects
//                    for (ProposalEvaluation proposalEvaluation : existingEvas) {
//                        Optional<ProposalEvalScore> existingScoreOpt = proposalEvaluation.getProposalEvalScores()
//                                .stream()
//                                .filter(score -> score.getCriteria() != null && "CRIT006".equals(score.getCriteria().getCriteriaId()))
//                                .findFirst();
//
//                        if (existingScoreOpt.isPresent()) {
//                            ProposalEvalScore existingScore = existingScoreOpt.get();
//                            existingScore.setScore(new BigDecimal(scoreStr));
//                            proposalEvalScoreRepository.save(existingScore);
//                        } else {
//                            // ไม่สร้างใหม่
//                            continue;
//                        }
//                    }
//
//                } else if (evaType.equals("Defense")) {
//                    List<DefenseEvaluation> existingEvas = defenseEvaluationRepository.findByProjectId_ProjectId(projectId);
//
//                    if (existingEvas == null || existingEvas.isEmpty()) {
//                        continue;
//                    }
//
//                    // Loop through all DefenseEvaluation objects
//                    for (DefenseEvaluation defenseEvaluation : existingEvas) {
//                        Optional<DefenseEvalScore> existingScoreOpt = defenseEvaluation.getDefenseEvalScore()
//                                .stream()
//                                .filter(score -> score.getCriteria() != null && "CRIT012".equals(score.getCriteria().getCriteriaId()))
//                                .findFirst();
//
//                        if (existingScoreOpt.isPresent()) {
//                            DefenseEvalScore existingScore = existingScoreOpt.get();
//                            existingScore.setScore(Float.parseFloat(scoreStr));
//                            defenseEvalScoreRepository.save(existingScore);
//                        } else {
//                            // ไม่สร้างใหม่
//                            continue;
//                        }
//                    }
//                }
//            }
//
//            return ResponseEntity.ok("Scores updated where applicable.");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
    @PostMapping("/api/save/timeliness")
    public ResponseEntity<String> saveTimelinessScore(@RequestBody List<TimelinessRequest> requests) {
        try {
            for (TimelinessRequest request : requests) {
                String projectId = request.getProjectId();
                String scoreStr = request.getScore();
                String evaType = request.getEvaType();

                if (evaType.equals("Proposal")) {
                    List<ProposalEvaluation> existingEvas = proposalEvaluationRepository.findByProject_ProjectId(projectId);

                    if (existingEvas == null || existingEvas.isEmpty()) {
                        continue;
                    }

                    for (ProposalEvaluation proposalEvaluation : existingEvas) {
                        Optional<ProposalEvalScore> existingScoreOpt = proposalEvaluation.getProposalEvalScores()
                                .stream()
                                .filter(score -> score.getCriteria() != null && "CRIT006".equals(score.getCriteria().getCriteriaId()))
                                .findFirst();

                        if (existingScoreOpt.isPresent()) {
                            ProposalEvalScore existingScore = existingScoreOpt.get();
                            existingScore.setScore(new BigDecimal(scoreStr));
                            proposalEvalScoreRepository.save(existingScore);
                        } else {
                            // ✅ สร้างใหม่ ถ้าไม่มี
                            ProposalEvalScore newScore = new ProposalEvalScore();
                            String evaId = proposalEvaluation.getProposalId() + "_CRIT006"; // หรือใช้ projectId ก็ได้
                            newScore.setEvaId(evaId);
                            newScore.setScore(new BigDecimal(scoreStr));

                            Criteria criteria = criteriaRepository.findById("CRIT006").orElse(null);
                            if (criteria == null) continue;

                            newScore.setCriteria(criteria);
                            newScore.setProposalEvaluation(proposalEvaluation);

                            proposalEvalScoreRepository.save(newScore);
                        }
                    }

                } else if (evaType.equals("Defense")) {
                    List<DefenseEvaluation> existingEvas = defenseEvaluationRepository.findByProjectId_ProjectId(projectId);

                    if (existingEvas == null || existingEvas.isEmpty()) {
                        continue;
                    }

                    for (DefenseEvaluation defenseEvaluation : existingEvas) {
                        Optional<DefenseEvalScore> existingScoreOpt = defenseEvaluation.getDefenseEvalScore()
                                .stream()
                                .filter(score -> score.getCriteria() != null && "CRIT012".equals(score.getCriteria().getCriteriaId()))
                                .findFirst();

                        if (existingScoreOpt.isPresent()) {
                            DefenseEvalScore existingScore = existingScoreOpt.get();
                            existingScore.setScore(Float.parseFloat(scoreStr));
                            defenseEvalScoreRepository.save(existingScore);
                        } else {
                            // ✅ สร้างใหม่ ถ้าไม่มี
                            DefenseEvalScore newScore = new DefenseEvalScore();
                            String evaId = defenseEvaluation.getDefenseEvaId() + "_CRIT012";
                            newScore.setEvalId(evaId);
                            newScore.setScore(Float.parseFloat(scoreStr));

                            Criteria criteria = criteriaRepository.findById("CRIT012").orElse(null);
                            if (criteria == null) continue;

                            newScore.setCriteria(criteria);
                            newScore.setDefenseEvaluation(defenseEvaluation);

                            defenseEvalScoreRepository.save(newScore);
                        }
                    }
                }
            }

            return ResponseEntity.ok("Scores updated or created where applicable.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }




}
