package com.bentoco.productcatalog.configurations.middlewares;

import com.bentoco.productcatalog.configurations.interfaces.AccessControl;
import com.bentoco.productcatalog.core.model.Profile;
import com.bentoco.productcatalog.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.UUID;

import static com.bentoco.productcatalog.constants.JwtClaimConstants.OWNER_ID_CLAIM;

@Component
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(RequestInterceptor.class);

    private final JwtUtils jwtUtils;
    private final RequestContext requestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        jwtUtils.extractPayload(authorization);
        this.setOwnerIdToContext();
        return hasRequiredPermission((HandlerMethod) handler);
    }

    private boolean hasRequiredPermission(HandlerMethod handlerMethod) {
        var roleRequired = handlerMethod.getMethodAnnotation(AccessControl.class);
        if (Objects.isNull(roleRequired)) {
            logger.info("no role requirement for this method. all users are welcome!");
            return true;
        }
        return jwtUtils.validateRoles(roleRequired.value());
    }

    private void setOwnerIdToContext() {
        var ownerId = jwtUtils.extractClaim(OWNER_ID_CLAIM).asString();
        requestContext.profile = new Profile(UUID.fromString(ownerId));
    }
}
