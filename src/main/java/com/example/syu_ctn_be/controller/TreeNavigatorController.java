package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.TreeNavigatorResponse;
import com.example.syu_ctn_be.service.TreeNavigatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/V1/tree")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TreeNavigatorController {

    private final TreeNavigatorService treeNavigatorService;

    @GetMapping
    public TreeNavigatorResponse getCourseTree() {
        return treeNavigatorService.getCourseTree();
    }
}
