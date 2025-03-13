package com.example.project.service;

import com.example.project.DTO.Dashboard.EvaluationTrackingDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;
    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;
    @Autowired
    private PosterEvaRepository posterEvaRepository;

//    public EvaluationTrackingDTO getEvaluationTracking(String evaType) {
        // if evaType == Proposal
        // list instructor = ProjectInstructorRole.findall()
            //public class ProjectInstructorRole {
            //    private String instructorId;
            //    private LocalDateTime assignDate;
            //    private String role;
            //    private Project projectIdRole;
            //    private Instructor instructor;
            //    private List<DefenseEvaluation> defenseEvaluations;
            //    private List<PosterEvaluation> posterEvaluations;
            //    private List<ProposalEvaluation> proposalEvaluations;
        // list student = StudentProject.findall()
            // public class StudentProject {
            //      private String studentPjId;
            //      private Student student;
            //      private Project project;
            //      private String status;
        // list proposalEva = ProposalEvaluation.findall()
            //   public class ProposalEvaluation {
            //            private String proposalId;
            //            private String comment;
            //            private LocalDateTime recordedOn;
            //            private LocalDateTime editedOn;
            //            private BigDecimal totalScore;
            //            private Account account;
            //            private Account accountEdit;
            //            private ProjectInstructorRole projectInstructorRole;
            //            private Project project;
            //            private Student student;
            //            private List<ProposalEvalScore> proposalEvalScores;

        // ให้เช็คเงื่อนไขว่า ใน project นั้นมี student กี่คนแล้ว instructor ของ Project นั้นมีจำนวน proposalEva เท่าไหร่
        // แล้วเก็บเข้า EvaluationTrackingDTO โดยถ้า proposalEva ของ instructor มีจำนวนเท่ากับ student ใน project จะเก็บ status เป็น success
            // public class EvaluationTrackingDTO {
                // private List<ProjectInstructorRole> projectInstructorRole;
                // private List<InstructorEvaStatus> instructorEvaStatus;
            // public static class InstructorEvaStatus {
                // private String instructorId;
                // private String projectId;
                // private String projectEvaStatus;
//    }


