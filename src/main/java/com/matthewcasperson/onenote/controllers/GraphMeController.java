package com.matthewcasperson.onenote.controllers;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import com.microsoft.graph.models.User;

@Controller
public class GraphMeController {

  @Autowired
  WebClient webClient;

  @GetMapping("/me")
  public String getGraphMe(@RegisteredOAuth2AuthorizedClient("api") OAuth2AuthorizedClient client) {

    final User user = webClient
        .get()
        .uri("http://localhost:8081/me")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(User.class)
        .block();

    System.out.println(user.givenName + " " + user.surname);

    return "me";
  }
}
