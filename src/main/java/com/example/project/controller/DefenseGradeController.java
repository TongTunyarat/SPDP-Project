package com.example.project.controller;

import com.example.project.DTO.Criteria.ShowProposalCriteriaDTO;
import com.example.project.DTO.DefenseEvaRequestDTO;
import com.example.project.DTO.DefenseEvaResponseDTO;
import com.example.project.DTO.DefenseGradeEvaResponseDTO;
import com.example.project.DTO.InstructorProjectListDTO;
import com.example.project.entity.*;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.service.DefenseEvaluationService;
import com.example.project.service.DefenseGradeService;
import com.example.project.service.ProposalEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;
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

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private DefenseEvaluationService defenseEvaluationService;

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;


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
    public List<DefenseGradeEvaResponseDTO> getFilterDefenseEvaScore(@ModelAttribute DefenseEvaRequestDTO resquestDTO) {
        return defenseGradeService.getFilterDefenseEvaScore(
                        resquestDTO.getProjectId(), resquestDTO.getInstructorName(), resquestDTO.getRole()).stream()
                .map(evaScore ->
                        new DefenseGradeEvaResponseDTO(
                                evaScore.getEvalId(),
                                evaScore.getDefenseEvaluation().getStudent().getStudentId(),
                                evaScore.getDefenseEvaluation().getStudent().getStudentName(),
                                evaScore.getCriteria().getCriteriaId(),
                                evaScore.getCriteria().getCriteriaName(),
                                evaScore.getCriteria().getType(),
                                evaScore.getScore().doubleValue(),
                                evaScore.getDefenseEvaluation().getComment()
                        )).collect(Collectors.toList());
    }

    public DefenseGradeController(DefenseEvaluationService defenseEvaluationService, DefenseGradeService defenseGradeService ) {
        this.defenseEvaluationService = defenseEvaluationService;
        this.defenseGradeService = defenseGradeService;
    }

    @GetMapping("/instructor/showGradeScoreDefense")
    @ResponseBody
    public List<DefenseEvaResponseDTO> getScoreDefense(@RequestParam String projectId) {
        // ดึงข้อมูล DefenseEvalScore ตาม projectId
        List<DefenseEvalScore> defenseEvalScoreList = defenseGradeService.getDefenseEvalScoresByProjectId(projectId);

        // สร้าง Set ของ StudentId ที่มี status เป็น "Active"
        List<StudentProject> studentProjectList = defenseEvaluationService.getStudentCriteria(projectId);

        Set<String> activeStudentIds = studentProjectList.stream()
                .filter(studentProject -> "Active".equals(studentProject.getStatus()))  // กรองเฉพาะ status เป็น "Active"
                .map(studentProject -> studentProject.getStudent().getStudentId())  // ดึง StudentId
                .collect(Collectors.toSet());

        return defenseEvalScoreList.stream()
                .filter(score -> activeStudentIds.contains(score.getDefenseEvaluation().getStudent().getStudentId()))  // กรอง DefenseEvalScore ที่มี StudentId ตรงกับ activeStudentIds
                .map(score -> new DefenseEvaResponseDTO(
                        score.getDefenseEvaluation().getDefenseEvaId(),
                        score.getDefenseEvaluation().getStudent().getStudentId(),
                        score.getDefenseEvaluation().getStudent().getStudentName(),
                        score.getCriteria().getCriteriaId(),
                        score.getCriteria().getCriteriaName(),
                        score.getScore()
                ))
                .collect(Collectors.toList());
    }




}