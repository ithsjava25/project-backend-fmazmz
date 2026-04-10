package org.fmazmz.casemanager.user.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.fmazmz.casemanager.common.api.openapi.UnauthorizedServerErrorResponses;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Oauth2 API", description = "Authentication and user onboarding via OAuth2")
@RequestMapping(
        path = "api/v1/auth",
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
                                    schema = @Schema(implementation = UserResponse.class)
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
    ResponseEntity<ApiResponseWrapper<UserResponse>> me(OAuth2AuthenticationToken authentication);
}
