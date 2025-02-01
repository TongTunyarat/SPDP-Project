package com.example.project.service;

import com.example.project.DTO.FilterResponseDTO;
import com.example.project.repository.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilterService {

    @Autowired
    private FilterRepository filterRepository;

    public List<FilterResponseDTO> getFilteredData(String program, String role ) {
        // ตรวจสอบค่า program, role  ที่ได้รับจากผู้ใช้
        return filterRepository.findFilteredData(

                // ถ้า program ไม่ใช่ "all", กรองตาม program, ถ้าเป็น "all" ส่งค่า null
                program != null && !program.equalsIgnoreCase("all") ? program : null,

                // ถ้า role ไม่ใช่ "all", กรองตาม role, ถ้าเป็น "all" ส่งค่า null
                role != null && !role.equalsIgnoreCase("all") ? role : null
        );
    }
}
