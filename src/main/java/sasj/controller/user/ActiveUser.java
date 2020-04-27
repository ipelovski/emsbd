package sasj.controller.user;

import org.springframework.security.core.userdetails.UserDetails;
import sasj.data.user.User;

// view model of the current user
public class ActiveUser {
    private String username;
    private User.Role role;

    public ActiveUser() {

    }

    public ActiveUser(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.role = User.Role.from(userDetails.getAuthorities().iterator().next().getAuthority());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAnonymous() {
        return role.equals(User.Role.anonymous);
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
}
