package org.fmazmz.casemanager.user.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.user.dto.OAuthInfoResponse;
import org.fmazmz.casemanager.user.dto.SignupRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.fmazmz.casemanager.openapi.SignupErrorResponses;
import org.fmazmz.casemanager.openapi.UnauthorizedServerErrorResponses;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Oauth2 API", description = "Authentication and user onboarding via OAuth2")
@RequestMapping(
        path = "api/v1/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface Oauth2Api {

    @Operation(summary = "Get authenticated OAuth2 user info")
    @ApiResponse(
            responseCode = "200",
            description = "OAuth2 user information",
            content = @Content(
                    schemaProperties = {
                            @SchemaProperty(
                                    name = "data",
                                    schema = @Schema(oneOf = {OAuthInfoResponse.class, UserResponse.class})
                            ),
                            @SchemaProperty(
                                    name = "requestId",
                                    schema = @Schema(type = "string", format = "uuid")
                            ),
                            @SchemaProperty(
                                    name = "timestamp",
                                    schema = @Schema(type = "integer", format = "int64")
                            )
                    }
            )
    )
    @UnauthorizedServerErrorResponses
    @GetMapping("me")
    ResponseEntity<ApiResponseWrapper<?>> me(OAuth2AuthenticationToken authentication);

    @Operation(summary = "Complete signup for authenticated OAuth2 user")
    @ApiResponse(
            responseCode = "201",
            description = "Created",
            useReturnTypeSchema = true
    )
    @SignupErrorResponses
    @PostMapping("signup")
    ResponseEntity<ApiResponseWrapper<UserResponse>> signup(
            OAuth2AuthenticationToken authentication,
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload to complete user signup"
            )
            SignupRequest request
    );
}
