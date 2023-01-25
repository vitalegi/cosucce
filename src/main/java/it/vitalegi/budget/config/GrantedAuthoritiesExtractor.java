package it.vitalegi.budget.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class GrantedAuthoritiesExtractor
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        logger.info("HELLO");
        logger.info("process token {}", jwt);
        logger.info("claims: {}", jwt.getClaims());
        isVerified(jwt.getClaims());
        logger.info("headers: {}", jwt.getHeaders());
        logger.info("subject: {}", jwt.getSubject());
        Collection<?> authorities = (Collection<?>)
                jwt.getClaims()
                   .getOrDefault("mycustomclaim", Collections.emptyList());

        return authorities.stream()
                          .map(Object::toString)
                          .map(SimpleGrantedAuthority::new)
                          .collect(Collectors.toList());
    }

    protected boolean isVerified(Map<String, Object> claims) {
        claims.forEach((key, value) -> logger.info("claim {}={}, types: {}, {}", key, value, key.getClass(),
                value.getClass()));
        return false;
    }
}