package com.example.customauth;

import java.util.HashSet;
import java.util.Set;

import com.dataiku.dip.security.auth.ServerAuthenticationFailure;
import com.dataiku.dip.security.auth.UserAttributes;
import com.dataiku.dip.security.auth.UserAuthenticationException;
import com.dataiku.dip.security.auth.UserIdentity;
import com.dataiku.dip.security.auth.UserSourceType;
import com.dataiku.dip.security.custom.CustomUserAuthenticator;

/**
 * Authenticate a user from an external user store into DSS.
 * The user from the external user store needs to be mapped into user identity.
 */
public class SampleCustomAuthenticator implements CustomUserAuthenticator {

    private Set<UserIdentity> users = new HashSet<>();

    public SampleCustomAuthenticator() {
        UserIdentity alice = new UserIdentity(UserSourceType.CUSTOM, "alice");
        alice.email = "alice@dataiku.com";
        alice.displayName = "Alice";
        alice.groupNames.add("admin");
        alice.groupNames.add("i-team");
        users.add(alice);

        UserIdentity bob = new UserIdentity(UserSourceType.CUSTOM, "bob");
        bob.email = "bob@dataiku.com";
        bob.displayName = "Bob";
        bob.groupNames.add("i-team");
        users.add(bob);
    }

    /**
     * Authenticate a user using login/password credential.
     * Fetch the user from the external user store and map it into a DSS user identity.
     *
     * @param inputLogin The user login
     * @param password The user password
     * @return the user identity corresponding from the external user store.
     * @throws ServerAuthenticationFailure if an unexpected error occurs
     * @throws UserAuthenticationException if the credentials couldn't match an existing user in the external user store
     */
    @Override
    public UserIdentity authenticate(String inputLogin, String password) throws ServerAuthenticationFailure, UserAuthenticationException {
        //Authenticate and fetch the user  from the external source.
        if ("charlie".equals(inputLogin) && "password".equals(password)) {
            throw new RuntimeException("Simulate a RuntimeException");
        }
        if ("delta".equals(inputLogin) && "password".equals(password)) {
            throw new ServerAuthenticationFailure("Simulate an ServerAuthenticationFailure exception");
        }
        return users.stream().filter(u -> u.login.equals(inputLogin) && password.equals("password")).findAny()
                .orElseThrow(() -> new UserAuthenticationException("No user found"));
    }
}
