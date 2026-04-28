package org.fmazmz.casemanager.user.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fmazmz.casemanager.common.api.ApiResponseWrapper;
import org.fmazmz.casemanager.common.api.openapi.StandardRestApiResponses;
import org.fmazmz.casemanager.user.authentication.CurrentUser;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.dto.UserLookupRequest;
import org.fmazmz.casemanager.user.dto.UserResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "User API", description = "User lookup operations")
@RequestMapping(
        path = "api/v1/users",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@StandardRestApiResponses
public interface UserApi {

    @Operation(summary = "Lookup users by IDs")
    @ApiResponse(responseCode = "200", description = "Lookup successful", useReturnTypeSchema = true)
    @PostMapping("lookup")
    ResponseEntity<ApiResponseWrapper<List<UserResponse>>> lookupByIds(
            @Parameter(hidden = true) @CurrentUser User actor,
            @Valid @RequestBody UserLookupRequest request
    );

    @Operation(summary = "Search users by username/email")
    @ApiResponse(responseCode = "200", description = "Search successful", useReturnTypeSchema = true)
    @GetMapping("search")
    ResponseEntity<ApiResponseWrapper<List<UserResponse>>> searchUsers(
            @Parameter(hidden = true) @CurrentUser User actor,
            @RequestParam("q") String query
    );
}
