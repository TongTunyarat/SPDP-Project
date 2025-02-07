package com.example.project.controller;

import com.example.project.DTO.InstructorProjectListDTO;
import com.example.project.DTO.ShowProposalCriteriaDTO;
import com.example.project.entity.Criteria;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ProposalEvalScore;
import com.example.project.repository.ProposalEvalScoreRepository;
import com.example.project.service.ProposalGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProposalGradeController {

    //=========================================== USE ===================================================

    // ShowProposalGrade
//    @GetMapping("/instructor/ShowProjectProposalGrade")
//    public String getGiveGradePage() {
//        return "ShowProposalGradeProject";
//    }

    // send project when click edit
//    @GetMapping("/instructor/editProposalGrade")
//    public String getProjectDetails(@RequestParam String projectId, Model model){
//        System.out.println("ID from click edit: "+ projectId);
//
//        model.addAttribute("projectId", projectId);
//        return "/GiveScore/GivePropGradeScore";
//    }

    @Autowired
    private ProposalGradeService proposalGradeService;
    @Autowired
    private ProposalEvalScoreRepository proposalEvalScoreRepository;

    public ProposalGradeController(ProposalGradeService proposalGradeService, ProposalEvalScoreRepository proposalEvalScoreRepository) {
        this.proposalGradeService = proposalGradeService;
        this.proposalEvalScoreRepository = proposalEvalScoreRepository;
    }

    // get criteria DTO
    @GetMapping("/instructor/criteriaProposalGrade")
    @ResponseBody
    public List<ShowProposalCriteriaDTO> getCriteriaDTO() {

        List<Criteria> criteriaList = proposalGradeService.getProposalCriteria();

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

    // get instructor from project
    @GetMapping("/project/instructor")
    @ResponseBody
    public List<InstructorProjectListDTO> getInstructorProject(String projectId) {
        List<ProjectInstructorRole> projectInstructorRoleList = proposalGradeService.getInstructorProject(projectId);
        return projectInstructorRoleList.stream()
                .map(instructor ->
                        new InstructorProjectListDTO(
                                instructor.getInstructorId(),
                                instructor.getRole(),
                                instructor.getProjectIdRole().getProjectId(),
                                instructor.getInstructor().getProfessorId(),
                                instructor.getInstructor().getProfessorName()
                        )).collect(Collectors.toList());
    }

    @GetMapping("/ProposalEvalScore")
    @ResponseBody
    public List<ProposalEvalScore> getProposalEvalScore() {
        return proposalEvalScoreRepository.findAll();
    }




















    //=========================================== NOT USE ===================================================

//    // get student criteria
//    @GetMapping("/instructor/studentProposalGradeCriteria")
//    @ResponseBody
//    public List<StudentCriteriaDTO> getStudentCriteria(@RequestParam String projectId) {
//
//        List<StudentProject> studentProject = proposalGradeService.getStudentCriteria(projectId);
//
//        System.out.println("ID from send editProposalEvaluation: "+ projectId);
//        return studentProject.stream()
//                .filter(student -> "Active".equals(student.getStatus()))
//                .map(student ->
//                        new StudentCriteriaDTO(
//                                student.getStudentPjId(),
//                                student.getStudent().getStudentId(),
//                                student.getStudent().getStudentName(),
//                                student.getStudent().getProgram(),
//                                student.getStudent().getSection(),
//                                student.getStudent().getTrack(),
//                                student.getStudent().getEmail(),
//                                student.getStudent().getPhone(),
//                                student.getProject().getProjectId(),
//                                student.getProject().getProjectTitle(),
//                                student.getStatus()
//                        )).collect(Collectors.toList());
//    }

}