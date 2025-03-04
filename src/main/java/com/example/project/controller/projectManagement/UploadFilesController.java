package com.example.project.controller.projectManagement;

import com.example.project.service.UploadFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class UploadFilesController {

    @Autowired
    private UploadFilesService uploadFilesService;

    @PostMapping("/uploadFiles")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = uploadFilesService.uploadFile(file);

        // ตรวจสอบว่าไฟล์ประมวลผลสำเร็จหรือไม่
        if (response.containsKey("message") && response.get("message").equals("File processed successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
