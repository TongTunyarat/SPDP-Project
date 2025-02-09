package com.example.project.controller;

import com.example.project.DTO.*;
import com.example.project.entity.Criteria;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.service.DefenseGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DefenseGradeController {

    //=========================================== USE ===================================================

    // ShowProposalGrade
//    @GetMapping("/instructor/ShowProjectDefenseGrade")
//    public String getGiveGradePage() {
//        return "ShowDefenseGradeProject";
//    }

    // send project when click edit
//    @GetMapping("/instructor/editDefenseGrade")
//    public String getProjectDetails(@RequestParam String projectId, Model model){
//        System.out.println("ID from click edit: "+ projectId);
//
//        model.addAttribute("projectId", projectId);
//        return "/GiveScore/GiveDefGradeScore";
//    }

    @Autowired
    private DefenseGradeService defenseGradeService;

    // get criteria DTO
    @GetMapping("/instructor/criteriaDefenseGrade")
    @ResponseBody
    public List<ShowProposalCriteriaDTO> getCriteriaDTO() {

        List<Criteria> criteriaList = defenseGradeService.getDefenseCriteria();

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

    // instructor project modal
    @GetMapping("/instructor/projectDefense")
    @ResponseBody
    public List<InstructorProjectListDTO> getInstructorProject(String projectId) {
        List<ProjectInstructorRole> projectInstructorRoleList = defenseGradeService.getInstructorProject(projectId);
        return projectInstructorRoleList.stream()
                .filter(instructorRole -> "Committee".equals(instructorRole.getRole())||"Advisor".equals(instructorRole.getRole()))
                .map(instructor ->
                        new InstructorProjectListDTO(
                                instructor.getInstructorId(),
                                instructor.getRole(),
                                instructor.getProjectIdRole().getProjectId(),
                                instructor.getInstructor().getProfessorId(),
                                instructor.getInstructor().getProfessorName()
                        )).collect(Collectors.toList());
    }

    // getProposalEvaScore
    // request and response modal
    @GetMapping("/instructor/GetDefenseEvalScoreModal")
    @ResponseBody
    public List<DefenseEvaResponseDTO> getFilterDefenseEvaScore(@ModelAttribute DefenseEvaRequestDTO resquestDTO) {
        return defenseGradeService.getFilterDefenseEvaScore(
                        resquestDTO.getProjectId(), resquestDTO.getInstructorName(), resquestDTO.getRole()).stream()
                .map(evaScore ->
                        new DefenseEvaResponseDTO(
                                evaScore.getEvalId(),
                                evaScore.getDefenseEvaluation().getStudent().getStudentId(),
                                evaScore.getDefenseEvaluation().getStudent().getStudentName(),
                                evaScore.getCriteria().getCriteriaId(),
                                evaScore.getCriteria().getCriteriaName(),
                                evaScore.getCriteria().getType(),
                                evaScore.getScore().doubleValue()
                        )).collect(Collectors.toList());
    }

}