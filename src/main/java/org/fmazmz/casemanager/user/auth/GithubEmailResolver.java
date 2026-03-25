package org.fmazmz.casemanager.user.auth;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubEmailResolver {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestClient restClient = RestClient.create();

    public GithubEmailResolver(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public String resolveGithubEmail(OAuth2AuthenticationToken token) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Unable to load GitHub OAuth access token.");
        }

        List<GithubEmailResponse> emails = restClient.get()
                .uri("https://api.github.com/user/emails")
                .header("Authorization", "Bearer " + client.getAccessToken().getTokenValue())
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (emails == null || emails.isEmpty()) {
            throw new IllegalStateException("No email addresses returned by GitHub.");
        }


        return emails.stream()
                .filter(GithubEmailResponse::primary)
                .map(GithubEmailResponse::email)
                .filter(email -> email != null && !email.isBlank())
                .findFirst()
                .orElseGet(() -> emails.stream()
                        .map(GithubEmailResponse::email)
                        .filter(email -> email != null && !email.isBlank())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No usable email found in GitHub response.")));
    }

    private record GithubEmailResponse(String email, boolean primary, boolean verified) {
    }
}
