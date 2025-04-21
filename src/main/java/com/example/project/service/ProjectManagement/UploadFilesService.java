package com.example.project.service.ProjectManagement;

import com.example.project.entity.*;
import com.example.project.repository.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        // 0) ตรวจสอบค่าว่างของแต่ละโปรเจกต์ในไฟล์
        for (FileProjectData fp : fileData.values()) {
            String no = fp.no;
            if (fp.title == null || fp.title.isBlank()) {
                warnings.add("Project No " + no + ": projectTitle ห้ามเป็นค่าว่าง");
            }
            if (fp.description == null || fp.description.isBlank()) {
                warnings.add("Project No " + no + ": projectDescription ห้ามเป็นค่าว่าง");
            }
            if (fp.category == null || fp.category.isBlank()) {
                warnings.add("Project No " + no + ": projectCategory ห้ามเป็นค่าว่าง");
            }
            if (fp.program == null || fp.program.isBlank()) {
                warnings.add("Project No " + no + ": program ห้ามเป็นค่าว่าง");
            }
            // advisor ต้องมีอย่างน้อย 1
            if (fp.advisors == null || fp.advisors.isEmpty()) {
                warnings.add("Project No " + no + ": ต้องระบุ advisor อย่างน้อย 1 คน");
            }
            // นักศึกษาต้องมีอย่างน้อย 1 record
            if (fp.students == null || fp.students.isEmpty()) {
                warnings.add("Project No " + no + ": ต้องมีนักศึกษาอย่างน้อย 1 คน");
            } else {
                // เช็คแต่ละนักศึกษา
                for (FileStudent fs : fp.students) {
                    if (fs.id == null || fs.id.isBlank()) {
                        warnings.add("Project No " + no + ": studentId ห้ามเป็นค่าว่าง");
                    }
                    if (fs.name == null || fs.name.isBlank()) {
                        warnings.add("Project No " + no + ": studentName ห้ามเป็นค่าว่าง");
                    }
                }
            }
        }
        // ถ้าเจอ warning ใด ๆ ให้หยุด และคืนไปเลย
        if (!warnings.isEmpty()) {
            return warnings;
        }

        // 1) สแกนหา Student ID ที่ซ้ำกับฐานข้อมูล
        for (FileProjectData fp : fileData.values()) {
            for (FileStudent fs : fp.students) {
                if (studentProjectRepository.existsByStudent_StudentId(fs.id)) {
                    warnings.add("Student ID " + fs.id + " already exists");
                }
            }
        }
        if (!warnings.isEmpty()) {
            return warnings;
        }

        // 2) ถ้าไม่มีค่าว่างและไม่ซ้ำแล้ว ก็เริ่มอัพลง DB ตามเดิม
        int year = LocalDate.now().getYear();
        Map<String, Integer> counters = new HashMap<>();
        int studentCounter = Integer.parseInt(generateNextStudentPjId());

        for (FileProjectData fp : fileData.values()) {
            String key = fp.program + "_" + year;
            int seq = counters.getOrDefault(key, 0) + 1;
            counters.put(key, seq);
            String projId = fp.program + " SP" + year + "-" + String.format("%02d", seq);

            if (projectRepository.existsById(projId)) {
                studentProjectRepository.deleteByProject_ProjectId(projId);
                projectInstructorRoleRepository.deleteByProjectIdRole_ProjectId(projId);
            }

            Project project = projectRepository.findById(projId)
                    .orElseGet(() -> {
                        Project p = new Project();
                        p.setProjectId(projId);
                        p.setRecordedOn(LocalDateTime.now());
                        return p;
                    });
            project.setProgram(fp.program);
            project.setSemester(String.valueOf(year));
            project.setProjectTitle(fp.title);
            project.setProjectCategory(fp.category);
            project.setProjectDescription(fp.description);
            project.setEditedOn(LocalDateTime.now());
            projectRepository.save(project);

            for (FileStudent fs : fp.students) {
                Student stu = studentRepository.findById(fs.id)
                        .orElseThrow(() -> new IllegalStateException("Student not found: " + fs.id));
                if (!stu.getStudentName().equals(fs.name)) {
                    throw new IllegalStateException(
                            "Name mismatch for " + fs.id +
                                    ": DB=" + stu.getStudentName() + ", File=" + fs.name);
                }
                StudentProject sp = new StudentProject();
                sp.setStudent(stu);
                sp.setProject(project);
                sp.setStatus("Active");
                sp.setStudentPjId("SP" + String.format("%03d", studentCounter++));
                studentProjectRepository.save(sp);
            }

            createRoles(fp.advisors, "Advisor", project);
            createRoles(fp.coAdvisors, "Co-Advisor", project);
        }

        return warnings;
    }

    private void createRoles(List<String> names, String role, Project project) {
        if (names == null) return;
        for (String profName : names) {
            if (profName.isBlank()) continue;
            if (!isValidInstructor(profName)) {
                throw new IllegalStateException("Invalid " + role + ": " + profName);
            }
            Instructor instr = instructorRepository.findByProfessorName(profName)
                    .orElseThrow(() -> new IllegalStateException(role + " not found: " + profName));
            if (instr.getProfessorId() == null) {
                instr.setProfessorId(generateNextInstructorId());
                instructorRepository.save(instr);
            }
            ProjectInstructorRole pir = new ProjectInstructorRole();
            pir.setInstructorId(generateNextInstructorId());
            pir.setAssignDate(LocalDateTime.now());
            pir.setRole(role);
            pir.setProjectIdRole(project);
            pir.setInstructor(instr);
            projectInstructorRoleRepository.save(pir);
        }
    }


    // —— HELPER TO PARSE FILE INTO IN‑MEMORY STRUCTURES ——
    private Map<String, FileProjectData> parseFile(MultipartFile file) throws IOException {
        // อ่านไฟล์เป็น String
        String csv = new String(file.getBytes(), StandardCharsets.UTF_8);

        // เตรียม CSVParser:
        // - DEFAULT format ให้ใช้ comma เป็น delimiter
        // - withQuote('"') รองรับ double quotes
        // - withEscape('\\') รองรับ escaping
        // - withIgnoreSurroundingSpaces() ตัด space รอบๆ
        CSVFormat format = CSVFormat.DEFAULT
                .withQuote('"')
                .withEscape('\\')
                .withIgnoreSurroundingSpaces();

        Map<String, FileProjectData> map = new LinkedHashMap<>();
        String currentNo = null;

        try (
                Reader reader = new StringReader(csv);
                CSVParser parser = new CSVParser(reader, format)
        ) {
            // อ่านทั้งหมดเป็น List<CSVRecord>
            List<CSVRecord> records = parser.getRecords();

            // เริ่มอ่านจากบรรทัดที่ 6 (index=5)
            for (int i = 5; i < records.size(); i++) {
                CSVRecord rec = records.get(i);

                // ถ้าแถวว่างข้าม
                if (rec.size() == 0) continue;

                // pad ให้มี 9 columns (กรณี missing)
                int needed = 9;
                List<String> cells = new ArrayList<>();
                for (int j = 0; j < needed; j++) {
                    cells.add(j < rec.size() ? rec.get(j).trim() : "");
                }

                String no = cells.get(0);
                String title = cells.get(1);
                String desc = cells.get(2);
                String category = cells.get(3);
                String studentId = cells.get(4);
                String studentName = cells.get(5);
                String program = cells.get(6);
                String advisor = cells.get(7);
                String coAdvisor = cells.get(8);

                // ถ้ามี No ใหม่ ให้สร้าง DTO ใหม่
                if (!no.isEmpty()) {
                    currentNo = no;
                    map.computeIfAbsent(no, k ->
                            new FileProjectData(no, title, desc, category, program)
                    );
                }
                if (currentNo == null) {
                    throw new IllegalStateException("Row " + (i + 1) + ": missing No.");
                }

                FileProjectData fp = map.get(currentNo);
                // เติม student ถ้ามี
                if (!studentId.isEmpty() && !studentName.isEmpty()) {
                    fp.students.add(new FileStudent(studentId, studentName));
                }
                // เติม advisor (ไม่ซ้ำ)
                if (!advisor.isEmpty() && !fp.advisors.contains(advisor)) {
                    fp.advisors.add(advisor);
                }
                // เติม co‑advisor (ไม่ซ้ำ)
                if (!coAdvisor.isEmpty() && !fp.coAdvisors.contains(coAdvisor)) {
                    fp.coAdvisors.add(coAdvisor);
                }
            }
        }

        return map;
    }


    // —— in‑memory DTO’s ——
    private static class FileProjectData {
        String no, title, description, category, program;
        List<FileStudent> students = new ArrayList<>();
        List<String> advisors = new ArrayList<>();
        List<String> coAdvisors = new ArrayList<>();

        FileProjectData(String no, String t, String d, String c, String p) {
            this.no = no;
            this.title = t;
            this.description = d;
            this.category = c;
            this.program = p;
        }
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


    @Transactional
    public List<String> processProjectCommittee(MultipartFile file) throws Exception {
        List<String> warnings = new ArrayList<>();

        // เตรียม parser ให้รองรับ comma/newline ใน quotes อัตโนมัติ
        CSVFormat format = CSVFormat.DEFAULT
                .withQuote('"')
                .withEscape('\\')
                .withIgnoreEmptyLines()
                .withTrim();

        try (Reader in = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(in, format)) {

            int rowIndex = 0;
            String currentProjectId = null;

            for (CSVRecord rec : parser) {
                rowIndex++;
                // ข้าม header 9 แถวแรก
                if (rowIndex <= 9) continue;

                // แปลง record เป็น List<String> และ trim
                List<String> values = StreamSupport.stream(rec.spliterator(), false)
                        .map(String::trim)
                        .collect(Collectors.toList());

                // เติมให้ครบ 10 คอลัมน์ (0..9)
                while (values.size() < 10) values.add("");

                String fileProjectId = values.get(0);
                String committeeName = values.get(9);

                // 1) หา Project ที่ตรงกับ ID
                if (!fileProjectId.isEmpty()) {
                    Optional<Project> projOpt = projectRepository.findById(fileProjectId);
                    if (projOpt.isPresent()) {
                        currentProjectId = projOpt.get().getProjectId();
                    } else {
                        continue;
                    }
                } else if (currentProjectId == null) {
                    continue;
                }
                // 2) เช็คชื่อ committee
                if (committeeName.isEmpty()) {
                    // ข้ามถ้าไม่มีชื่อ
                    continue;
                }
                // ลบ single‑quote รอบๆ ออก (ถ้ามี)
                committeeName = committeeName.replaceAll("^'+|'+$", "");

                // เช็คว่ามี instructor ชื่อนี้หรือไม่
                Optional<Instructor> instOpt = instructorRepository.findByProfessorName(committeeName);
                if (instOpt.isEmpty()) {
                    continue;
                }
                Instructor instructor = instOpt.get();
                // สร้าง professorId ใหม่ถ้ายังไม่มี
                if (instructor.getProfessorId() == null) {
                    String newId = generateNextInstructorId();
                    instructor.setProfessorId(newId);
                    instructorRepository.save(instructor);
                }

                // 3) ป้องกันซ้ำ
                boolean exists = projectInstructorRoleRepository
                        .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(
                                currentProjectId, instructor.getProfessorId(), "Committee");
                if (exists) {
                    continue;
                }

                // 4) สร้างแถวใหม่
                Project project = projectRepository.findById(currentProjectId).get();
                ProjectInstructorRole pir = new ProjectInstructorRole();
                pir.setInstructorId(generateNextInstructorId());
                pir.setAssignDate(LocalDateTime.now());
                pir.setRole("Committee");
                pir.setProjectIdRole(project);
                pir.setInstructor(instructor);
                projectInstructorRoleRepository.save(pir);
            }
        } catch (IOException e) {
            throw new Exception("Error อ่าน CSV: " + e.getMessage(), e);
        }
        return warnings;
    }

    @Transactional
    public List<String> processProjectPosterCommittee(MultipartFile file) throws Exception {
        List<String> warnings = new ArrayList<>();

        // กำหนด CSVFormat ให้รองรับ "…", comma, newline, trim
        CSVFormat format = CSVFormat.DEFAULT
                .withQuote('"')
                .withEscape('\\')
                .withIgnoreEmptyLines()
                .withTrim();

        try (
                Reader in = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(in, format)
        ) {
            int rowIndex = 0;
            String currentProjectId = null;

            for (CSVRecord rec : parser) {
                rowIndex++;
                // ข้าม header บรรทัด 1–9
                if (rowIndex <= 9) continue;

                // เก็บค่าทุกคอลัมน์ (trim แล้ว) ลง list
                List<String> vals = StreamSupport.stream(rec.spliterator(), false)
                        .map(String::trim)
                        .collect(Collectors.toList());
                // เติมให้มีอย่างน้อย 11 คอลัมน์
                while (vals.size() < 11) vals.add("");

                String fileProjectId = vals.get(0);   // โปรเจกต์ไอดี
                String posterCommittee = vals.get(10); // ค่า Poster‑Committee ที่คอลัมน์ 10

                // 1) หา projectId ใหม่
                if (!fileProjectId.isEmpty()) {
                    Optional<Project> pOpt = projectRepository.findById(fileProjectId);
                    if (pOpt.isPresent()) {
                        currentProjectId = pOpt.get().getProjectId();
                    } else {
                        warnings.add("Row " + rowIndex +
                                ": Project ID '" + fileProjectId + "' ไม่พบ");
                        continue;
                    }
                } else if (currentProjectId == null) {
                    // ถ้าไม่เจอ id ในแถวแรก ก็ข้าม
//                    warnings.add("Row " + rowIndex + ": ไม่มี Project ID ให้ใช้");
                    continue;
                }

                // 2) ถ้าไม่มีชื่อให้ข้าม
                if (posterCommittee.isEmpty()) {
                    continue;
                }
                // ลบ single‑quote รอบๆ ออก (ถ้ามี)
                posterCommittee = posterCommittee.replaceAll("^'+|'+$", "");

                // 3) ตรวจสอบชื่ออาจารย์
                Optional<Instructor> instOpt =
                        instructorRepository.findByProfessorName(posterCommittee);
                if (instOpt.isEmpty()) {
                    warnings.add("Row " + rowIndex +
                            ": ชื่อ Poster‑Committee '" + posterCommittee + "' ไม่พบ");
                    continue;
                }
                Instructor instructor = instOpt.get();
                if (instructor.getProfessorId() == null) {
                    String newId = generateNextInstructorId();
                    instructor.setProfessorId(newId);
                    instructorRepository.save(instructor);
                }

                // 4) ป้องกันซ้ำ
                boolean exists = projectInstructorRoleRepository
                        .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(
                                currentProjectId, instructor.getProfessorId(), "Poster-Committee");
                if (exists) {
                    warnings.add("Row " + rowIndex +
                            ": Poster‑Committee '" + posterCommittee +
                            "' ถูกเพิ่มแล้วในโปรเจกต์ " + currentProjectId);
                    continue;
                }

                // 5) สร้าง record ใหม่
                Project project = projectRepository.findById(currentProjectId).get();
                ProjectInstructorRole pir = new ProjectInstructorRole();
                pir.setInstructorId(generateNextInstructorId());
                pir.setAssignDate(LocalDateTime.now());
                pir.setRole("Poster-Committee");
                pir.setProjectIdRole(project);
                pir.setInstructor(instructor);
                projectInstructorRoleRepository.save(pir);
            }
        } catch (IOException e) {
            throw new Exception("Error อ่านไฟล์ CSV: " + e.getMessage(), e);
        }

        return warnings;
    }


    // -------------------- DELETE PROJECT -------------------- //

    @Transactional
    public void deleteProjectDetails(String projectId) {
        // ตรวจสอบว่าโปรเจกต์มีอยู่หรือไม่
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for ID: " + projectId));


        // ลบข้อมูลนักศึกษาที่เกี่ยวข้องกับโปรเจกต์นี้
        List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(projectId);
        if (studentProjects != null && !studentProjects.isEmpty()) {
            studentProjectRepository.deleteAll(studentProjects);  // ลบข้อมูลนักศึกษาทั้งหมดที่เกี่ยวข้องกับโปรเจกต์นี้
        }

        // ลบโปรเจกต์จากฐานข้อมูล
        projectRepository.delete(project);
    }


    private final DefenseEvaluationRepository defenseRepo;
    private final ProjectInstructorRoleRepository roleRepo;
    private final StudentProjectRepository stuRepo;
    private final ProjectRepository projRepo;
    private final PosterEvaRepository posterRepo;
    private final ProposalEvaluationRepository proposalRepo;
    private final GradingDefenseEvaluationRepository gradeDefenseRepo;
    private final GradingProposalEvaluationRepository gradePropRepo;
    private final ProposalEvalScoreRepository proposalEvalScore;
    private final DefenseEvalScoreRepository defenseEvalScore;
    private final PosterEvaScoreRepository posterEvalScore;
    private final DefenseSchedRepository defenseSched;
    private final ProposalSchedRepository proposalSched;

    public UploadFilesService(
            DefenseEvaluationRepository defenseRepo,
            ProjectInstructorRoleRepository roleRepo,
            StudentProjectRepository stuRepo,
            ProjectRepository projRepo,
            PosterEvaRepository posterRepo,
            ProposalEvaluationRepository proposalRepo,
            GradingProposalEvaluationRepository gradePropRepo,
            GradingDefenseEvaluationRepository gradeDefenseRepo,
            ProposalEvalScoreRepository proposalEvalScore,
            DefenseEvalScoreRepository defenseEvalScore,
            PosterEvaScoreRepository posterEvalScore,
            DefenseSchedRepository defenseSched,
            ProposalSchedRepository proposalSched
    ) {
        this.defenseRepo = defenseRepo;
        this.roleRepo = roleRepo;
        this.stuRepo = stuRepo;
        this.projRepo = projRepo;
        this.posterRepo = posterRepo;
        this.proposalRepo = proposalRepo;
        this.gradeDefenseRepo = gradeDefenseRepo;
        this.gradePropRepo = gradePropRepo;
        this.proposalEvalScore = proposalEvalScore;
        this.defenseEvalScore = defenseEvalScore;
        this.posterEvalScore = posterEvalScore;
        this.defenseSched = defenseSched;
        this.proposalSched = proposalSched;
    }

    @Transactional
    public void deleteAllProjects() {
        // 1) bulk‑delete ข้อมูลใน child ก่อน
        proposalEvalScore.deleteAllInBatch();
        defenseEvalScore.deleteAllInBatch();
        posterEvalScore.deleteAllInBatch();

        proposalRepo.deleteAllInBatch();
        posterRepo.deleteAllInBatch();
        defenseRepo.deleteAllInBatch();

        gradePropRepo.deleteAllInBatch();
        gradeDefenseRepo.deleteAllInBatch();

        defenseSched.deleteAllInBatch();
        proposalSched.deleteAllInBatch();

        // 2) bulk‑delete project‑instructor‑role
        roleRepo.deleteAllInBatch();

        // 3) bulk‑delete student‑project
        stuRepo.deleteAllInBatch();

        // 4) bulk‑delete project
        projRepo.deleteAllInBatch();
    }


}