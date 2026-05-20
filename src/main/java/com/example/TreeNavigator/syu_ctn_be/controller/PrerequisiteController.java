package com.example.TreeNavigator.syu_ctn_be.controller;

import com.example.TreeNavigator.syu_ctn_be.dto.TreeNavigatorResponse;
import com.example.TreeNavigator.syu_ctn_be.dto.prerequisiteDTO;
import com.example.TreeNavigator.syu_ctn_be.service.TreeNavigatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/V1/prerequisites")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PrerequisiteController {

    private final TreeNavigatorService treeNavigatorService;

    @GetMapping
    public List<prerequisiteDTO> getPrerequisites() {
        TreeNavigatorResponse response = treeNavigatorService.getCourseTree();
        return response.getLinks();
    }
}