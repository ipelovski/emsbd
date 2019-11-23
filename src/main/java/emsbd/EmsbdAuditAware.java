package emsbd;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class EmsbdAuditAware implements AuditorAware<User> {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        EmsbdAuditAware.currentUser = currentUser;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        return Optional.ofNullable(currentUser);
    }
}
