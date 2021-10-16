package com.matthewcasperson.onenote.controllers;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import com.microsoft.graph.models.User;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OneNoteController {

  @Autowired
  WebClient webClient;

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

  @GetMapping("/notes/{name}")
  public ModelAndView getPageView(@PathVariable("name") final String name) {
    final ModelAndView mav = new ModelAndView("pageview");
    mav.addObject("iframesrc", "/notes/" + name + "/html");
    mav.addObject("markdownsrc", "/notes/" + name + "/markdown");
    return mav;
  }

  @GetMapping(value = "/notes/{name}/html", produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public String getNoteHtml(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client,
      @PathVariable("name") final String name,
      final HttpServletResponse response) {
    response.setHeader("X-Frame-Options", "SAMEORIGIN");
    response.setHeader("Content-Security-Policy", " frame-ancestors 'self'");
    return webClient
        .get()
        .uri("http://localhost:8081/notes/" + name + "/html")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  @GetMapping("/notes/{name}/markdown")
  public ResponseEntity<byte[]> getNoteMarkdown(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client,
      @PathVariable("name") final String name) {
    final String markdown = webClient
        .get()
        .uri("http://localhost:8081/notes/" + name + "/markdown")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(String.class)
        .block();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_MARKDOWN);
    final String filename = "page.md";
    headers.setContentDispositionFormData(filename, filename);
    return new ResponseEntity<>(markdown.getBytes(), headers, HttpStatus.OK);
  }
}
