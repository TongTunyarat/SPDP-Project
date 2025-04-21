package com.example.project.service.ProjectManagement;

import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.entity.Instructor;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EditProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private InstructorRepository instructorRepository;

    @Transactional
    public List<String> updateProjectDetails(String projectId, ProjectDetailsDTO updatedDetails) {

        // --------- Update Project Details --------- //
        // ดึงข้อมูลโปรเจกต์จากฐานข้อมูล
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for ID: " + projectId));

        // ตรวจสอบว่าโปรเจกต์มีการแก้ไขข้อมูลหรือไม่
        boolean isUpdated = false;

        // ตรวจสอบการแก้ไขข้อมูลโปรเจกต์
        if (!project.getProjectTitle().equals(updatedDetails.getProjectTitle())) {
            project.setProjectTitle(updatedDetails.getProjectTitle());
            isUpdated = true;
        }
        if (!project.getProjectDescription().equals(updatedDetails.getProjectDescription())) {
            project.setProjectDescription(updatedDetails.getProjectDescription());
            isUpdated = true;
        }
        if (!project.getProgram().equals(updatedDetails.getProgram())) {
            project.setProgram(updatedDetails.getProgram());
            isUpdated = true;
        }
        if (!Objects.equals(project.getProjectCategory(), updatedDetails.getCategory())) {
            project.setProjectCategory(updatedDetails.getCategory());
            isUpdated = true;
        }
        if (!project.getSemester().equals(updatedDetails.getSemester())) {
            project.setSemester(updatedDetails.getSemester());
            isUpdated = true;
        }

        // --------- Update Instructor in Project --------- //

        List<String> errors = new ArrayList<>();

        Project inst = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        // 1) ตรวจสอบ duplicate instructor
        List<ProfessorRoleDTO> profs = Optional.ofNullable(updatedDetails.getProfessorList())
                .orElse(Collections.emptyList());
        Set<String> seen = new HashSet<>();
        for (ProfessorRoleDTO p : profs) {
            if (!seen.add(p.getProfessorId())) {
                errors.add("Duplicate instructor: " + p.getProfessorName() + " (ID=" + p.getProfessorId() + ")");
            }
        }

        // 2) เช็คจำนวน Committee ไม่เกิน 2
        long committeeCount = profs.stream()
                .filter(p -> "Committee".equals(p.getRole()))
                .count();
        if (committeeCount > 2) {
            errors.add("Role 'Committee' cannot exceed 2 members (got " + committeeCount + ")");
        }

        // 3) เช็ค Advisor ไม่เกิน 1 และอย่างน้อย 1
        long advCount = profs.stream()
                .filter(p -> "Advisor".equals(p.getRole()))
                .count();
        if (advCount > 1) {
            errors.add("Role 'Advisor' cannot exceed 1 member (got " + advCount + ")");
        }
        if (advCount < 1) {
            errors.add("Role 'Advisor' must have at least 1 member");
        }

        // 4) เช็คว่ามีนักศึกษาอย่างน้อย 1 คน
        int studentCount = Optional.ofNullable(updatedDetails.getStudentList())
                .orElse(Collections.emptyList()).size();
        if (studentCount < 1) {
            errors.add("At least one student is required");
        }

        // ถ้ามี error ให้รีเทิร์นเลย
        if (!errors.isEmpty()) {
            return errors;
        }

        // 2) อัปเดตฟิลด์ Project เรียงตามเดิม (title, description, program…)
        boolean dirty = false;
        if (!project.getProjectTitle().equals(updatedDetails.getProjectTitle())) {
            project.setProjectTitle(updatedDetails.getProjectTitle());
            dirty = true;
        }
        if (!project.getProjectDescription().equals(updatedDetails.getProjectDescription())) {
            project.setProjectDescription(updatedDetails.getProjectDescription());
            dirty = true;
        }
        if (!project.getProgram().equals(updatedDetails.getProgram())) {
            project.setProgram(updatedDetails.getProgram());
            dirty = true;
        }
        if(!project.getSemester().equals(updatedDetails.getSemester())) {
            project.setSemester(updatedDetails.getSemester());
            dirty = true;
        }
        if (dirty) projectRepository.save(project);


        // 3) ดึงรายการ ProjectInstructorRole เดิม (ordered ให้ตำแหน่ง stable)
        List<ProjectInstructorRole> existing = projectInstructorRoleRepository
                .findByProjectIdRole_ProjectIdOrderByAssignDateAsc(projectId);


        // 4) เตรียมข้อมูลใหม่จาก front‑end
        List<ProfessorRoleDTO> newList = Optional.ofNullable(updatedDetails.getProfessorList())
                .orElse(Collections.emptyList());

        // 5) ไล่ override record เดิม ตาม index
        int i = 0;
        for (; i < existing.size() && i < newList.size(); i++) {
            ProjectInstructorRole role = existing.get(i);
            ProfessorRoleDTO dto = newList.get(i);

            // เปลี่ยนผูก Instructor ใหม่
            Instructor instr = instructorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Instructor not found: " + dto.getProfessorId()));
            role.setInstructor(instr);

            // เปลี่ยน role type ถ้ามีการแก้ไข
            role.setRole(dto.getRole());

            projectInstructorRoleRepository.save(role);
        }

        // 6) ถ้ามีรายการใหม่เกินจำนวนเดิม → สร้างเพิ่ม
        for (; i < newList.size(); i++) {
            ProfessorRoleDTO dto = newList.get(i);
            Instructor instr = instructorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Instructor not found: " + dto.getProfessorId()));

            ProjectInstructorRole newRole = new ProjectInstructorRole();
            newRole.setProjectIdRole(project);
            newRole.setInstructor(instr);
            newRole.setRole(dto.getRole());
            newRole.setAssignDate(LocalDateTime.now());
            newRole.setInstructorId(generateNextInstructorId());
            projectInstructorRoleRepository.save(newRole);
        }



        // --------- Update Student in Project --------- //
        List<StudentProject> updatedStudents = Optional.ofNullable(updatedDetails.getStudentList())
                .orElse(Collections.emptyList())  // ถ้า studentList เป็น null ให้ใช้ List ว่าง
                .stream()
                .map(student -> {
                    // หาคู่ที่ตรงกันในฐานข้อมูล
                    StudentProject studentProject = project.getStudentProjects().stream()
                            .filter(sp -> sp.getStudent().getStudentId().equals(student.getStudentId()))
                            .findFirst()
                            .orElse(new StudentProject(project, student.getStudentId()));  // ถ้าไม่เจอให้สร้างใหม่

                    // กำหนดค่า status ใหม่
                    studentProject.setStatus(student.getStatus() != null ? student.getStatus() : "Active");

                    return studentProject;
                })
                .collect(Collectors.toList());

        // ตรวจสอบและบันทึกการเปลี่ยนแปลงนักศึกษา
        studentProjectRepository.saveAll(updatedStudents);

        // ถ้ามีการแก้ไขใดๆ ให้บันทึกโปรเจกต์
        if (isUpdated) {
            projectRepository.save(project);
        }
        return errors;
    }

    private String generateNextInstructorId() {
        // ค้นหาค่ารหัสล่าสุดที่มีอยู่ในฐานข้อมูล
        String latestId = projectInstructorRoleRepository.findLatestInstructorId();

        // ถ้าไม่พบรหัสล่าสุด (กรณีฐานข้อมูลยังว่างเปล่า)
        if (latestId == null) {
            return "INST001";  // เริ่มต้นที่ INST001
        }

        // เอารหัสล่าสุดออกมาจาก INSTxxxx และเพิ่ม 1
        String numericPart = latestId.substring(4);  // เอาส่วนเลขออกจาก INSTxxxx
        int nextId = Integer.parseInt(numericPart) + 1;  // เพิ่ม 1
        return String.format("INST%03d", nextId);  // ใช้ String.format เพื่อให้รหัสใหม่มีรูปแบบเหมือนเดิม เช่น INST002, INST003, ...
    }

    @Transactional
    public void deleteStudentFromProject(String projectId, String studentId) {
        // ค้นหาข้อมูล studentProject ที่ตรงกัน
        StudentProject studentProject = studentProjectRepository.findByProject_ProjectIdAndStudentStudentId(projectId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found for this project"));

        // ลบ student จาก project
        studentProjectRepository.delete(studentProject);
    }

    @Transactional
    public void deleteInstructorFromProject(String projectId, String instructorId) {

        ProjectInstructorRole projectInstructorRole = projectInstructorRoleRepository.findByProjectIdRole_ProjectIdAndInstructorId(projectId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found for this project"));

        projectInstructorRoleRepository.delete(projectInstructorRole);
    }
}

