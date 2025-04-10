package com.example.project.controller;


import com.example.project.DTO.*;
import com.example.project.DTO.Criteria.ShowProposalCriteriaDTO;
import com.example.project.DTO.Criteria.StudentCriteriaDTO;
import com.example.project.DTO.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.service.ProjectInstructorRoleService;
import com.example.project.service.ProjectService;
import com.example.project.service.ProposalEvaluationService;
import com.example.project.service.ProposalGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/instructor")
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
    private ProposalGradeService proposalGradeService;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectInstructorRoleService projectInstructorRoleService;

    // get criteria DTO
    @GetMapping("/criteriaProposal")
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
    @GetMapping("/studentProposalCriteria")
    @ResponseBody
    public List<StudentCriteriaDTO> getStudentCriteria(@RequestParam String projectId) {

        List<StudentProject> studentProject = proposalEvaluationService.getStudentCriteria(projectId);

        System.out.println("ID from send editProposalEvaluation: " + projectId);
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

    //======================================= GET ProposalEvalScore ========================================

    public ProposalEvaluationController(ProposalEvaluationService proposalEvaluationService, ProposalGradeService proposalGradeService, ProjectInstructorRoleRepository projectInstructorRoleRepository, ProjectService projectService) {
        this.proposalEvaluationService = proposalEvaluationService;
        this.proposalGradeService = proposalGradeService;
        this.projectInstructorRoleRepository = projectInstructorRoleRepository;
        this.projectService = projectService;
    }


    // get score DTO
    @GetMapping("/showScoreProposal")
    @ResponseBody
    public List<ProposalEvalScoreDTO> getScoreProposal(@RequestParam String projectId) {
        // ดึงข้อมูล ProposalEvalScore ตาม projectId
        List<ProposalEvalScore> proposalEvalScoreList = proposalEvaluationService.getProposalEvalScoresByProjectId(projectId);

        // ดึงข้อมูล StudentProject ตาม projectId
        List<StudentProject> studentProjectList = proposalEvaluationService.getStudentCriteria(projectId);

        // สร้าง Set ของ StudentId ที่มี status เป็น "Active"
        Set<String> activeStudentIds = studentProjectList.stream()
                .filter(studentProject -> "Active".equals(studentProject.getStatus()))  // กรองเฉพาะ status เป็น "Active"
                .map(studentProject -> studentProject.getStudent().getStudentId())  // ดึง StudentId
                .collect(Collectors.toSet());

        return proposalEvalScoreList.stream()
                .filter(score -> activeStudentIds.contains(score.getProposalEvaluation().getStudent().getStudentId()))  // กรอง ProposalEvalScore ที่มี StudentId ตรงกับ activeStudentIds
                .map(score -> {
                    // ดึงคะแนนจาก score
                    Double scoreValue = score.getScore() != null ? score.getScore().doubleValue() : 0.0;

                    // สร้างและส่ง ProposalEvalScoreDTO โดยส่ง weight เป็น String
                    return new ProposalEvalScoreDTO(
                            score.getEvaId(),
                            score.getProposalEvaluation().getStudent().getStudentId(),
                            score.getProposalEvaluation().getStudent().getStudentName(),
                            score.getProposalEvaluation().getProject().getProjectId(),
                            score.getCriteria().getCriteriaId(),
                            score.getCriteria().getCriteriaName(),
                            score.getCriteria().getWeight(),  // ส่งคะแนนจริง
                            scoreValue,
                            score.getCriteria().getMaxScore() // ส่ง weight ในรูปแบบ String
                    );
                })
                .collect(Collectors.toList());
    }


    @GetMapping("/showStudentDetails")
    @ResponseBody
    public List<StudentProjectDTO> getStudentDetails(@RequestParam String projectId) {
        List<StudentProject> studentProjectList = projectService.getStudentDetails(projectId);
        return studentProjectList.stream()
                .map(studentProject -> new StudentProjectDTO(
                        studentProject.getStudent().getStudentId(),
                        studentProject.getStudent().getStudentName(),
                        studentProject.getStatus()))
                .toList();
    }


//
//    @GetMapping("/GetProposalEvalScore")
//    @ResponseBody
//    public List<ProposalEvaResponseDTO> getProposalEvaScore(@RequestParam String projectId,
//                                                            @RequestParam(required = false) String instructorName,
//                                                            @RequestParam(required = false) String role) {
//        return proposalGradeService.getFilterProposalEvaScore(projectId, instructorName, role).stream()
//                .map(evaScore -> new ProposalEvaResponseDTO(
//                        evaScore.getEvaId(),
//                        evaScore.getProposalEvaluation().getStudent().getStudentId(),
//                        evaScore.getProposalEvaluation().getStudent().getStudentName(),
//                        evaScore.getCriteria().getCriteriaId(),
//                        evaScore.getCriteria().getCriteriaName(),
//                        evaScore.getCriteria().getType(),
//                        evaScore.getScore().doubleValue()
//                )).collect(Collectors.toList());
//    }
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
