package com.matthewcasperson.onenote.authproviders;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

/**
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow#first-case-access-token-request-with-a-shared-secret
 */
public class OboAuthenticationProvider extends BaseAuthenticationProvider {

    private final String accessToken;
    private final String tenant;
    private final String clientId;
    private final String clientSecret;
    private final String scope;

    public OboAuthenticationProvider(final String accessToken, final String tenant, final String clientId,
            final String clientSecret, final String scope) {
        this.accessToken = accessToken;
        this.tenant = tenant;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(final URL requestUrl) {
        // don't attempt to get a token if the request URL doesn't need it
        if (!shouldAuthenticateRequestWithUrl(requestUrl)) {
            return CompletableFuture.completedFuture(null);
        }
       
        final String uri = UriComponentsBuilder
                .fromHttpUrl("https://login.microsoftonline.com/" + tenant + "/oauth2/v2.0/token")
                .queryParam("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .queryParam("client_id", clientId).queryParam("client_secret", clientSecret)
                .queryParam("assertion", accessToken).queryParam("scope", scope)
                .queryParam("requested_token_use", "on_behalf_of").build().toUriString();

        return WebClient.create(uri).get().retrieve().bodyToMono(String.class).toFuture();
    }

}
