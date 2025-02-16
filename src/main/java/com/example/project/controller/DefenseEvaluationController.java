package com.example.project.controller;

import com.example.project.DTO.Criteria.ShowProposalCriteriaDTO;
import com.example.project.DTO.Criteria.StudentCriteriaDTO;
import com.example.project.entity.Criteria;
import com.example.project.entity.StudentProject;
import com.example.project.service.DefenseEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
    private DefenseEvaluationService defenseEvaluationService;

    public DefenseEvaluationController(DefenseEvaluationService defenseEvaluationService) {
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
}