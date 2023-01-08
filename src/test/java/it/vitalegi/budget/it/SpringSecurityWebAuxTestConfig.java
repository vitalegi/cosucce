package it.vitalegi.budget.it;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    //https://stackoverflow.com/questions/15203485/spring-test-security-how-to-mock-authentication
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(Arrays.asList(//
                user("user1", "Alice", true), //
                user("user2", "Bob", true), //
                user("non-verified", "Carl", false) //
        ));
    }

    protected User user(String uid, String username, boolean verified) {
        List<GrantedAuthority> grants = new ArrayList<>();
        grants.add(new SimpleGrantedAuthority("USERNAME:" + username));
        if (verified) {
            grants.add(new SimpleGrantedAuthority("VERIFIED"));
        }
        return new User(uid, "password", grants);
    }
}