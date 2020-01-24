package emsbj;

import emsbj.user.User;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class JournalAuditAware implements AuditorAware<User> {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        JournalAuditAware.currentUser = currentUser;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        return Optional.ofNullable(currentUser);
    }
}
