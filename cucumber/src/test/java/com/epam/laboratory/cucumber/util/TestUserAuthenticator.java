package com.epam.laboratory.cucumber.util;

import com.epam.laboratory.cucumber.client.ApiClient;
import com.epam.laboratory.cucumber.dto.Credentials;
import com.epam.laboratory.cucumber.dto.TokensResponse;
import com.epam.laboratory.cucumber.util.ContextHolder.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestUserAuthenticator {
    public static final Key<TokensResponse> TOKENS_KEY = Key.of("tokens", TokensResponse.class);

    @Value("${test-data.username}")
    private String testUsername;

    @Value("${test-data.password}")
    private String testPassword;

    private final ContextHolder contextHolder;
    private final ApiClient apiClient;
    private final JsonMapper jsonMapper;

    public void authenticate() {
        var authHeader = BasicAuthenticationHeaderResolver.resolve(new Credentials(testUsername, testPassword));
        var authRequest = apiClient.request("/api/auth/tokens",
                ApiClient.HTTP_METHOD_GET,
                Map.of(ApiClient.AUTHORIZATION_HEADER, authHeader),
                null);
        try (var httpClient = apiClient.spec()) {
            var response = httpClient.send(authRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            var responseBody = response.body();
            if (response.statusCode() == 200) {
                var tokens = jsonMapper.readValue(responseBody, TokensResponse.class);
                contextHolder.put(TOKENS_KEY, tokens);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate test user", e);
        }
    }

}
