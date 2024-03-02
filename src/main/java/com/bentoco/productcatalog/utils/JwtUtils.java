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

import static com.bentoco.productcatalog.constants.JwtClaimConstants.ROLE_CLAIM;

@Component
public class JwtUtils {

    @Value("${security.jwt-secret}")
    private String secret;

    private static final Logger logger = LogManager.getLogger(JwtUtils.class);

    public Boolean validateTokenRoles(String authorization, Role[] roles) {
        try {
            var roleClaim = extractClaimFromJwt(authorization, ROLE_CLAIM).asString();
            var roleToCheck = Role.valueOf(roleClaim);
            return Arrays.asList(roles).contains(roleToCheck);
        } catch (Exception exception) {
            logger.error("permission required: {}", exception.getMessage());
            throw new UnauthorizedException("not necessary permissions to access this resource.");
        }
    }

    public Claim extractClaimFromJwt(final String authorization, final String claim) {
        try {
            var verifier = getJwtVerifier();
            //todo: refactor
            var verifiedToken = verifier.verify(authorization);
            return verifiedToken.getClaims().get(claim);
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
