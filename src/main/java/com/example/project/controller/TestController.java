package com.example.project.controller;

import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private DefenseEvalScoreRepository defenseEvalScoreRepository;

    @Autowired
    private DefenseEvaluationRepository defenseEvaluationRepository;

    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;

    @Autowired
    private ScoringPeriodsRepository scoringPeriodsRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private ProposalSchedRepository proposalSchedRepository;

    @Autowired
    private DefenseSchedRepository defenseSchedRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PosterEvaRepository posterEvaRepository;

    @Autowired
    private PosterEvaScoreRepository posterEvaScoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;


//    @GetMapping("/")
//    public String index() {
//        return "Login";
//    }

    @GetMapping("/admins/all")
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @GetMapping("/instructor/all")
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @GetMapping("/accounts/all")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/criterias/all")
    public List<Criteria> getAllCriterias() { return criteriaRepository.findAll(); }

    @GetMapping("/defenseEvaScore/all")
    public List<DefenseEvalScore> getAllDefenseScore() { return defenseEvalScoreRepository.findAll(); }

    @GetMapping("/defenseEva/all")
    public List<DefenseEvaluation> getAllDefenseEva() { return defenseEvaluationRepository.findAll(); }

    @GetMapping("/defenseGrade/all")
    public List<GradingDefenseEvaluation> getAllDefenseGrade() { return gradingDefenseEvaluationRepository.findAll(); }

    @GetMapping("/period/all")
    public List<ScoringPeriods> getAllScorePeriod() { return scoringPeriodsRepository.findAll(); }

    @GetMapping("/project/all")
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/instructorRole/all")
    public List<ProjectInstructorRole> getAllProjectInstructorRole() { return projectInstructorRoleRepository.findAll(); }

    @GetMapping("/proposalSchedule")
    public List<ProposalSchedule> getAllProposalSchedule() {return proposalSchedRepository.findAll();}

    @GetMapping("/defenseSchedule")
    public List<DefenseSchedule> getAllDefenseSchedule() {return defenseSchedRepository.findAll();}

    @GetMapping("/room")
    public List<Room> getAllRooms() {return roomRepository.findAll();}

    @GetMapping("/posterEva")
    public List<PosterEvaluation> getAllPosterEva() {return posterEvaRepository.findAll();}

    @GetMapping("/posterEva/score")
    public List<PosterEvaluationScore> getAllPosterEvaScore() {return posterEvaScoreRepository.findAll();}

    @GetMapping("/student")
    public List<StudentProject> getAllStudent() {return studentProjectRepository.findAll();}
}
