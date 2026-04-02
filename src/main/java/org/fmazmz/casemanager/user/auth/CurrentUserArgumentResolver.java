package org.fmazmz.casemanager.user.auth;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.user.model.User;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticatedUserResolver authenticatedUserResolver;

    public CurrentUserArgumentResolver(AuthenticatedUserResolver authenticatedUserResolver) {
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken token)) {
            throw new AccessDeniedException("OAuth2 authentication required");
        }
        return authenticatedUserResolver.requireUser(token);
    }
}
