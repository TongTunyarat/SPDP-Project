package com.example.project.controller;

import com.example.project.DTO.Score.*;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.CalculateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculateControllerTest {
    @Mock
    CalculateService calculateService;
    @Mock
    ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    InstructorRepository instructorRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    ProposalEvaluationRepository proposalEvaluationRepository;
    @Mock
    DefenseEvaluationRepository defenseEvaluationRepository;
    @Mock
    PosterEvaRepository posterEvaRepository;
    @Mock
    GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    @Mock
    GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;
    @InjectMocks
    CalculateController calculateController;


    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Mock SecurityContext
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        Authentication mockAuthentication = mock(Authentication.class);

        // Mock username ที่จะถูกดึงจาก Authentication
        when(mockAuthentication.getName()).thenReturn("testUsername");

        // ตั้งค่าการ mock ให้ SecurityContextHolder ใช้ mockSecurityContext
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);

        // ตั้งค่า SecurityContextHolder ให้ใช้ mock
        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @Test
    void testGetInstructorId() {
        // Mock Account, Instructor, Project และ ProjectInstructorRole
        Account mockAccount = new Account();
        Instructor mockInstructor = new Instructor();

        Project mockProject = new Project("ICT SP2024-15", "ICT", "2025", "Mind Bloom: A Mobile Application for Mental Health Self-Care",
                "Develop", "มายด์บลูม แอปพลิเคชันมือถือสำหรับดูแลสุขภาพทางจิตใจด้วยตนเอง",
                LocalDateTime.of(2025, Month.APRIL, 8, 22, 27, 28),
                LocalDateTime.of(2025, Month.APRIL, 8, 22, 27, 28), new Account(), new Account());
        ProjectInstructorRole mockProjectInstructorRole = new ProjectInstructorRole();
        mockProjectInstructorRole.setInstructorId("12345");

        // Mock repository calls
        when(accountRepository.findByUsername("testUsername")).thenReturn(mockAccount);
        when(instructorRepository.findByAccount(mockAccount)).thenReturn(mockInstructor);
        when(projectRepository.findByProjectId("5555")).thenReturn(mockProject);
        when(projectInstructorRoleRepository.findByInstructorAndProjectIdRole(mockInstructor, mockProject)).thenReturn(mockProjectInstructorRole);

        // Call method to test
        String result = calculateController.getInstructorId("5555");

        // Assert that the result matches expected instructorId
        assertEquals("12345", result);
    }

    @Test
    void testSaveEvaluation() {
        // Mock ข้อมูลที่จะถูกใช้ในการทดสอบ
        ProjectInstructorRole mockInstructor = new ProjectInstructorRole(); // mock project instructor role
        Project mockProject = new Project(); // mock project
        Student mockStudent = new Student(); // mock student

        // หาก ID เป็น String
        when(projectInstructorRoleRepository.findById(anyString())).thenReturn(Optional.of(mockInstructor));
        when(studentRepository.findById(anyString())).thenReturn(Optional.of(mockStudent));
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(mockProject));

        // Mock calculateService เพื่อให้ทดสอบได้
        doNothing().when(calculateService).saveEvaluation(any(ProjectInstructorRole.class), any(Project.class), any(Student.class), anyList(), anyString());

        // สร้าง request body
        EvaluationRequest request = new EvaluationRequest();
        request.setScores(new ArrayList<ScoreDTO>());
        request.setComment("Good job");

        // เรียกใช้ method ที่ทดสอบ
        ResponseEntity<String> result = calculateController.saveEvaluation("instructorId", "projectId", "studentId", request);

        // ตรวจสอบว่าเรียก method saveEvaluation ใน calculateService และผลลัพธ์ที่ได้คือ ResponseEntity ที่คาดหวัง
        verify(calculateService).saveEvaluation(any(ProjectInstructorRole.class), any(Project.class), any(Student.class), anyList(), anyString());
        Assertions.assertEquals(ResponseEntity.ok("Evaluation saved successfully"), result);
    }



    //
//    @Test
//    void testGetEvaluation() {
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findByProjectInstructorRoleAndProjectAndStudent(any(ProjectInstructorRole.class), any(Project.class), any(Student.class))).thenReturn(new ProposalEvaluation());
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<?> result = calculateController.getEvaluation("instructorId", "projectId", "studentId");
//        Assertions.assertEquals(new ResponseEntity<?>(null, null, 0), result);
//    }
//
//    @Test
//    void testSaveDefenseEvaluation() {
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<String> result = calculateController.saveDefenseEvaluation("instructorId", "projectId", "studentId", new EvaluationRequest());
//        verify(calculateService).saveDefenseEvaluation(any(ProjectInstructorRole.class), any(Project.class), any(Student.class), any(List<ScoreDTO>.class), anyString());
//        Assertions.assertEquals(new ResponseEntity<String>("body", null, 0), result);
//    }
//
    @Test
    void testGetDefenseEvaluation() {
        // Mock สิ่งที่ findInstructor/findProject/findStudent ต้องใช้
        ProjectInstructorRole mockInstructor = new ProjectInstructorRole();
        Project mockProject = new Project();
        Student mockStudent = new Student();
        DefenseEvaluation mockDefenseEvaluation = new DefenseEvaluation();

        // Mock การหา instructor, project, student
        when(projectInstructorRoleRepository.findById(any())).thenReturn(Optional.of(mockInstructor));
        when(projectRepository.findById(any())).thenReturn(Optional.of(mockProject));
        when(studentRepository.findById(any())).thenReturn(Optional.of(mockStudent));

        // Mock หา DefenseEvaluation
        when(defenseEvaluationRepository.findByDefenseInstructorIdAndProjectIdAndStudent(
                any(ProjectInstructorRole.class), any(Project.class), any(Student.class)))
                .thenReturn(mockDefenseEvaluation);

        // Act
        ResponseEntity<?> result = calculateController.getDefenseEvaluation("instructorId", "projectId", "studentId");

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockDefenseEvaluation, result.getBody());
    }

