package emsbj.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

@Service
public class UserService {
    private static final String modelAttributeName = "activeUser";
    private final ActiveUser anonymousUser;
    private final AuthenticationTrustResolver authenticationTrustResolver;

    @Autowired
    public UserService() {
        this.anonymousUser = createAnonymous();
        this.authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    }

    public void setActiveUser(Model model) {
        model.addAttribute(modelAttributeName, getActiveUser());
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = authenticationTrustResolver.isAnonymous(authentication);
        if (isAnonymous) {
            return Optional.empty();
        }
        else if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return Optional.of(user);
        } else {
            throw new IllegalStateException("Unknown principal type "
                + authentication.getPrincipal().getClass().getCanonicalName());
        }
    }

    private ActiveUser getActiveUser() {
        return getCurrentUser()
            .map(ActiveUser::new)
            .orElse(anonymousUser);
    }

    private ActiveUser createAnonymous() {
        User anonymousUser = new User("guest");
        anonymousUser.setRole(User.Role.anonymous);
        return new ActiveUser(anonymousUser);
    }
}
