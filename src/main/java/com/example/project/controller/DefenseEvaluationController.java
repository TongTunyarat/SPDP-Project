package com.example.project.controller;

import com.example.project.DTO.Criteria.ShowProposalCriteriaDTO;
import com.example.project.DTO.Criteria.StudentCriteriaDTO;
import com.example.project.DTO.DefenseEvaResponseDTO;
import com.example.project.DTO.DefenseEvaScoreDTO;
import com.example.project.DTO.ProposalEvalScoreDTO;
import com.example.project.entity.*;
import com.example.project.service.DefenseEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DefenseEvaluationController {


    //=========================================== USE ===================================================

    // ShowDefenseProject
//    @GetMapping("/instructor/ShowDefenseEvaProject")
//    public String getGiveGradePage() {
//        return "ShowDefenseEvaProject";
//    }

    // send project when click edit
//    @GetMapping("/instructor/editDefenseEvaluation")
//    public String getProjectDetails(@RequestParam String projectId, Model model){
//        System.out.println("ID from click edit: "+ projectId);
//
//        model.addAttribute("projectId", projectId);
//        return "/GiveScore/GiveDefEvaScore";
//    }
    @Autowired
    private DefenseEvaluationService defenseEvalService;

    @Autowired
    private DefenseEvaluationService defenseEvaluationService;

    public DefenseEvaluationController(DefenseEvaluationService defenseEvalService, DefenseEvaluationService defenseEvaluationService) {
        this.defenseEvalService = defenseEvalService;
        this.defenseEvaluationService = defenseEvaluationService;
    }

    // get criteria DTO
    @GetMapping("/instructor/criteriaDefense")
    @ResponseBody
    public List<ShowProposalCriteriaDTO> getCriteriaDTO() {

        List<Criteria> criteriaList = defenseEvaluationService.getDefenseCriteria();

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

    @GetMapping("/instructor/studentDefenseCriteria")
    @ResponseBody
    public List<StudentCriteriaDTO> getStudentCriteria(@RequestParam String projectId) {

        List<StudentProject> studentProject = defenseEvaluationService.getStudentCriteria(projectId);

        System.out.println("ID from send editDefenseEvaluation: "+ projectId);
        return studentProject.stream()
                .filter(student -> "Active".equals(student.getStatus()))
                .map(student ->
                        new StudentCriteriaDTO(
                                student.getStudentPjId(),
                                student.getStudent().getStudentId(),
                                student.getStudent().getStudentName(),
                                student.getStudent().getProgram(),
                                student.getStudent().getSection(),
                                student.getStudent().getTrack(),
                                student.getStudent().getEmail(),
                                student.getStudent().getPhone(),
                                student.getProject().getProjectId(),
                                student.getProject().getProjectTitle(),
                                student.getStatus()
                        )).collect(Collectors.toList());
    }


    // get score DTO
    @GetMapping("/instructor/showScoreDefense")
    @ResponseBody
    public List<DefenseEvaScoreDTO> getScoreDefense(@RequestParam String projectId) {
        // ดึงข้อมูล DefenseEvalScore ตาม projectId
        List<DefenseEvalScore> defenseEvalScoreList = defenseEvaluationService.getDefenseEvalScoresByProjectId(projectId);

        // ดึงข้อมูล StudentProject ตาม projectId
        List<StudentProject> studentProjectList = defenseEvaluationService.getStudentCriteria(projectId);

        // สร้าง Set ของ StudentId ที่มี status เป็น "Active"
        Set<String> activeStudentIds = studentProjectList.stream()
                .filter(studentProject -> "Active".equals(studentProject.getStatus()))  // กรองเฉพาะ status เป็น "Active"
                .map(studentProject -> studentProject.getStudent().getStudentId())  // ดึง StudentId
                .collect(Collectors.toSet());

        return defenseEvalScoreList.stream()
                .filter(score -> {
                    // ตรวจสอบว่า studentId จาก DefenseEvaluation มีอยู่ใน activeStudentIds หรือไม่
                    String studentId = score.getDefenseEvaluation().getStudent().getStudentId();  // ดึง studentId จาก DefenseEvaluation
                    return activeStudentIds.contains(studentId);  // กรองเฉพาะที่มี status "Active"
                })
                .map(score -> {
                    // ดึงคะแนนจาก score
                    Double scoreValue = score.getScore() != null ? score.getScore().doubleValue() : 0.0;

                    // สร้างและส่ง DefenseEvaScoreDTO โดยส่ง weight เป็น String
                    return new DefenseEvaScoreDTO(
                            score.getEvalId(),
                            score.getDefenseEvaluation().getStudent().getStudentId(),
                            score.getDefenseEvaluation().getStudent().getStudentName(),
                            score.getDefenseEvaluation().getProjectId().getProjectId(),
                            score.getCriteria().getCriteriaId(),
                            score.getCriteria().getCriteriaName(),
                            score.getCriteria().getWeight(),  // ส่งคะแนนจริง
                            scoreValue,
                            score.getCriteria().getMaxScore() // ส่ง weight ในรูปแบบ String
                    );
                })
                .collect(Collectors.toList());
    }

//    @GetMapping("/instructor/showScoreDefense")
//    @ResponseBody
//    public List<DefenseEvaScoreDTO> getScoreDefense(@RequestParam String projectId) {
//        // ดึงข้อมูล ProposalEvalScore ตาม projectId
//        List<DefenseEvalScore> defenseEvalScoreList = defenseEvalService.getDefenseEvaScoresByProjectId(projectId);
//
//        return defenseEvalScoreList.stream()
//                .map(score -> new DefenseEvaScoreDTO(
//                        score.getEvalId(),
//                        score.getDefenseEvaluation().getStudent().getStudentId(),
//                        score.getDefenseEvaluation().getStudent().getStudentName(),
//                        score.getDefenseEvaluation().getProjectId().getProjectId(),
//                        score.getCriteria().getCriteriaId(),
//                        score.getCriteria().getCriteriaName(),
//                        score.getScore() != null ? score.getScore().doubleValue() : 0.0
//
//                )).collect(Collectors.toList());
//    }
}