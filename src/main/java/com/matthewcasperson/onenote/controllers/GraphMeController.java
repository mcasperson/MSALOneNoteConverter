package com.matthewcasperson.onenote.controllers;

import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.matthewcasperson.onenote.authproviders.OboAuthenticationProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.models.User;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
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
  public ModelAndView getGraphMe(@RegisteredOAuth2AuthorizedClient("api") OAuth2AuthorizedClient client) {

    final GraphServiceClient graphClient = buildGraphClient(
        client.getAccessToken().getTokenValue(),
        azureAd.getTenantId(),
        azureAd.getClientId(),
        azureAd.getClientSecret(),
        "https://graph.microsoft.com/user.read");
    final User me = graphClient.me().buildRequest().get();
    final ModelAndView mav = new ModelAndView("me");
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

    return GraphServiceClient.builder()
        .authenticationProvider(oboAuthenticationProvider)
        .buildClient();
  }
}
