package com.matthewcasperson.onenote.authproviders;

import com.google.common.collect.ImmutableMap;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.UserAssertion;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;

import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

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

        final String oboToken = getAccessToken(accessToken, clientId, clientSecret);
        return CompletableFuture.completedFuture(oboToken);

//        final MultiValueMap formData = CollectionUtils.toMultiValueMap(
//            ImmutableMap.<String, List<String>>builder()
//                .put("grant_type", List.of("urn:ietf:params:oauth:grant-type:jwt-bearer"))
//                .put("client_id", List.of(clientId))
//                .put("client_secret", List.of(clientSecret))
//                .put("assertion", List.of(accessToken))
//                .put("scope", List.of(scope))
//                .put("requested_token_use", List.of("on_behalf_of"))
//                .build());
//
//        return WebClient
//            .create("https://login.microsoftonline.com/" + tenant + "/oauth2/v2.0/token")
//            .post()
//            .bodyValue(BodyInserters.fromFormData(formData))
//            .retrieve()
//            .bodyToMono(String.class)
//            .toFuture();
    }

    private String getAccessToken(final String authToken, final String clientId, final String clientSecret) {
        try {
            final IClientCredential credential = ClientCredentialFactory.createFromSecret(clientSecret);
            final ConfidentialClientApplication cca = ConfidentialClientApplication.builder(clientId, credential)
                .authority("https://login.microsoftonline.com/matthewcasperson.onmicrosoft.com").build();

            final OnBehalfOfParameters parameters = OnBehalfOfParameters
                .builder(Collections.singleton("https://graph.microsoft.com/.default"),
                    new UserAssertion(authToken)).build();
            return cca.acquireToken(parameters).join().accessToken();
        }
        catch (final Exception ex) {
            return "";
        }
    }

}
