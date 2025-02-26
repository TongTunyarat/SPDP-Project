package com.example.project.controller;

import com.example.project.DTO.Score.*;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.CalculateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
public class CalculateController {

    @Autowired
    private CalculateService calculateService;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;
    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;
    @Autowired
    private PosterEvaRepository posterEvaRepository;
    @Autowired
    private GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;


    @PostMapping("/testsave")
    public void testSaveEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId,
            @RequestBody List<ScoreDTO> scores) {
        System.out.println("Save evaluation");
        System.out.println("instructorId: " + instructorId);
        System.out.println("projectId: " + projectId);
        System.out.println("studentId: " + studentId);
        System.out.println("scores: " + scores);
    }

    @GetMapping("/get-instructorId")
    public String getInstructorId(@RequestParam("projectId") String projectId) {
        // ดึง username จาก SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // ค้นหา Account จาก username
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            return "Account not found";  // ถ้าไม่พบ Account
        }
        // ค้นหา Instructor โดยใช้ Account object
        Instructor instructor = instructorRepository.findByAccount(account);
        if (instructor == null) {
            return "Instructor not found";  // ถ้าไม่พบ Instructor
        }

        // ค้นหา ProjectInstructorRole โดยใช้ professor_id และ project_id
        Project project = projectRepository.findByProjectId(projectId);
        ProjectInstructorRole instructorRole = projectInstructorRoleRepository.findByInstructorAndProjectIdRole(instructor, project);
        if (instructorRole == null) {
            return "ProjectInstructorRole not found";  // ถ้าไม่พบ ProjectInstructorRole
        }

        System.out.println("Instructor ID: " + instructorRole.getInstructorId());
        // ส่งค่า instructor_id ที่ได้จาก ProjectInstructorRole
        return instructorRole.getInstructorId();  // หรือ return ค่าอื่นที่คุณต้องการจาก ProjectInstructorRole
    }


