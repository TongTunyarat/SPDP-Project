package com.example.project.controller;


import com.example.project.DTO.ShowProposalCriteriaDTO;
import com.example.project.DTO.StudentCriteriaDTO;
import com.example.project.entity.Criteria;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.service.ProposalEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProposalEvaluationController {

    //=========================================== USE ===================================================

    // send project when click edit
//    @GetMapping("/instructor/editProposalEvaluation")
//    public String getProjectDetails(@RequestParam String projectId, Model model){
//        System.out.println("ID from click edit: "+ projectId);
//
//        model.addAttribute("projectId", projectId);
//        return "/GiveScore/GivePropEvaScore";
//    }

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public ProposalEvaluationController(ProposalEvaluationService proposalEvaluationService, ProjectInstructorRoleRepository projectInstructorRoleRepository) {
        this.proposalEvaluationService = proposalEvaluationService;
        this.projectInstructorRoleRepository = projectInstructorRoleRepository;
    }

    // get criteria DTO
    @GetMapping("/instructor/criteriaProposal")
    @ResponseBody
    public List<ShowProposalCriteriaDTO> getCriteriaDTO() {

        List<Criteria> criteriaList = proposalEvaluationService.getProposalCriteria();

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

    // get student criteria
    @GetMapping("/instructor/studentProposalCriteria")
    @ResponseBody
    public List<StudentCriteriaDTO> getStudentCriteria(@RequestParam String projectId) {

        List<StudentProject> studentProject = proposalEvaluationService.getStudentCriteria(projectId);

        System.out.println("ID from send editProposalEvaluation: "+ projectId);
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



    //=========================================== NOT USE ===================================================

    // get student criteria
//    @GetMapping("/studentProposalCriteria")
//    @ResponseBody
//    public ResponseEntity<?> getStudentCriteria(@RequestParam String projectId) {
//
//        List<StudentProject> studentProject = proposalEvaluationService.getStudentCriteria(projectId);
//
//        if(studentProject == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Student Project");
//        }
//        return ResponseEntity.ok(studentProject);
//    }



























    //=========================================== NOT USE ===================================================

    // get all criteria
//    @GetMapping("/instructor/criteriaProposal")
//    @ResponseBody
//    public List<Criteria> getProposalCriteria(){
//        return proposalEvaluationService.getProposalCriteria();
//    }
}