//    public EvaluationTrackingDTO getEvaluationTracking(String evaType) {
//        if (!"Proposal".equalsIgnoreCase(evaType)) {
//            return null; // รองรับเฉพาะ Proposal เท่านั้น
//        }
//
//        List<ProjectInstructorRole> instructorRoles = projectInstructorRoleRepository.findAll();
//        List<StudentProject> studentProjects = studentProjectRepository.findAll();
//        List<ProposalEvaluation> proposalEvaluations = proposalEvaluationRepository.findAll();
//
//        Map<String, Long> studentCountByProject = studentProjects.stream()
//                .filter(sp -> "Active".equalsIgnoreCase(sp.getStatus())) // นับเฉพาะโครงการที่ Active
//                .collect(Collectors.groupingBy(sp -> sp.getProject().getProjectId(), Collectors.counting()));
//
//        Map<String, Long> proposalCountByInstructor = proposalEvaluations.stream()
//                .collect(Collectors.groupingBy(
//                        pe -> pe.getProjectInstructorRole().getInstructor().getProfessorId() + "_" + pe.getProject().getProjectId(),
//                        Collectors.counting()));
//
//        List<EvaluationTrackingDTO.InstructorEvaStatus> instructorEvaStatusList = new ArrayList<>();
//
//        for (ProjectInstructorRole role : instructorRoles) {
//            String instructorId = role.getInstructor().getProfessorId();
//            String projectId = role.getProjectIdRole().getProjectId();
//            long studentCount = studentCountByProject.getOrDefault(projectId, 0L);
//            long proposalEvaCount = proposalCountByInstructor.getOrDefault(instructorId + "_" + projectId, 0L);
//
//            String projectEvaStatus = (proposalEvaCount == studentCount) ? "success" : "pending";
//
//            instructorEvaStatusList.add(new EvaluationTrackingDTO.InstructorEvaStatus(instructorId, projectId, projectEvaStatus));
//        }
//
//        return new EvaluationTrackingDTO(instructorRoles, instructorEvaStatusList);
//    }
    public EvaluationTrackingDTO getEvaluationTracking(String evaType, String year) {
        List<ProjectInstructorRole> instructorRoles = projectInstructorRoleRepository.findByProjectIdRole_Semester(year);
        List<EvaluationTrackingDTO.InstructorEvaStatus> instructorEvaStatusList = new ArrayList<>();

        if ("Proposal".equalsIgnoreCase(evaType)) {
            return getProposalEvaluationTracking(instructorRoles);
        } else if ("Defense".equalsIgnoreCase(evaType)) {
            return getDefenseEvaluationTracking(instructorRoles);
        } else if ("Poster".equalsIgnoreCase(evaType)) {
            return getPosterEvaluationTracking(instructorRoles);
        }

        return null; // ถ้าไม่ใช่ทั้ง 3 ประเภทให้คืนค่า null
    }

    private EvaluationTrackingDTO getProposalEvaluationTracking(List<ProjectInstructorRole> instructorRoles) {
        // กรองเฉพาะ Instructor ที่มี role เป็น "Advisor" หรือ "Committee"
        List<ProjectInstructorRole> filteredRoles = instructorRoles.stream()
                .filter(role -> "Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole()))
                .collect(Collectors.toList());

        List<StudentProject> studentProjects = studentProjectRepository.findAll();
        List<ProposalEvaluation> proposalEvaluations = proposalEvaluationRepository.findAll();

        // นับจำนวน Student ที่ Active ในแต่ละ Project
        Map<String, Long> studentCountByProject = studentProjects.stream()
                .filter(sp -> "Active".equalsIgnoreCase(sp.getStatus()))
                .collect(Collectors.groupingBy(sp -> sp.getProject().getProjectId(), Collectors.counting()));

        // นับจำนวน Proposal Evaluation ของ Instructor ต่อ Project
        Map<String, Long> proposalCountByInstructor = proposalEvaluations.stream()
                .collect(Collectors.groupingBy(
                        pe -> pe.getProjectInstructorRole().getInstructor().getProfessorId() + "_" + pe.getProject().getProjectId(),
                        Collectors.counting()));

        List<EvaluationTrackingDTO.InstructorEvaStatus> instructorEvaStatusList = new ArrayList<>();

        for (ProjectInstructorRole role : filteredRoles) {
            String instructorId = role.getInstructor().getProfessorId();
            String projectId = role.getProjectIdRole().getProjectId();
            long studentCount = studentCountByProject.getOrDefault(projectId, 0L);
            long proposalEvaCount = proposalCountByInstructor.getOrDefault(instructorId + "_" + projectId, 0L);

            String projectEvaStatus = (proposalEvaCount == studentCount) ? "success" : "pending";
            instructorEvaStatusList.add(new EvaluationTrackingDTO.InstructorEvaStatus(instructorId, projectId, projectEvaStatus));
        }

        return new EvaluationTrackingDTO(filteredRoles, instructorEvaStatusList);
    }


    private EvaluationTrackingDTO getDefenseEvaluationTracking(List<ProjectInstructorRole> instructorRoles) {
        // กรองเฉพาะ Instructor ที่มี role เป็น "Advisor" หรือ "Committee"
        List<ProjectInstructorRole> filteredRoles = instructorRoles.stream()
                .filter(role -> "Advisor".equalsIgnoreCase(role.getRole()) || "Committee".equalsIgnoreCase(role.getRole()))
                .collect(Collectors.toList());

        List<StudentProject> studentProjects = studentProjectRepository.findAll();
        List<DefenseEvaluation> defenseEvaluations = defenseEvaluationRepository.findAll();

        // นับจำนวน Student ที่ Active ในแต่ละ Project
        Map<String, Long> studentCountByProject = studentProjects.stream()
                .filter(sp -> "Active".equalsIgnoreCase(sp.getStatus()))
                .collect(Collectors.groupingBy(sp -> sp.getProject().getProjectId(), Collectors.counting()));

        // นับจำนวน Defense Evaluation ของ Instructor ต่อ Project
        Map<String, Long> defenseCountByInstructor = defenseEvaluations.stream()
                .collect(Collectors.groupingBy(
                        de -> de.getDefenseInstructorId().getInstructor().getProfessorId() + "_" + de.getProjectId().getProjectId(),
                        Collectors.counting()));

        List<EvaluationTrackingDTO.InstructorEvaStatus> instructorEvaStatusList = new ArrayList<>();

        for (ProjectInstructorRole role : filteredRoles) {
            String instructorId = role.getInstructor().getProfessorId();
            String projectId = role.getProjectIdRole().getProjectId();
            long studentCount = studentCountByProject.getOrDefault(projectId, 0L);
            long defenseEvaCount = defenseCountByInstructor.getOrDefault(instructorId + "_" + projectId, 0L);

            String projectEvaStatus = (defenseEvaCount == studentCount) ? "success" : "pending";
            instructorEvaStatusList.add(new EvaluationTrackingDTO.InstructorEvaStatus(instructorId, projectId, projectEvaStatus));
        }

        return new EvaluationTrackingDTO(filteredRoles, instructorEvaStatusList);
    }


    private EvaluationTrackingDTO getPosterEvaluationTracking(List<ProjectInstructorRole> instructorRoles) {
        // กรองเฉพาะ Instructor ที่มี role เป็น "Committee" หรือ "Poster-Committee"
        List<ProjectInstructorRole> filteredRoles = instructorRoles.stream()
                .filter(role -> "Committee".equalsIgnoreCase(role.getRole()) || "Poster-Committee".equalsIgnoreCase(role.getRole()))
                .collect(Collectors.toList());

        List<PosterEvaluation> posterEvaluations = posterEvaRepository.findAll();

        // นับจำนวน Poster Evaluation ของ Instructor ต่อ Project
        Map<String, Long> posterCountByInstructor = posterEvaluations.stream()
                .collect(Collectors.groupingBy(
                        pe -> pe.getInstructorIdPoster().getInstructor().getProfessorId() + "_" + pe.getProjectIdPoster().getProjectId(),
                        Collectors.counting()));

        List<EvaluationTrackingDTO.InstructorEvaStatus> instructorEvaStatusList = new ArrayList<>();

        for (ProjectInstructorRole role : filteredRoles) {
            String instructorId = role.getInstructor().getProfessorId();
            String projectId = role.getProjectIdRole().getProjectId();
            long posterEvaCount = posterCountByInstructor.getOrDefault(instructorId + "_" + projectId, 0L);

            // Poster ไม่เช็คจำนวน Student ดังนั้นถ้า Instructor มี Poster Evaluation อย่างน้อย 1 ใบถือว่าสำเร็จ
            String projectEvaStatus = (posterEvaCount > 0) ? "success" : "pending";
            instructorEvaStatusList.add(new EvaluationTrackingDTO.InstructorEvaStatus(instructorId, projectId, projectEvaStatus));
        }

        return new EvaluationTrackingDTO(filteredRoles, instructorEvaStatusList);
    }



}