//    @PostMapping("/saveEvaluation")
//    public ResponseEntity<String> saveEvaluation(
//            @RequestParam String instructorId,
//            @RequestParam String projectId,
//            @RequestParam String studentId,
//            @RequestBody List<ScoreDTO> scores) {
//        try {
//            System.out.println("Save Evaluation Controller");
//
//            ProjectInstructorRole instructor = findInstructor(instructorId);
//            Project project = findProject(projectId);
//            Student student = findStudent(studentId);
//
//            // ✅ เรียกใช้ Service
//            calculateService.saveEvaluation(instructor, project, student, scores);
//
//            return ResponseEntity.ok("Evaluation saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
//        }
//    }
    @PostMapping("/saveEvaluation")
    public ResponseEntity<String> saveEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId,
            @RequestBody EvaluationRequest request) {
        try {
            System.out.println("🐔 request.getScores(): " + request.getScores());
            System.out.println("🐔 request.getComment(): " + request.getComment());

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            calculateService.saveEvaluation(instructor, project, student, request.getScores(), request.getComment());

            return ResponseEntity.ok("Evaluation saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/getEvaluation")
    public ResponseEntity<?> getEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId) {
        try {
            System.out.println("Get Evaluation Controller");

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            ProposalEvaluation scores = proposalEvaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(
                    instructor, project, student);
            System.out.println("[Controller] Get Score Response: "+scores);

            return ResponseEntity.ok(scores); // ส่งค่าคะแนนกลับไป
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

//    @PostMapping("/saveDefenseEvaluation")
//    public ResponseEntity<String> saveDefenseEvaluation(
//            @RequestParam String instructorId,
//            @RequestParam String projectId,
//            @RequestParam String studentId,
//            @RequestBody List<ScoreDTO> scores) {
//        try {
//            System.out.println("Save Evaluation Controller");
//
//            ProjectInstructorRole instructor = findInstructor(instructorId);
//            Project project = findProject(projectId);
//            Student student = findStudent(studentId);
//
//            // ✅ เรียกใช้ Service
//            calculateService.saveDefenseEvaluation(instructor, project, student, scores);
//
//            return ResponseEntity.ok("Evaluation saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
//        }
//    }
    @PostMapping("/saveDefenseEvaluation")
    public ResponseEntity<String> saveDefenseEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId,
            @RequestBody EvaluationRequest request) {
        try {
            System.out.println("Save Evaluation Controller");
            System.out.println("🐔 request.getScores(): " + request.getScores());
            System.out.println("🐔 request.getComment(): " + request.getComment());

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            // ✅ เรียกใช้ Service
            calculateService.saveDefenseEvaluation(instructor, project, student, request.getScores(), request.getComment());

            return ResponseEntity.ok("Evaluation saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getDefenseEvaluation")
    public ResponseEntity<?> getDefenseEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId) {
        try {
            System.out.println("Get Evaluation Controller");

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            DefenseEvaluation scores = defenseEvaluationRepository.findByDefenseInstructorIdAndProjectIdAndStudent(
                    instructor, project, student);
            System.out.println("[Controller] Get Score Response: "+scores);

            return ResponseEntity.ok(scores); // ส่งค่าคะแนนกลับไป
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

//    @PostMapping("/savePosterEvaluation")
//    public ResponseEntity<String> savePosterEvaluation(
//            @RequestParam String instructorId,
//            @RequestParam String projectId,
//            @RequestBody List<ScoreDTO> scores) {
//        try {
//            System.out.println("Save Evaluation Controller");
//
//            ProjectInstructorRole instructor = findInstructor(instructorId);
//            Project project = findProject(projectId);
//
//            // ✅ เรียกใช้ Service
//            calculateService.savePosterEvaluation(instructor, project, scores);
//
//            return ResponseEntity.ok("Evaluation saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
//        }
//    }
    @PostMapping("/savePosterEvaluation")
    public ResponseEntity<String> savePosterEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestBody EvaluationRequest request) {
        try {
            System.out.println("Save Evaluation Controller");

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);

            // ✅ เรียกใช้ Service พร้อมกับส่งคอมเมนต์ไปด้วย
            calculateService.savePosterEvaluation(instructor, project, request.getScores(), request.getComment());

            return ResponseEntity.ok("Evaluation saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }



    @GetMapping("/getPosterEvaluation")
    public ResponseEntity<?> getPosterEvaluation(
            @RequestParam String instructorId,
            @RequestParam String projectId) {
        try {
            System.out.println("Get Poster Evaluation Process...");

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);

            PosterEvaluation scores = posterEvaRepository.findByInstructorIdPosterAndProjectIdPoster(instructor, project);
            System.out.println("[Controller] Get Score Response: "+scores);

            return ResponseEntity.ok(scores); // ส่งค่าคะแนนกลับไป
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/scoreTotal")
    public ResponseEntity<StudentScoreResponse> getStudentTotalScore(
            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId,
            @RequestParam String evalType) {

        try {
            System.out.println("Get Score Response Process...");
            System.out.println("📌 instructorId = " + instructorId);
            System.out.println("📌 projectId = " + projectId);
            System.out.println("📌 studentId = " + studentId);
            System.out.println("📌 evalType = " + evalType);

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            StudentScoreDTO studentScoreDTO = null;

            if (evalType.equalsIgnoreCase("proposal")) {
                studentScoreDTO = calculateService.calculateTotalScoreProposal(instructor, project, student);
            } else if (evalType.equalsIgnoreCase("defense")) {
                studentScoreDTO = calculateService.calculateTotalScoreDefense(instructor, project, student);
            } else {
                return ResponseEntity.badRequest().body(null);
            }

            System.out.println("[Controller] Get Score studentScoreDTO: "+studentScoreDTO);

            // Construct response object
            StudentScoreResponse response = new StudentScoreResponse(
                    student.getStudentId(),
                    student.getStudentName(),
                    studentScoreDTO.getScores(),
                    studentScoreDTO.getRawTotalScore(),  // คะแนนเต็ม
                    studentScoreDTO.getTotalScore()      // คะแนนหลังคิดเป็น %
            );

            System.out.println("[Controller] Get Score Response: "+response);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/scoreTotalPoster")
    public ResponseEntity<StudentScorePosterResponse> getStudentTotalScorePoster(
            @RequestParam String instructorId,
            @RequestParam String projectId ){

        try {
            System.out.println("Get Score Response Process...");
            System.out.println("📌 instructorId = " + instructorId);
            System.out.println("📌 projectId = " + projectId);

            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);

            StudentScoreDTO studentScoreDTO = null;

            studentScoreDTO = calculateService.calculateTotalScorePoster(instructor, project);

            System.out.println("[Controller] Get Score studentScoreDTO: "+studentScoreDTO);

            // Construct response object
            StudentScorePosterResponse response = new StudentScorePosterResponse(
                    studentScoreDTO.getScores(),
                    studentScoreDTO.getRawTotalScore(),  // คะแนนเต็ม
                    studentScoreDTO.getTotalScore()      // คะแนนหลังคิดเป็น %
            );

            System.out.println("[Controller] Get Score Response: "+response);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // ------------------ Grade --------------------------//
    @PostMapping("/saveProposalGrade")
    public ResponseEntity<String> saveProposalGrade(
            @RequestParam String projectId,
            @RequestParam String studentId,
            @RequestBody ScoreRequestDTO scoreRequest) {
        try {
            System.out.println("Save Grade Controller");

            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            // ✅ เรียกใช้ Service
            String grade = calculateService.saveProposalGrade(project, student, scoreRequest);

            return ResponseEntity.ok("Grading Result: " + grade);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/getGradeProposal")
    public ResponseEntity<?> getGradeProposal(
            @RequestParam String projectId,
            @RequestParam String studentId) {
        try {
            System.out.println("Get Grade Controller");

            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            GradingProposalEvaluation score = gradingProposalEvaluationRepository.findByProjectAndStudent(project, student);
            System.out.println("[Controller] Get Score Response: "+score);

            return ResponseEntity.ok(score); // ส่งค่าคะแนนกลับไป
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/saveDefenseGrade")
    public ResponseEntity<String> saveDefenseGrade(
            @RequestParam String projectId,
            @RequestParam String studentId,
            @RequestBody DefenseScoreRequestDTO scoreRequest) {
        try {
            System.out.println("Save Grade Controller");

            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            // ✅ เรียกใช้ Service
            String grade = calculateService.saveDefenseGrade(project, student, scoreRequest);

            return ResponseEntity.ok("Grading Result: "+grade);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getGradeDefense")
    public ResponseEntity<?> getGradeDefense(
//            @RequestParam String instructorId,
            @RequestParam String projectId,
            @RequestParam String studentId) {
        try {
            System.out.println("Get Grade Controller");

//            ProjectInstructorRole instructor = findInstructor(instructorId);
            Project project = findProject(projectId);
            Student student = findStudent(studentId);

            GradingDefenseEvaluation score = gradingDefenseEvaluationRepository.findByProjectIdAndStudentId(project, student);
            System.out.println("[Controller] Get Score Response: "+score);

            return ResponseEntity.ok(score); // ส่งค่าคะแนนกลับไป
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
















    private ProjectInstructorRole findInstructor(String instructorId) {
        return projectInstructorRoleRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found: " + instructorId));
    }

    private Project findProject(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
    }

    private Student findStudent(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
    }

}
