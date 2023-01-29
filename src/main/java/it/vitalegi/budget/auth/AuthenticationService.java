package it.vitalegi.budget.auth;

import it.vitalegi.budget.util.BooleanUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class AuthenticationService {

    public String getName() {
        Jwt principal = getPrincipal();
        return principal.getClaimAsString("email");
    }

    public String getUid() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        return authentication.getName();
    }

    public boolean isVerified() {
        Jwt principal = getPrincipal();
        Boolean verified = principal.getClaimAsBoolean("email_verified");
        return BooleanUtil.isTrue(verified);
    }

    protected Jwt getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        return (Jwt) authentication.getPrincipal();
    }
}
