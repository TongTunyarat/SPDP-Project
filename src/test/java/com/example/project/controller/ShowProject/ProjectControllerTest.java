package com.example.project.controller.ShowProject;

import com.example.project.DTO.InstructorProjectDTO;
import com.example.project.DTO.ShowProject.*;
import com.example.project.DTO.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.*;

class ProjectControllerTest {
    @Mock
    ProjectService projectService;
    @Mock
    ProposalEvaluationRepository proposalEvaluationRepository;
    @Mock
    ProposalEvaluationService proposalEvaluationService;
    @Mock
    GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    @Mock
    ProposalGradeService proposalGradeService;
    @Mock
    DefenseEvaluationService defenseEvaluationService;
    @Mock
    DefenseEvaluationRepository defenseEvaluationRepository;
    @Mock
    DefenseGradeService defenseGradeService;
    @Mock
    GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;
    @Mock
    PosterEvaluationService posterEvaluationService;
    @Mock
    PosterEvaRepository posterEvaRepository;
    @Mock
    ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @InjectMocks
    ProjectController projectController;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    void testViewInstructorProjectPage() {
        String result = projectController.viewInstructorProjectPage();
        Assertions.assertEquals("DashboardInstructor", result);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        Authentication mockAuthentication = mock(Authentication.class);

        when(mockAuthentication.getName()).thenReturn("testUsername");

        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);

        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @Test
    void testGetInstructorData() {

        // Mock Student
        Student mockStudent = new Student();
        mockStudent.setStudentId("6487001");
        mockStudent.setStudentName("Oliver Thompson");

        // Mock StudentProject
        StudentProject mockStudentProject = new StudentProject();
        mockStudentProject.setStudent(mockStudent);
        mockStudentProject.setStudentPjId("SP001");
        mockStudentProject.setStatus("Active");

        // Mock Project
        Project mockProject = new Project();
        mockProject.setProjectId("DST SP2024-01");
        mockProject.setProjectTitle("Secure LLM Chatbot with Issue Tracking for Customer Service");
        mockProject.setProgram("CS"); // <<< set Program เพราะ DTO ต้องใช้
        mockProject.setStudentProjects(List.of(mockStudentProject));

        // Mock ProjectInstructorRole
        ProjectInstructorRole mockRole = new ProjectInstructorRole();
        mockRole.setRole("Advisor");
        mockRole.setProjectIdRole(mockProject);

        // Mock ProposalEvaluation
        ProposalEvaluation mockProposalEvaluation = Mockito.mock(ProposalEvaluation.class);

        Account mockAccount = Mockito.mock(Account.class);
        when(mockAccount.getUsername()).thenReturn("userTest");

        Instructor mockInstructor = Mockito.mock(Instructor.class);
        when(mockInstructor.getAccount()).thenReturn(mockAccount);

        ProjectInstructorRole mockProjectInstructorRole = Mockito.mock(ProjectInstructorRole.class);
        when(mockProjectInstructorRole.getInstructor()).thenReturn(mockInstructor);

        when(mockProposalEvaluation.getProjectInstructorRole()).thenReturn(mockProjectInstructorRole);
        when(mockProposalEvaluation.getStudent()).thenReturn(mockStudent);

        // Mock ProposalEvalScore
        Criteria mockCriteria = new Criteria();
        mockCriteria.setCriteriaId("C1");

        ProposalEvalScore mockScore = Mockito.mock(ProposalEvalScore.class);
        when(mockScore.getCriteria()).thenReturn(mockCriteria);
        when(mockScore.getScore()).thenReturn(BigDecimal.valueOf(100));

        when(mockProposalEvaluation.getProposalEvalScores()).thenReturn(List.of(mockScore));

        // Mock Repository & Service
        when(projectService.getInstructorProject()).thenReturn(List.of(mockRole));
        when(proposalEvaluationRepository.findByProject_ProjectId("DST SP2024-01")).thenReturn(List.of(mockProposalEvaluation));
        when(proposalEvaluationService.getProposalCriteria()).thenReturn(List.of(mockCriteria));

        // Call
        List<InstructorProjectDTONew> result = projectController.getInstructorData();

        // Expected
        StudentProjectDTONew expectedStudentProject = new StudentProjectDTONew(
                "6487001",
                "Oliver Thompson",
                "Active",
                true // เพราะมีคะแนนครบ
        );

        InstructorProjectDTONew expectedInstructorProject = new InstructorProjectDTONew(
                "CS",
                "DST SP2024-01",
                "Secure LLM Chatbot with Issue Tracking for Customer Service",
                "Advisor",
                List.of(expectedStudentProject),
                true // เพราะ student คนเดียวประเมินครบ
        );

        // Assert แบบละเอียด
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedInstructorProject.getProjectId(), result.get(0).getProjectId());
        Assertions.assertEquals(expectedInstructorProject.getProjectTitle(), result.get(0).getProjectTitle());
        Assertions.assertEquals(expectedInstructorProject.getRole(), result.get(0).getRole());
    }

    @Test
    void testGetInstructorPropGradeData() {
        when(projectService.getInstructorProject()).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));
        when(proposalEvaluationRepository.findByProject_ProjectId(anyString())).thenReturn(List.of(new ProposalEvaluation()));
        when(proposalEvaluationService.getProposalCriteria()).thenReturn(List.of(new Criteria()));
        when(gradingProposalEvaluationRepository.findByProject_ProjectId(anyString())).thenReturn(List.of(new GradingProposalEvaluation()));
        when(projectInstructorRoleRepository.findByProjectIdRole_ProjectId(anyString())).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));

        List<InstructorProjectPropGradeDTO> result = projectController.getInstructorPropGradeData();
        Assertions.assertEquals(List.of(new InstructorProjectPropGradeDTO("projectProgram", "projectId", "projectTitle", "role", List.of(new StudentProjectPropGradeDTO("studentId", "studentName", "status", true, new InstructorEvaluationStatusDTO(0L, 0L), "gradeResult")), true)), result);
    }

    @Test
    void testGetInstructorDefenseData() {
        when(projectService.getInstructorProject()).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));
        when(defenseEvaluationService.getDefenseCriteria()).thenReturn(List.of(new Criteria()));
        when(defenseEvaluationRepository.findByProjectId_ProjectId(anyString())).thenReturn(List.of(new DefenseEvaluation()));

        List<InstructorProjectDefenseDTO> result = projectController.getInstructorDefenseData();
        Assertions.assertEquals(List.of(new InstructorProjectDefenseDTO("projectProgram", "projectId", "projectTitle", "role", List.of(new StudentProjectDefenseDTO("studentId", "studentName", "status", true)), true)), result);
    }

    @Test
    void testGetInstructorDefenseGradeData() {
        when(projectService.getInstructorProject()).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));
        when(defenseEvaluationService.getDefenseCriteria()).thenReturn(List.of(new Criteria()));
        when(defenseEvaluationRepository.findByProjectId_ProjectId(anyString())).thenReturn(List.of(new DefenseEvaluation()));
        when(gradingDefenseEvaluationRepository.findByProjectId_ProjectId(anyString())).thenReturn(List.of(new GradingDefenseEvaluation()));
        when(posterEvaluationService.getPosterCriteria()).thenReturn(List.of(new Criteria()));
        when(posterEvaRepository.findByProjectIdPoster_ProjectId(anyString())).thenReturn(List.of(new PosterEvaluation()));
        when(projectInstructorRoleRepository.findByProjectIdRole_ProjectId(anyString())).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));

        List<InstructorProjectDefenseGradeDTO> result = projectController.getInstructorDefenseGradeData();
        Assertions.assertEquals(List.of(new InstructorProjectDefenseGradeDTO("projectProgram", "projectId", "projectTitle", "role", List.of(new StudentProjectDefenseGradeDTO("studentId", "studentName", "status", true, new InstructorEvaluationDefenseStatusDTO(0L, 0L), "gradeResult")), true)), result);
    }

    @Test
    void testGetInstructorPosterData() {
        when(projectService.getInstructorProject()).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));
        when(posterEvaluationService.getPosterCriteria()).thenReturn(List.of(new Criteria()));
        when(posterEvaRepository.findByProjectIdPoster_ProjectId(anyString())).thenReturn(List.of(new PosterEvaluation()));

        List<InstructorProjectPosterDTO> result = projectController.getInstructorPosterData();
        Assertions.assertEquals(List.of(new InstructorProjectPosterDTO("projectProgram", "projectId", "projectTitle", "role", List.of(new StudentProjectPosterDTO("studentId", "studentName", "status", true)), true)), result);
    }

    @Test
    void testGetAdvisorData() {
        when(projectService.getInstructorProject()).thenReturn(List.of(new ProjectInstructorRole(new Project("projectId", "program", null, "projectTitle", null, null, null, null, null, null))));

        List<InstructorProjectDTO> result = projectController.getAdvisorData();
        Assertions.assertEquals(List.of(new InstructorProjectDTO("projectProgram", "projectId", "projectTitle", "role", List.of(new StudentProjectDTO("studentId", "studentName", "status")))), result);
    }

    @Test
    void testGetProjectDetails() {
        when(projectService.getProjectDetails(anyString())).thenReturn(new Project("projectId", "program", "semester", "projectTitle", "projectCategory", "projectDescription", LocalDateTime.of(2025, Month.APRIL, 9, 17, 24, 58), LocalDateTime.of(2025, Month.APRIL, 9, 17, 24, 58), new Account(), new Account()));

        Project result = projectController.getProjectDetails("projectId");
        Assertions.assertEquals(new Project("projectId", "program", "semester", "projectTitle", "projectCategory", "projectDescription", LocalDateTime.of(2025, Month.APRIL, 9, 17, 24, 58), LocalDateTime.of(2025, Month.APRIL, 9, 17, 24, 58), new Account(), new Account()), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme