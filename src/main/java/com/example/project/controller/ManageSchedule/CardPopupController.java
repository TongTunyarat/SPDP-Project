package com.example.project.controller.ManageSchedule;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class CardPopupController {

    @GetMapping("/admin/editCardPopup")
    public ResponseEntity<String> getEditCardPopup() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("templates/ManageSchedule/CardPopupProposal.html");
            if (is == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: File not found");
            }
            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(html);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading file");
        }
    }
}
