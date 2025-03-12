package com.example.project.controller.ProjectManagement;

import com.example.project.service.ProjectManagement.UploadFilesService;
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
        System.out.println("✅ File upload started: " + file.getOriginalFilename()); // ✅ Debug

        Map<String, Object> response = uploadFilesService.uploadFile(file);

        return response.containsKey("message") && response.get("message").equals("File processed successfully")
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
