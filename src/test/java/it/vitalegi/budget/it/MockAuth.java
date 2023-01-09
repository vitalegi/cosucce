package it.vitalegi.budget.it;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockAuth {

    String username(String uid) {
        return "username_" + uid;
    }

    RequestPostProcessor user(String uid) {
        return user(uid, username(uid), true);
    }

    RequestPostProcessor user(String uid, String username, boolean verified) {
        List<GrantedAuthority> grants = new ArrayList<>();
        grants.add(new SimpleGrantedAuthority("USERNAME:" + username));
        if (verified) {
            grants.add(new SimpleGrantedAuthority("VERIFIED"));
        }
        org.springframework.security.core.userdetails.User user;
        user = new org.springframework.security.core.userdetails.User(uid, "password", grants);
        return SecurityMockMvcRequestPostProcessors.user(user);
    }
}
