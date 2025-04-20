package com.example.project.service.ProjectManagement;

import com.example.project.entity.*;
import com.example.project.repository.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UploadFilesService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private InstructorRepository instructorRepository;



    // ฟังก์ชันในการตรวจสอบว่าเป็นชื่ออาจารย์ที่ถูกต้องหรือไม่
    private boolean isValidInstructor(String instructor) {
        return instructor != null && !instructor.trim().isEmpty() && !instructor.equals("Aj. XXXX");
    }

    // ฟังก์ชันในการตรวจสอบว่า Instructor มีบทบาทใน projectId เดียวกันหรือยัง
    private boolean isInstructorAlreadyAssignedToProject(String professorId, String projectId) {
        Optional<ProjectInstructorRole> existingRole = projectInstructorRoleRepository.findByProjectIdRole_ProjectIdAndInstructor_ProfessorId(projectId, professorId);
        return existingRole.isPresent();
    }

    // ฟังก์ชันในการบันทึก ProjectInstructorRole
    private void saveInstructorRole(Project project, Instructor instructor, String role) {
        ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
        projectInstructorRole.setProjectIdRole(project);
        projectInstructorRole.setRole(role);
        projectInstructorRole.setInstructor(instructor);
        projectInstructorRole.setAssignDate(LocalDateTime.now());
        projectInstructorRole.setInstructorId(generateNextInstructorId());  // สร้าง instructorId ใหม่

        // ตรวจสอบว่าอาจารย์ถูกเพิ่มไปแล้วหรือยัง
        if (!isInstructorAlreadyAssignedToProject(instructor.getProfessorId(), project.getProjectId())) {
            projectInstructorRoleRepository.save(projectInstructorRole);  // บันทึกข้อมูล
            System.out.println("Saved Instructor Role for: " + instructor + " with role: " + role);
        } else {
            System.out.println("Instructor " + instructor.getProfessorName() + " already assigned to project " + project.getProjectId());
        }
    }

    // -------------------- DELETE PROJECT -------------------- //

    @Transactional
    public void deleteProjectDetails(String projectId) {
        // ตรวจสอบว่าโปรเจกต์มีอยู่หรือไม่
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for ID: " + projectId));

//        // ลบข้อมูลอาจารย์ที่ปรึกษาที่เกี่ยวข้องกับโปรเจกต์นี้
//        List<ProjectInstructorRole> existingRoles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
//        if (existingRoles != null && !existingRoles.isEmpty()) {
//            projectInstructorRoleRepository.deleteAll(existingRoles);  // ลบอาจารย์ที่ปรึกษาทั้งหมดที่เกี่ยวข้องกับโปรเจกต์นี้
//        }

        // ลบข้อมูลนักศึกษาที่เกี่ยวข้องกับโปรเจกต์นี้
        List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(projectId);
        if (studentProjects != null && !studentProjects.isEmpty()) {
            studentProjectRepository.deleteAll(studentProjects);  // ลบข้อมูลนักศึกษาทั้งหมดที่เกี่ยวข้องกับโปรเจกต์นี้
        }

        // ลบโปรเจกต์จากฐานข้อมูล
        projectRepository.delete(project);
    }

    public void deleteAllProjects() {
        // ลบทุกโปรเจกต์ในฐานข้อมูล
        projectRepository.deleteAll();
    }


//    private void processExcel(MultipartFile file, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
//        try {
//            EasyExcel.read(file.getInputStream(), ExcelDataDTO.class, new ReadListener<ExcelDataDTO>() {
//                @Override
//                public void invoke(ExcelDataDTO data, AnalysisContext context) {
//                    System.out.println("Excel Data Read: " + data);
//
//                    // ตรวจสอบว่า studentId ไม่เป็นค่าว่าง
//                    if (data == null || data.getStudentId() == null || data.getStudentId().isEmpty()) {
//                        errorLogs.add("Missing Student ID: " + data);
//                        return;
//                    }
//
//                    // จัดเตรียมข้อมูลแถว (row) สำหรับ mapProjectToEntities
//                    String[] rowData = {
//                            data.getProjectId(), data.getProjectTitle(), data.getProjectDescription(),
//                            data.getAdvisor(), data.getCommittee(), data.getPosterCommittee(),
//                            data.getStudentId(), data.getStudentName(), data.getProgram(),
//                            data.getSection(), data.getTrack()
//                    };
//
//                    System.out.println("Row Data: " + Arrays.toString(rowData));
//                    // ส่งข้อมูลไปยังฟังก์ชัน mapProjectToEntities
//                    mapProjectToEntities(rowData, students, projects, studentProjects, errorLogs);
//                }
//
//                @Override
//                public void doAfterAllAnalysed(AnalysisContext context) {
//                    // สามารถใช้เพื่อทำการ cleanup หรือการประมวลผลหลังจากที่อ่านข้อมูลเสร็จ
//                }
//            }).sheet().doRead();
//        } catch (Exception e) {
//            System.out.println("Error processing Excel: " + e.getMessage());
//            errorLogs.add("Error processing Excel: " + e.getMessage());
//        }
//    }

    // ----------------- Function Upload Project Details (บันทึกลง DB) ----------------- //
