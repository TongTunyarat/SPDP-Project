package com.example.project.controller;

import com.example.project.DTO.FilterResponseDTO;
import com.example.project.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filter")
public class FilterController {

    @Autowired
    private FilterService filterService;

    // รับข้อมูลจากผู้ใช้และทำการกรองข้อมูล
    @GetMapping
    public ResponseEntity<List<FilterResponseDTO>> filterData(
            @RequestParam(required = false) String program,
            @RequestParam(required = false) String role
    ) {
        try {
            // เรียกใช้ Service เพื่อดึงข้อมูลที่กรองแล้ว
            List<FilterResponseDTO> response = filterService.getFilteredData(program, role);

            // ส่งข้อมูลที่ได้จาก Service กลับไปยัง Frontend
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // หากเกิดข้อผิดพลาด, ส่งกลับเป็น error
            return ResponseEntity.status(500).body(null);
        }
    }
}
