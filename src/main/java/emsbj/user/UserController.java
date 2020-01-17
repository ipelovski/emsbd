package emsbj.user;

import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/sign-in")
    public String signIn(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Username or password is incorrect.");
        }
        if (logout != null) {
            model.addAttribute("logout", "You are now logged out.");
        }
        return "sign-in";
    }

    @GetMapping("/sign-up")
    public String signUpForm(SignUpForm signUpForm) {
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }
        try {
            User user = new User();
            String username = signUpForm.getUsername();
            String password = passwordEncoder.encode(signUpForm.getPassword());
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(User.Role.user);
            userRepository.save(user);
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
//            Authentication authentication = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "home";
        } catch(Exception e) {
            e.printStackTrace();
            signUpForm.setError(e.getMessage());
            return "sign-up";
        }
    }

    @ScriptAssert(lang = "javascript", reportOn = "confirmPassword",
        script = "java.util.Objects.equals(_this.password, _this.confirmPassword)",
        message = "Your password and confirmation password should match.")
    public static class SignUpForm {
        @NotNull
        @Size(min = 5, max = 20)
        private String username;
        @NotNull
        @Size(min = 7, max = 50)
        private String password;
        @NotNull
        @Size(min = 7, max = 50,
            message = "${min > validatedValue.length() ?" +
                "'The password should be at least 7 characters.'" +
                ":'The password should be at most 50 characters.'}")
        private String confirmPassword;
        private String error;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