//
//    @Test
//    void testSavePosterEvaluation() {
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<String> result = calculateController.savePosterEvaluation("instructorId", "projectId", new EvaluationRequest());
//        verify(calculateService).savePosterEvaluation(any(ProjectInstructorRole.class), any(Project.class), any(List<ScoreDTO>.class), anyString());
//        Assertions.assertEquals(new ResponseEntity<String>("body", null, 0), result);
//    }
//
//    @Test
//    void testGetPosterEvaluation() {
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findByInstructorIdPosterAndProjectIdPoster(any(ProjectInstructorRole.class), any(Project.class))).thenReturn(new PosterEvaluation());
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<?> result = calculateController.getPosterEvaluation("instructorId", "projectId");
//        Assertions.assertEquals(new ResponseEntity<?>(null, null, 0), result);
//    }
//
//    @Test
//    void testGetStudentTotalScore() {
//        when(calculateService.calculateTotalScoreProposal(any(ProjectInstructorRole.class), any(Project.class), any(Student.class))).thenReturn(new StudentScoreDTO(List.of(new ScoreDetail("criteriaId", 0d)), 0d, 0d));
//        when(calculateService.calculateTotalScoreDefense(any(ProjectInstructorRole.class), any(Project.class), any(Student.class))).thenReturn(new StudentScoreDTO(List.of(new ScoreDetail("criteriaId", 0d)), 0d, 0d));
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<StudentScoreResponse> result = calculateController.getStudentTotalScore("instructorId", "projectId", "studentId", "evalType");
//        Assertions.assertEquals(new ResponseEntity<StudentScoreResponse>(new StudentScoreResponse("studentId", "studentName", List.of(new ScoreDetail("criteriaId", 0d)), 0d, 0d), null, 0), result);
//    }
//
//    @Test
//    void testGetStudentTotalScorePoster() {
//        when(calculateService.calculateTotalScorePoster(any(ProjectInstructorRole.class), any(Project.class))).thenReturn(new StudentScoreDTO(List.of(new ScoreDetail("criteriaId", 0d)), 0d, 0d));
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<StudentScorePosterResponse> result = calculateController.getStudentTotalScorePoster("instructorId", "projectId");
//        Assertions.assertEquals(new ResponseEntity<StudentScorePosterResponse>(new StudentScorePosterResponse(List.of(new ScoreDetail("criteriaId", 0d)), 0d, 0d), null, 0), result);
//    }
//
//    @Test
//    void testSaveProposalGrade() {
//        when(calculateService.saveProposalGrade(any(Project.class), any(Student.class), any(ScoreRequestDTO.class))).thenReturn("saveProposalGradeResponse");
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<String> result = calculateController.saveProposalGrade("projectId", "studentId", new ScoreRequestDTO());
//        Assertions.assertEquals(new ResponseEntity<String>("body", null, 0), result);
//    }
//
//    @Test
//    void testGetGradeProposal() {
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findByProjectAndStudent(any(Project.class), any(Student.class))).thenReturn(new GradingProposalEvaluation());
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<?> result = calculateController.getGradeProposal("projectId", "studentId");
//        Assertions.assertEquals(new ResponseEntity<?>(null, null, 0), result);
//    }
//
//    @Test
//    void testSaveDefenseGrade() {
//        when(calculateService.saveDefenseGrade(any(Project.class), any(Student.class), any(DefenseScoreRequestDTO.class))).thenReturn("saveDefenseGradeResponse");
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<String> result = calculateController.saveDefenseGrade("projectId", "studentId", new DefenseScoreRequestDTO());
//        Assertions.assertEquals(new ResponseEntity<String>("body", null, 0), result);
//    }
//
//    @Test
//    void testGetGradeDefense() {
//        when(projectInstructorRoleRepository.findById(any(ID.class))).thenReturn(null);
//        when(studentRepository.findById(any(ID.class))).thenReturn(null);
//        when(projectRepository.findById(any(ID.class))).thenReturn(null);
//        when(instructorRepository.findById(any(ID.class))).thenReturn(null);
//        when(accountRepository.findById(any(ID.class))).thenReturn(null);
//        when(proposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(defenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(posterEvaRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingProposalEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//        when(gradingDefenseEvaluationRepository.findByProjectIdAndStudentId(any(Project.class), any(Student.class))).thenReturn(new GradingDefenseEvaluation());
//        when(gradingDefenseEvaluationRepository.findById(any(ID.class))).thenReturn(null);
//
//        ResponseEntity<?> result = calculateController.getGradeDefense("projectId", "studentId");
//        Assertions.assertEquals(new ResponseEntity<?>(null, null, 0), result);
//    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme