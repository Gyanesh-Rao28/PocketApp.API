package com.gyanesh.pocketManager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    @GetMapping("/status")
    public String healthCheck(){
        return "Running...";
    }

    @GetMapping("/secure")
    public String secureRoute(){
        return "***Secured***";
    }


}
