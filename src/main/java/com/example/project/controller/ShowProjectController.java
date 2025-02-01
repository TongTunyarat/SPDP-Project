package com.example.project.controller;

import com.example.project.entity.Project;
import com.example.project.service.ShowProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShowProjectController {

    @Autowired
    private ShowProjectService showProjectService;

    @GetMapping("/show-project/{word}")
    public ResponseEntity<List<Project>> processWord(@PathVariable String word) {
        System.out.println("Search Word [Controller]: "+word);

        List<Project> result = showProjectService.processWord(word);

        System.out.println("Show Result [Controller]: "+result);

        return ResponseEntity.ok(result);
    }

}
