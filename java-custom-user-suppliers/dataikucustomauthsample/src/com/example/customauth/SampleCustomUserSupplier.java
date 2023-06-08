package com.example.customauth;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.dataiku.dip.security.auth.ServerAuthenticationFailure;
import com.dataiku.dip.security.auth.UserAttributes;
import com.dataiku.dip.security.auth.UserIdentity;
import com.dataiku.dip.security.auth.UserNotFoundException;
import com.dataiku.dip.security.auth.UserQueryFilter;
import com.dataiku.dip.security.custom.CustomUserSupplier;

/**
 * Supply a user from an external user store into DSS.
 * This supplier needs to fetch the user from the external store using the user attribute resulting from the authentication in DSS and then map
 * the user into a DSS user attribute, in order to sync or provisioning it if needed.
 */
public class SampleCustomUserSupplier implements CustomUserSupplier {

    private Set<UserAttributes> users = new HashSet<>();
    
    public SampleCustomUserSupplier() {
        UserAttributes alice = new UserAttributes();
        alice.login = "alice";
        alice.email = "alice@dataiku.com";
        alice.displayName = "Alice";
        alice.sourceGroupNames.add("admin");
        alice.sourceGroupNames.add("i-team");
        users.add(alice);

        UserAttributes bob = new UserAttributes();
        bob.login = "bob";
        bob.email = "bob@dataiku.com";
        bob.displayName = "Bob";
        bob.sourceGroupNames.add("i-team");
        users.add(bob);
    }
    
    /**
     * Read the user from the source into user attributes.
     * Note: The user attributes can be use for either sync or provision, depending on if a user in DSS already exist.
     * The user supplier is not directly in charge of saving the user in DSS.
     * @param userIdentity The user identity issued from the authentication.
     * @return The external user mapped into a DSS user attributes.
     * @throws ServerAuthenticationFailure if an unexpected error occurs
     * @throws UserNotFoundException if no user in the external user source matches the user identity.
     */
    @Override
    public UserAttributes getUserAttributes(UserIdentity userIdentity) throws ServerAuthenticationFailure, UserNotFoundException {
        if ("charlie".equals(userIdentity.login)) {
            throw new RuntimeException("Simulate a RuntimeException");
        }
        if ("delta".equals(userIdentity.login)) {
            throw new ServerAuthenticationFailure("Simulate an ServerAuthenticationFailure exception");
        }
        //Fetch the user  from the external source.
        return users.stream().filter(u -> u.login.equals(userIdentity.login)).findAny()
                .orElseThrow(() -> new UserNotFoundException("Couldn't find user '" + userIdentity.login + "'"));
    }

    /**
     * Fetch users from the source into user attributes.
     * This method is used to display users from this source through the admin UI or the public API.
     * The user supplier is not directly in charge of saving the user in DSS.
     * @param filter filters that must be applied on this source users
     * @return a set of external users who match the provided filters of all users if the filters are empty
     * @throws ServerAuthenticationFailure if an unexpected error occurs
     */
    @Override
    public Set<UserAttributes> fetchUsers(UserQueryFilter filter) throws ServerAuthenticationFailure {
        if (filter.getLogin() != null) {
            return users.stream().filter(u -> u.login.equals(filter.getLogin())).collect(Collectors.toSet());
        } else if (filter.getGroupName() != null) {
            return users.stream().filter(u -> u.sourceGroupNames.contains(filter.getGroupName())).collect(Collectors.toSet());
        } else {
            return users;
        }
    }

    /**
     * Fetch groups from the source.
     * This method is used to display groups from this source through the admin UI or the public API.
     * @return a set of external group names
     * @throws ServerAuthenticationFailure if an unexpected error occurs
     */
    @Override
    public Set<String> fetchGroups() throws ServerAuthenticationFailure {
        // fetch groups
        return users.stream().flatMap(u -> u.sourceGroupNames.stream()).collect(Collectors.toSet());
    }

    /**
     * Whether the user supplier is *able* to sync users on demand (i.e. not during login process)
     */
    @Override
    public boolean canSyncOnDemand() {
        return true;
    }

    /**
     * Whether the user supplier is *able* to fetch users using filters
     */
    @Override
    public boolean canFetchUsers() {
        return true;
    }

    /**
     * Whether the user supplier is *able* to fetch groups
     */
    @Override
    public boolean canFetchGroups() {
        return true;
    }
}
