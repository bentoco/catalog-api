package com.bentoco.productcatalog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.bentoco.productcatalog.controller.exception.InsufficientRoleException;
import com.bentoco.productcatalog.controller.exception.UnauthorizedException;
import com.bentoco.productcatalog.core.model.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.bentoco.productcatalog.constants.JwtClaimConstants.ROLE_CLAIM;

@Component
public class JwtUtils {

    @Value("${security.jwt-secret}")
    private String secret;

    private Map<String, Claim> payload = null;

    private static final Logger logger = LogManager.getLogger(JwtUtils.class);

    public Boolean validateRoles(Role[] roles) {
        try {
            var roleClaim = extractClaim(ROLE_CLAIM).asString();
            var roleToCheck = Role.valueOf(roleClaim);
            return Arrays.asList(roles).contains(roleToCheck);
        } catch (Exception exception) {
            logger.error("permission required: {}", exception.getMessage());
            throw new UnauthorizedException("not necessary permissions to access this resource.");
        }
    }

    public void extractPayload(final String authorization) {
        try {
            var verifier = getJwtVerifier();
            var verifiedToken = verifier.verify(authorization);
            payload = verifiedToken.getClaims();
        } catch (Exception exception) {
            logger.error("invalid authorization token.");
            throw new UnauthorizedException("invalid or missing authentication credentials.");
        }
    }

    public Claim extractClaim(final String claim) {
        try {
            return payload.get(claim);
        } catch (Exception exception) {
            logger.error("{} claim is required.", claim);
            throw new InsufficientRoleException("access denied. please check yours credentials.");
        }
    }

    private JWTVerifier getJwtVerifier() {
        var algorithm = Algorithm.HMAC256(this.secret);
        return JWT.require(algorithm).build();
    }
}
