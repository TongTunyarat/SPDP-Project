package com.example.project.controller;

import com.example.project.DTO.ScoringPeriodsRequest;
import com.example.project.service.ScoringPeriodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/scoring-periods")
public class ScoringPeriodsController {

    @Autowired
    private ScoringPeriodsService scoringPeriodsService;


    // บันทึกข้อมูล Scoring Period
    @PostMapping
    public ResponseEntity<String> createScoringPeriod(@RequestBody ScoringPeriodsRequest request) {
        try {
            System.out.println("request");
            System.out.println(request);

            scoringPeriodsService.createOrUpdateScoringPeriod(request);
            return ResponseEntity.ok("Scoring Period saved successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ดึงข้อมูลวันที่จากฐานข้อมูล
    @GetMapping("/get-dates")
    public ResponseEntity<Map<String, String>> getDates(
            @RequestParam String evaluationType,
            @RequestParam String year
    ) {
        try {
            Map<String, String> dates = scoringPeriodsService.getFormattedDatesByEvaluationType(evaluationType,year);
            if (dates.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

}


