package com.example.project.service;

import com.example.project.DTO.ScoringPeriodsRequest;
import com.example.project.entity.Account;
import com.example.project.entity.ScoringPeriods;
import com.example.project.repository.ScoringPeriodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ScoringPeriodsService {

    @Autowired
    private ScoringPeriodsRepository scoringPeriodsRepository;

    @Autowired
    private AccountService accountService;

    public void createOrUpdateScoringPeriod(ScoringPeriodsRequest request) {
        // เช็คว่า account ที่ส่งมาอยู่ในระบบ
        Account account = accountService.findByUsername(request.getRecordedBy());
        if (account == null) {
            throw new IllegalArgumentException("Invalid recordedBy username");
        }

        // เช็คว่า ScoringPeriod มี evaluationType นั้นๆอยู่แล้วรึเปล่า
        Optional<ScoringPeriods> existingScoringPeriod = scoringPeriodsRepository.findByEvaluationTypeAndYear(request.getEvaluationType(), request.getYear());

        ScoringPeriods scoringPeriods;

        if (existingScoringPeriod.isPresent()) {
            // ถ้ามี evaluationType นั้นแล้ว ให้ update row
            scoringPeriods = existingScoringPeriod.get();
            scoringPeriods.setStartDate(request.getStartDate());
            scoringPeriods.setEndDate(request.getEndDate());
            scoringPeriods.setRecordedByPeriod(account);
            scoringPeriods.setYear(request.getYear());

            scoringPeriodsRepository.save(scoringPeriods);
        } else {
            // ถ้ายังไม่มี evaluationType นั้นให้ create a new
            scoringPeriods = new ScoringPeriods();
            scoringPeriods.setEvaluationType(request.getEvaluationType());
            scoringPeriods.setStartDate(request.getStartDate());
            scoringPeriods.setEndDate(request.getEndDate());
            scoringPeriods.setRecordedByPeriod(account);
            scoringPeriods.setYear(request.getYear());

            scoringPeriodsRepository.save(scoringPeriods);
        }
    }

    public Map<String, String> getFormattedDatesByEvaluationType(String evaluationType, String year) {
        // ดึงวันที่จากฐานข้อมูล
//        LocalDate startDate = scoringPeriodsRepository.findStartDateByEvaluationType(evaluationType);
//        LocalDate endDate = scoringPeriodsRepository.findEndDateByEvaluationType(evaluationType);

        System.out.println("evaluationType: " + evaluationType);
        System.out.println("year: " + year);

        LocalDate startDate = scoringPeriodsRepository.findStartDateByEvaluationTypeAndYear(evaluationType, year);
        LocalDate endDate = scoringPeriodsRepository.findEndDateByEvaluationTypeAndYear(evaluationType, year);

        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        // จัดรูปแบบวันที่
        Map<String, String> result = new HashMap<>();
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            result.put("startDate", startDate.format(formatter));
            result.put("endDate", endDate.format(formatter));
        }
        return result;
    }

}
