package com.matthewcasperson.onenote.controllers;

import com.microsoft.graph.requests.GraphServiceClient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphMeController {
    @GetMapping("/me")
    public String getGraphMe() {
        return "me";
    }

    private GraphServiceClient buildGraphClient() {
        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes, clientSecretCredential);
        
        return GraphServiceClient
            .builder()
            .authenticationProvider(tokenCredentialAuthProvider)
            .buildClient();
    }
}
