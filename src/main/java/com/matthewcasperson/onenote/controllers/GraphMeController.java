package com.matthewcasperson.onenote.controllers;

import org.springframework.stereotype.Controller;

@Controller
public class GraphMeController {
    
    public String getGraphMe() {
        return "me";
    }
}
