package com.example.project.controller.ProjectManagement;

import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.entity.Project;
import com.example.project.service.ProjectManagement.UploadFilesService;
import com.example.project.service.ProjectManagement.AddNewProjectService;
import com.example.project.service.ProjectService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class UploadFilesController {

    @Autowired
    private UploadFilesService uploadFilesService;
    @Autowired
    private AddNewProjectService AddNewProjectService;
    @Autowired
    private ProjectService projectService;

//    @PostMapping("/uploadFiles")
//    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
//        System.out.println("✅ File upload started: " + file.getOriginalFilename()); // ✅ Debug
//
//        Map<String, Object> response = uploadFilesService.uploadFile(file);
//
//        return response.containsKey("message") && response.get("message").equals("File processed successfully")
//                ? new ResponseEntity<>(response, HttpStatus.OK)
//                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }

//    @PostMapping("/uploadFiles")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            uploadFilesService.processCsvFile(file);
//            return ResponseEntity.ok("File uploaded and processed successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
//        }
//    }

    @PostMapping("/uploadFiles")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file,
                                                          @RequestParam("uploadType") String uploadType,
                                                          @RequestParam("fileType") String fileType) {
        try {
            // Process the file based on uploadType
            uploadFilesService.processCsvFile(file, uploadType); // ใช้ processCsvFile ตาม uploadType
            return ResponseEntity.ok(Map.of("message", "File processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "File processing failed", "errors", List.of(e.getMessage())));
        }
    }

}