//    public List<String> processProjectAndStudent(MultipartFile file) throws Exception {
//        List<String> warnings = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            int rowIndex = 0;
//            int currentYear = LocalDate.now().getYear();
//
//            Map<String, Integer> projectNumberCounters = new HashMap<>();
//            Map<String, Project> createdProjects = new HashMap<>();
//            String currentProjectId = null;
//
//            int studentIdCounter = Integer.parseInt(generateNextStudentPjId());
//
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//                if (rowIndex < 9) continue;
//                if (line.trim().isEmpty()) continue;
//
//                // ใช้ split ด้วย limit -1 เพื่อเก็บ trailing empty columns
//                String[] values = line.split(",", -1);
//                // ถ้าจำนวนคอลัมน์ยังน้อยกว่า 8 ให้เติมค่าว่างเข้าไป
//                if (values.length < 8) {
//                    String[] fullValues = new String[8];
//                    for (int i = 0; i < 8; i++) {
//                        if (i < values.length) {
//                            fullValues[i] = values[i].trim();
//                        } else {
//                            fullValues[i] = "";
//                        }
//                    }
//                    values = fullValues;
//                }
//
//                String fileProjectId = values[0].trim();
//                String projectTitle = values[1].trim();
//                String projectDescription = values[2].trim();
//                String projectCategory = values[3].trim();
//                String studentId = values[4].trim();
//                String studentName = values[5].trim();
//                String program = values[6].trim();
//                String advisor = values[7].trim();
//
//                // เมื่อเจอแถวที่มีข้อมูลโปรเจกต์ใหม่ (fileProjectId มีค่า)
//                if (!fileProjectId.isEmpty()) {
//                    Optional<Project> existingProjectOpt = projectRepository.findByProjectTitleAndProjectDescription(projectTitle, projectDescription);
//                    if (existingProjectOpt.isPresent()) {
//                        System.out.println("Project already exists for title '" + projectTitle
//                                + "' and description '" + projectDescription + "' at row " + rowIndex + ". Skipping project insertion.");
//                        warnings.add("Project '" + projectTitle + "' already exists.");
//                        currentProjectId = existingProjectOpt.get().getProjectId();
//                    } else {
//                        String key = program + "_" + currentYear;
//                        int projectNumberCounter = projectNumberCounters.containsKey(key)
//                                ? projectNumberCounters.get(key) + 1
//                                : generateNextProjectNumber(program, String.valueOf(currentYear));
//                        projectNumberCounters.put(key, projectNumberCounter);
//
//                        String newProjectId = program + " SP" + currentYear + "-" + String.format("%02d", projectNumberCounter);
//                        Project project = new Project();
//                        project.setProjectId(newProjectId);
//                        project.setProgram(program);
//                        project.setSemester(String.valueOf(currentYear));
//                        project.setProjectTitle(projectTitle);
//                        project.setProjectCategory(projectCategory);
//                        project.setProjectDescription(projectDescription);
//                        project.setRecordedOn(LocalDateTime.now());
//                        project.setEditedOn(LocalDateTime.now());
//
//                        projectRepository.save(project);
//                        createdProjects.put(newProjectId, project);
//                        currentProjectId = newProjectId;
//                    }
//                } else {
//                    // ไม่มี fileProjectId -> ใช้โปรเจกต์ล่าสุด
//                    if (currentProjectId == null) {
//                        String msg = "Skipping row " + rowIndex + " as no project info available.";
//                        System.out.println(msg);
//                        warnings.add("Row " + rowIndex + ": No project information available.");
//                        continue;
//                    }
//                }
//
//                // ตรวจสอบว่านักศึกษาสำหรับโปรเจกต์นี้มีอยู่แล้วหรือไม่
//                boolean studentProjectExists = studentProjectRepository.existsByProject_ProjectIdAndStudent_StudentId(currentProjectId, studentId);
//                if (studentProjectExists) {
//                    String msg = "StudentProject already exists for project " + currentProjectId + " and student " + studentId
//                            + " at row " + rowIndex + ". Skipping student-project insertion.";
//                    System.out.println(msg);
//                    warnings.add("Student '" + studentId + "' already exists in Project '" + currentProjectId + "'.");
//                    continue;
//                }
//
//                // ตรวจสอบข้อมูลนักศึกษา
//                Optional<Student> optStudent = studentRepository.findById(studentId);
//                if (optStudent.isPresent()) {
//                    Student student = optStudent.get();
//                    if (!student.getStudentName().equals(studentName)) {
//                        String msg = "Student info mismatch for ID " + studentId
//                                + ": Database Name = '" + student.getStudentName() + "', File Name = '" + studentName + "'. Skipping row " + rowIndex + ".";
//                        System.out.println(msg);
//                        warnings.add("Student information mismatch for ID '" + studentId + "'.");
//                        continue;
//                    }
//                } else {
//                    String msg = "Student not found for ID " + studentId + " at row " + rowIndex + ". Skipping row.";
//                    System.out.println(msg);
//                    warnings.add("Student '" + studentId + "' not found.");
//                    continue;
//                }
//
//                Project project = createdProjects.get(currentProjectId);
//                if (project == null) {
//                    Optional<Project> projectOpt = projectRepository.findById(currentProjectId);
//                    if (projectOpt.isPresent()) {
//                        project = projectOpt.get();
//                    } else {
//                        String msg = "Skipping row " + rowIndex + " as project not found for ID: " + currentProjectId;
//                        System.out.println(msg);
//                        warnings.add("Project not found for ID '" + currentProjectId + "'.");
//                        continue;
//                    }
//                }
//
//                StudentProject studentProject = new StudentProject();
//                studentProject.setStudent(optStudent.get());
//                studentProject.setProject(project);
//                studentProject.setStatus("Active");
//                studentProject.setStudentPjId("SP" + String.format("%03d", studentIdCounter++));
//                studentProjectRepository.save(studentProject);
//
//                if (!advisor.isEmpty()) {
//                    if (!isValidInstructor(advisor)) {
//                        String msg = "Advisor name '" + advisor + "' is invalid at row " + rowIndex + ". Skipping advisor assignment.";
//                        System.out.println(msg);
//                        warnings.add("Invalid advisor '" + advisor + "' for Project '" + currentProjectId + "'.");
//                    } else {
//                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(advisor);
//                        if (!optInstructor.isPresent()) {
//                            String msg = "Advisor '" + advisor + "' not found in Instructor entity at row " + rowIndex + ". Skipping advisor assignment.";
//                            System.out.println(msg);
//                            warnings.add("Advisor '" + advisor + "' not found for Project '" + currentProjectId + "'.");
//                        } else {
//                            Instructor instructor = optInstructor.get();
//                            if (instructor.getProfessorId() == null) {
//                                String newProfessorId = generateNextInstructorId();
//                                instructor.setProfessorId(newProfessorId);
//                                instructorRepository.save(instructor);
//                            }
//                            boolean instructorAlreadyAssigned = projectInstructorRoleRepository
//                                    .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorId(currentProjectId, instructor.getProfessorId());
//                            if (instructorAlreadyAssigned) {
//                                String msg = "Advisor '" + advisor + "' is already assigned to project " + currentProjectId
//                                        + " at row " + rowIndex + ". Skipping assignment.";
//                                System.out.println(msg);
//                                warnings.add("Advisor '" + advisor + "' already exists in Project '" + currentProjectId + "'.");
//                            } else {
//                                String newInstructorRoleId = generateNextInstructorId();
//                                ProjectInstructorRole roleRecord = new ProjectInstructorRole();
//                                roleRecord.setInstructorId(newInstructorRoleId);
//                                roleRecord.setAssignDate(LocalDateTime.now());
//                                roleRecord.setRole("Advisor");
//                                roleRecord.setProjectIdRole(project);
//                                roleRecord.setInstructor(instructor);
//                                projectInstructorRoleRepository.save(roleRecord);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//        return warnings;
//    }


    /**
     * 1) อ่านไฟล์ทั้งหมด สร้างโครงสร้าง in‑memory ของแต่ละโปรเจกต์
     * 2) ตรวจสอบใน DB (StudentProject, ProjectInstructorRole) ว่าตรงกับไฟล์หรือไม่
     * 3) คืน Map<projectId, List<discrepancyMessages>> (ว่าง = ผ่าน)
     */
    public Map<String, List<String>> validateProjectAndStudent(MultipartFile file) throws IOException {
        Map<String, FileProjectData> fileData = parseFile(file);
        Map<String, List<String>> errors = new LinkedHashMap<>();

        for (Map.Entry<String, FileProjectData> entry : fileData.entrySet()) {
            String projectId = entry.getKey();
            FileProjectData fp = entry.getValue();
            List<String> projectErrors = new ArrayList<>();

            if (projectRepository.existsById(projectId)) {
                // — ตรวจ studentProject
                List<StudentProject> dbSP = studentProjectRepository
                        .findByProject_ProjectId(projectId);
                Set<String> fileStudents = fp.students.stream()
                        .map(s -> s.id + "|" + s.name)
                        .collect(Collectors.toSet());
                Set<String> dbStudents = dbSP.stream()
                        .map(sp -> sp.getStudent().getStudentId() + "|" + sp.getStudent().getStudentName())
                        .collect(Collectors.toSet());

                for (String fs : fileStudents) {
                    if (!dbStudents.contains(fs)) {
                        projectErrors.add("Missing in DB StudentProject: " + fs);
                    }
                }
                for (String ds : dbStudents) {
                    if (!fileStudents.contains(ds)) {
                        projectErrors.add("Extra in DB StudentProject: " + ds);
                    }
                }

                // — ตรวจ ProjectInstructorRole
                List<ProjectInstructorRole> dbIR = projectInstructorRoleRepository
                        .findByProjectIdRole_ProjectId(projectId);
                Set<String> fileInstructors = fp.advisors.stream()
                        .filter(a -> !a.isEmpty())
                        .collect(Collectors.toSet());
                Set<String> dbInstructors = dbIR.stream()
                        .map(ir -> ir.getInstructor().getProfessorName())
                        .collect(Collectors.toSet());

                for (String fi : fileInstructors) {
                    if (!dbInstructors.contains(fi)) {
                        projectErrors.add("Missing in DB Advisor: " + fi);
                    }
                }
                for (String di : dbInstructors) {
                    if (!fileInstructors.contains(di)) {
                        projectErrors.add("Extra in DB Advisor: " + di);
                    }
                }
            }
            if (!projectErrors.isEmpty()) {
                errors.put(projectId, projectErrors);
            }
        }

        return errors;
    }

    /**
     * ถ้า validation ผ่าน (หรือ front-end ยืนยันมา) จึงเขียนลง DB
     */
    @Transactional
    public List<String> processProjectAndStudent(MultipartFile file) throws Exception {
        Map<String, FileProjectData> fileData = parseFile(file);
        List<String> warnings = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        Map<String, Integer> projectCounters = new HashMap<>();
        int studentPjCounter = Integer.parseInt(generateNextStudentPjId());

        for (FileProjectData fp : fileData.values()) {
            // 1) หาใน DB จาก title+description (หรือ projectId เดิม)
            Optional<Project> optProj = projectRepository
                    .findByProjectTitleAndProjectDescription(fp.title, fp.description);

            Project project;
            if (optProj.isPresent()) {
                // — ถ้ามีอยู่แล้ว: อัปเดตฟิลด์ที่เปลี่ยนแปลง
                project = optProj.get();
                project.setProgram(fp.program);
                project.setSemester(String.valueOf(currentYear));
                project.setProjectCategory(fp.category);
                project.setProjectDescription(fp.description);
                project.setEditedOn(LocalDateTime.now());
                projectRepository.save(project);

                // — ลบข้อมูลเก่า เพื่อเตรียมแทนที่ใหม่
                studentProjectRepository.deleteByProject_ProjectId(project.getProjectId());
                projectInstructorRoleRepository.deleteByProjectIdRole_ProjectId(project.getProjectId());
            } else {
                // — ถ้าไม่มี ให้สร้างใหม่ตามเดิม
                String key = fp.program + "_" + currentYear;
                int nextNum = projectCounters.getOrDefault(key,
                        generateNextProjectNumber(fp.program, String.valueOf(currentYear))
                );
                projectCounters.put(key, nextNum);

                String newId = fp.program + " SP" + currentYear
                        + "-" + String.format("%02d", nextNum);

                project = new Project();
                project.setProjectId(newId);
                project.setProgram(fp.program);
                project.setSemester(String.valueOf(currentYear));
                project.setProjectTitle(fp.title);
                project.setProjectCategory(fp.category);
                project.setProjectDescription(fp.description);
                project.setRecordedOn(LocalDateTime.now());
                project.setEditedOn(LocalDateTime.now());
                projectRepository.save(project);
            }

            // 2) สร้าง StudentProject ใหม่ (แทนที่ข้อมูลเก่า)
            for (FileStudent fs : fp.students) {
                if (fs.id == null || fs.id.isBlank()) continue;

                // เช็คว่ามีนักศึกษาในระบบ และชื่อตรงกัน
                Student student = studentRepository.findById(fs.id)
                        .orElseThrow(() -> new IllegalStateException("Student not found: " + fs.id));
                if (!student.getStudentName().equals(fs.name)) {
                    throw new IllegalStateException(
                            "Name mismatch for " + fs.id + ": DB=" +
                                    student.getStudentName() + ", File=" + fs.name);
                }

                // สร้าง record ใหม่
                StudentProject sp = new StudentProject();
                sp.setStudent(student);
                sp.setProject(project);
                sp.setStatus("Active");
                sp.setStudentPjId("SP" + String.format("%03d", studentPjCounter++));
                studentProjectRepository.save(sp);
            }

            // 3) สร้าง ProjectInstructorRole ใหม่ (แทนที่ข้อมูลเก่า)
            for (String advName : fp.advisors) {
                if (advName == null || advName.isBlank()) continue;
                if (!isValidInstructor(advName)) {
                    throw new IllegalStateException("Invalid advisor format: " + advName);
                }

                Instructor instr = instructorRepository
                        .findByProfessorName(advName)
                        .orElseThrow(() -> new IllegalStateException("Advisor not found: " + advName));

                if (instr.getProfessorId() == null) {
                    instr.setProfessorId(generateNextInstructorId());
                    instructorRepository.save(instr);
                }

                ProjectInstructorRole pir = new ProjectInstructorRole();
                pir.setInstructorId(generateNextInstructorId());
                pir.setAssignDate(LocalDateTime.now());
                pir.setRole("Advisor");
                pir.setProjectIdRole(project);
                pir.setInstructor(instr);
                projectInstructorRoleRepository.save(pir);
            }

            // 4) NEW: บล็อกสร้าง ProjectInstructorRole สำหรับ Co‑Advisor
            for (String coAdvName : fp.getCoAdvisors()) {
                if (coAdvName == null || coAdvName.isBlank()) continue;
                if (!isValidInstructor(coAdvName)) {
                    throw new IllegalStateException("Invalid co-advisor format: " + coAdvName);
                }

                Instructor instr = instructorRepository
                        .findByProfessorName(coAdvName)
                        .orElseThrow(() -> new IllegalStateException("Co-Advisor not found: " + coAdvName));

                if (instr.getProfessorId() == null) {
                    instr.setProfessorId(generateNextInstructorId());
                    instructorRepository.save(instr);
                }

                ProjectInstructorRole pir = new ProjectInstructorRole();
                pir.setInstructorId(generateNextInstructorId());
                pir.setAssignDate(LocalDateTime.now());
                pir.setRole("Co-Advisor");
                pir.setProjectIdRole(project);
                pir.setInstructor(instr);
                projectInstructorRoleRepository.save(pir);
            }

        }

        return warnings;
    }

    // —— HELPER TO PARSE FILE INTO IN‑MEMORY STRUCTURES ——

    private Map<String, FileProjectData> parseFile(MultipartFile file) throws IOException {
        Map<String, FileProjectData> map = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int row = 0;
            String currentProjId = null;

            // เริ่มอ่านจากบรรทัดที่ 6 (index 5)
            while ((line = br.readLine()) != null) {
                row++;

                // ข้ามบรรทัดก่อนบรรทัดที่ต้องการ (บรรทัดที่ 1 ถึง 5)
                if (row < 6 || line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);

                // pad to length 8
                if (cols.length < 8) {
                    String[] tmp = new String[8];
                    System.arraycopy(cols, 0, tmp, 0, cols.length);
                    for (int i = cols.length; i < 8; i++) tmp[i] = "";
                    cols = tmp;
                }

                String fid = cols[0].trim();
                String title = cols[1].trim();
                String desc = cols[2].trim();
                String category = cols[3].trim();
                String sid = cols[4].trim();
                String sname = cols[5].trim();
                String prog = cols[6].trim();
                String adv = cols[7].trim();
                String coadv  = cols[8].trim();

                if (!fid.isEmpty()) {
                    currentProjId = fid;
                    map.putIfAbsent(currentProjId, new FileProjectData(
                            currentProjId, title, desc, category, prog
                    ));
                }
                if (currentProjId == null) {
                    throw new IllegalStateException("Row " + row + ": no projectId");
                }

                FileProjectData fp = map.get(currentProjId);
                if (!sid.isEmpty()) {
                    fp.students.add(new FileStudent(sid, sname));
                }
                if (!adv.isEmpty() && !fp.advisors.contains(adv)) {
                    fp.advisors.add(adv);
                }

                if (!coadv.isBlank()) {
                    fp.getCoAdvisors().add(coadv);
                }
            }
        }
        return map;
    }

    // —— in‑memory DTO’s ——
    private static class FileProjectData {
        String projectId, title, description, category, program;
        List<FileStudent> students = new ArrayList<>();
        private List<String> advisors   = new ArrayList<>();
        private List<String> coAdvisors = new ArrayList<>();

        FileProjectData(String projectId, String title, String desc,
                        String cat, String prog) {
            this.projectId = projectId;
            this.title = title;
            this.description = desc;
            this.category = cat;
            this.program = prog;
        }

        public List<String> getCoAdvisors() { return coAdvisors; }
        public void setCoAdvisors(List<String> coAdvisors) { this.coAdvisors = coAdvisors; }

    }

    private static class FileStudent {
        String id, name;

        FileStudent(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public String generateNextProjectId(String program, String year) {
        int nextNum = generateNextProjectNumber(program, year);
        return String.format("%s SP%s-%02d", program, year, nextNum);
    }


    // ฟังก์ชันในการดึงรหัส Project ล่าสุด และเพิ่มขึ้นเป็นตัวเลขต่อไป (คืนค่าเป็น int)
    private int generateNextProjectNumber(String program, String year) {
        String latestProjectId = projectRepository.findLatestProjectIdByProgramAndYear(program, year);
        if (latestProjectId == null || latestProjectId.isEmpty()) {
            return 1;
        }
        String[] parts = latestProjectId.split("-");
        if (parts.length < 2) {
            return 1;
        }
        try {
            int latestNumber = Integer.parseInt(parts[1]);
            return latestNumber + 1;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    // ฟังก์ชันในการดึงรหัส student_pj_id ล่าสุดจากฐานข้อมูล และเพิ่มขึ้นในรูปแบบที่มีหลักนำ
    private String generateNextStudentPjId() {
        String latestId = studentProjectRepository.findLatestStudentPjId();
        if (latestId == null || latestId.isEmpty()) {
            return "01";
        }
        String numericPart = latestId.substring(2);
        int nextNumber = Integer.parseInt(numericPart) + 1;
        int width = numericPart.length();
        return String.format("%0" + width + "d", nextNumber);
    }

    // ฟังก์ชันในการสร้าง instructor_id ใหม่
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



    public List<String> processProjectCommittee(MultipartFile file) throws Exception {
        List<String> warnings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int rowIndex = 0;
            String currentProjectId = null;  // projectId สำหรับผูก Committee

            // อ่านไฟล์ CSV ทีละบรรทัด (สมมติ header อยู่ที่แถว 1-9)
            while ((line = br.readLine()) != null) {
                rowIndex++;
                if (rowIndex < 10) continue;  // ข้าม header
                if (line.trim().isEmpty()) continue;

                // ใช้ regex split เพื่อแยกเฉพาะ comma ที่ไม่ได้อยู่ภายใน double quotes
                String pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] values = line.split(pattern, -1);

                // หากจำนวนคอลัมน์ที่ได้ไม่ครบ 10 ให้เติมคอลัมน์ด้วยค่า ""
                if (values.length < 10) {
                    String[] fullValues = new String[10];
                    for (int i = 0; i < 10; i++) {
                        if (i < values.length) {
                            fullValues[i] = values[i].trim();
                        } else {
                            fullValues[i] = "";
                        }
                    }
                    values = fullValues;
                }

                // ดึงข้อมูลจาก CSV ตามตำแหน่งที่คาดหวัง
                String fileProjectId = values[0].trim();
                String committeeName = values[9].trim();

                // --------------------- จัดการ Project ---------------------
                if (!fileProjectId.isEmpty()) {
                    Optional<Project> projectOpt = projectRepository.findById(fileProjectId);
                    if (projectOpt.isPresent()) {
                        currentProjectId = projectOpt.get().getProjectId();
                    } else {
                        System.out.println("Project with ID '" + fileProjectId + "' not found at row " + rowIndex + ".");
                        warnings.add("Row " + rowIndex + ": Project with ID '" + fileProjectId + "' not found.");
                        continue;
                    }
                } else {
                    if (currentProjectId == null) {
                        System.out.println("Row " + rowIndex + " has no project ID provided (no previous project).");
//                        warnings.add("Row " + rowIndex + ": No project ID provided (no previous project).");
                        continue;
                    }
                    // ใช้ currentProjectId เดิม
                }

                // --------------------- จัดการ Committee ---------------------
                if (committeeName.isEmpty()) {
                    System.out.println("No committee data provided at row " + rowIndex + ".");
//                    warnings.add("Row " + rowIndex + ": No committee data provided.");
                    continue;
                } else {
                    if (!isValidInstructor(committeeName)) {
                        System.out.println("Committee name '" + committeeName + "' is invalid at row " + rowIndex + ". Skipping committee assignment.");
                        warnings.add("Row " + rowIndex + ": Invalid committee '" + committeeName + "' for Project '" + currentProjectId + "'.");
                        continue;
                    } else {
                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(committeeName);
                        if (!optInstructor.isPresent()) {
                            System.out.println("Committee '" + committeeName + "' not found in Instructor entity at row " + rowIndex + ". Skipping committee assignment.");
                            warnings.add("Row " + rowIndex + ": Committee '" + committeeName + "' not found for Project '" + currentProjectId + "'.");
                            continue;
                        }
                        Instructor instructor = optInstructor.get();

                        if (instructor.getProfessorId() == null) {
                            String newProfessorId = generateNextInstructorId();
                            instructor.setProfessorId(newProfessorId);
                            instructorRepository.save(instructor);
                        }

                        boolean committeeAlreadyAssigned = projectInstructorRoleRepository
                                .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(
                                        currentProjectId, instructor.getProfessorId(), "Committee");
                        if (committeeAlreadyAssigned) {
                            System.out.println("Committee '" + committeeName + "' is already assigned to project " + currentProjectId
                                    + " at row " + rowIndex + ". Skipping assignment.");
                            warnings.add("Row " + rowIndex + ": Committee '" + committeeName + "' already exists in Project '" + currentProjectId + "'.");
                        } else {
                            String newCommitteeRoleId = generateNextInstructorId();
                            ProjectInstructorRole roleRecord = new ProjectInstructorRole();
                            roleRecord.setInstructorId(newCommitteeRoleId);
                            roleRecord.setAssignDate(LocalDateTime.now());
                            roleRecord.setRole("Committee");

                            Optional<Project> projOpt = projectRepository.findById(currentProjectId);
                            if (!projOpt.isPresent()) {
                                System.out.println("Project not found for ID '" + currentProjectId + "' at row " + rowIndex + ".");
                                warnings.add("Row " + rowIndex + ": Project not found for ID '" + currentProjectId + "'.");
                                continue;
                            }
                            Project project = projOpt.get();
                            roleRecord.setProjectIdRole(project);
                            roleRecord.setInstructor(instructor);

                            projectInstructorRoleRepository.save(roleRecord);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
        return warnings;
    }


    public List<String> processProjectPosterCommittee(MultipartFile file) throws Exception {
        List<String> warnings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int rowIndex = 0;
            String currentProjectId = null;  // projectId สำหรับผูก posterCommittee

            // อ่านไฟล์ CSV ทีละบรรทัด (สมมติ header อยู่ที่แถว 1-9)
            while ((line = br.readLine()) != null) {
                rowIndex++;
                if (rowIndex < 10) continue;  // ข้าม header
                if (line.trim().isEmpty()) continue;

                // ใช้ regex split เพื่อแยกเฉพาะ comma ที่ไม่ได้อยู่ภายใน double quotes
                String pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] values = line.split(pattern, -1);

                // ต้องการขั้นต่ำ 11 คอลัมน์ (index 0..10) เพราะ posterCommittee อยู่คอลัมน์ที่ 10
                if (values.length < 11) {
                    String[] fullValues = new String[11];
                    for (int i = 0; i < 11; i++) {
                        if (i < values.length) {
                            fullValues[i] = values[i].trim();
                        } else {
                            fullValues[i] = "";
                        }
                    }
                    values = fullValues;
                }

                // ดึงข้อมูลจาก CSV ตามตำแหน่งที่คาดหวัง
                String fileProjectId = values[0].trim();
                String posterCommittee = values[10].trim(); // เปลี่ยนจากคอลัมน์ที่ 9 เป็นคอลัมน์ที่ 10

                // --------------------- จัดการ Project ---------------------
                if (!fileProjectId.isEmpty()) {
                    Optional<Project> projectOpt = projectRepository.findById(fileProjectId);
                    if (projectOpt.isPresent()) {
                        currentProjectId = projectOpt.get().getProjectId();
                    } else {
                        System.out.println("Project with ID '" + fileProjectId + "' not found at row " + rowIndex + ".");
                        warnings.add("Row " + rowIndex + ": Project with ID '" + fileProjectId + "' not found.");
                        continue;
                    }
                } else {
                    if (currentProjectId == null) {
                        System.out.println("Row " + rowIndex + " has no project ID provided (no previous project).");
                        continue;
                    }
                    // ใช้ currentProjectId เดิม
                }

                // --------------------- จัดการ posterCommittee ---------------------
                if (posterCommittee.isEmpty()) {
                    System.out.println("No posterCommittee data provided at row " + rowIndex + ".");
                    continue;
                } else {
                    if (!isValidInstructor(posterCommittee)) {
                        System.out.println("posterCommittee name '" + posterCommittee + "' is invalid at row " + rowIndex + ". Skipping posterCommittee assignment.");
                        warnings.add("Row " + rowIndex + ": Invalid posterCommittee '" + posterCommittee + "' for Project '" + currentProjectId + "'.");
                        continue;
                    } else {
                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(posterCommittee);
                        if (!optInstructor.isPresent()) {
                            System.out.println("posterCommittee '" + posterCommittee + "' not found in Instructor entity at row " + rowIndex + ". Skipping posterCommittee assignment.");
                            warnings.add("Row " + rowIndex + ": posterCommittee '" + posterCommittee + "' not found for Project '" + currentProjectId + "'.");
                            continue;
                        }
                        Instructor instructor = optInstructor.get();

                        if (instructor.getProfessorId() == null) {
                            String newProfessorId = generateNextInstructorId();
                            instructor.setProfessorId(newProfessorId);
                            instructorRepository.save(instructor);
                        }

                        boolean committeeAlreadyAssigned = projectInstructorRoleRepository
                                .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(
                                        currentProjectId, instructor.getProfessorId(), "posterCommittee");
                        if (committeeAlreadyAssigned) {
                            System.out.println("posterCommittee '" + posterCommittee + "' is already assigned to project " + currentProjectId
                                    + " at row " + rowIndex + ". Skipping assignment.");
                            warnings.add("Row " + rowIndex + ": posterCommittee '" + posterCommittee + "' already exists in Project '" + currentProjectId + "'.");
                        } else {
                            String newCommitteeRoleId = generateNextInstructorId();
                            ProjectInstructorRole roleRecord = new ProjectInstructorRole();
                            roleRecord.setInstructorId(newCommitteeRoleId);
                            roleRecord.setAssignDate(LocalDateTime.now());
                            roleRecord.setRole("Poster-Committee");

                            Optional<Project> projOpt = projectRepository.findById(currentProjectId);
                            if (!projOpt.isPresent()) {
                                System.out.println("Project not found for ID '" + currentProjectId + "' at row " + rowIndex + ".");
                                warnings.add("Row " + rowIndex + ": Project not found for ID '" + currentProjectId + "'.");
                                continue;
                            }
                            Project project = projOpt.get();
                            roleRecord.setProjectIdRole(project);
                            roleRecord.setInstructor(instructor);

                            projectInstructorRoleRepository.save(roleRecord);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
        return warnings;
    }


}