package com.matthewcasperson.onenote.controllers;

import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.matthewcasperson.onenote.authproviders.OboAuthenticationProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.models.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Controller
public class GraphMeController {
    @GetMapping("/me")
    public ModelAndView getGraphMe(AADAuthenticationProperties azureAd) {

        JwtAuthenticationToken oauthToken = (JwtAuthenticationToken) SecurityContextHolder
            .getContext()
            .getAuthentication();

        final GraphServiceClient client = buildGraphClient(
                oauthToken.getToken().getTokenValue(), 
                azureAd.getTenantId(),
                azureAd.getClientId(), 
                azureAd.getClientSecret(),
                "https://graph.microsoft.com/user.read+offline_access");
        final User me = client.me().buildRequest().get();
        final ModelAndView mav = new ModelAndView("me");
        mav.addObject("me", me);
        return mav;
    }

    private GraphServiceClient buildGraphClient(
        final String accessToken, 
        final String tenantId, 
        final String clientId,
        final String clientSecret, 
        final String scopes) {
        final OboAuthenticationProvider oboAuthenticationProvider = new OboAuthenticationProvider(
            accessToken,
            tenantId,
            clientId, 
            clientSecret, 
            scopes);

        return GraphServiceClient.builder().authenticationProvider(oboAuthenticationProvider).buildClient();
    }
}
