package com.matthewcasperson.onenote.controllers;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import com.microsoft.graph.models.User;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OneNoteController {

  @Autowired
  WebClient webClient;

  @GetMapping("/me")
  public String getGraphMe(@RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client) {

    final User user = webClient
        .get()
        .uri("http://localhost:8081/me")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(User.class)
        .block();

    System.out.println(user.givenName + " " + user.surname);

    return "markdown";
  }

  @GetMapping("/")
  public ModelAndView getIndex(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client) {
    final List notes = webClient
        .get()
        .uri("http://localhost:8081/notes/")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(List.class)
        .block();

    final ModelAndView mav = new ModelAndView("index");
    mav.addObject("notes", notes);
    return mav;
  }

  @GetMapping("/notes/{name}/markdown")
  public ModelAndView getGraphMe(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client,
      @PathVariable("name") final String name) {
    final String markdown = webClient
        .get()
        .uri("http://localhost:8081/notes/" + name + "/markdown")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(String.class)
        .block();

    final ModelAndView mav = new ModelAndView("markdown");
    mav.addObject("markdown", markdown);
    return mav;
  }
}
