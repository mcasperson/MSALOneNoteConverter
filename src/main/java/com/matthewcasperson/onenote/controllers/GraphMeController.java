package com.matthewcasperson.onenote.controllers;

import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.matthewcasperson.onenote.authproviders.OboAuthenticationProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Controller
public class GraphMeController {

  @Autowired
  AADAuthenticationProperties azureAd;

    @GetMapping("/me")
    public ModelAndView getGraphMe() {

         final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) SecurityContextHolder
             .getContext()
             .getAuthentication();

        final String token = ((OidcUser) oauthToken.getPrincipal()).getIdToken().getTokenValue();

         final GraphServiceClient client = buildGraphClient(
             token,
             azureAd.getTenantId(),
             azureAd.getClientId(),
             azureAd.getClientSecret(),
             "https://graph.microsoft.com/user.read+offline_access");
         final User me = client.me().buildRequest().get();
        final ModelAndView mav = new ModelAndView("me");
        // mav.addObject("me", me);
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
