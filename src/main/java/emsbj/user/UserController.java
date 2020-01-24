package emsbj.user;

import emsbj.controller.SecuredController;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Controller
public class UserController implements SecuredController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers("/sign-up")
            .permitAll();
    }

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
            String username = signUpForm.getUsername();
            String password = signUpForm.getPassword();
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                bindingResult.rejectValue(
                    "username", "unique", "This username is already used. Try another one.");
                return "sign-up";
            }
            User user = new User();
            String encodedPassword = passwordEncoder.encode(password);
            user.setUsername(username);
            user.setPassword(encodedPassword);
            user.setRole(User.Role.user);
            userRepository.save(user);
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
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
        @Size(min = 5, max = 20,
            message = "The username should be at " +
                "${min > validatedValue.length() ? formatter.format('least %d', min)" +
                ": formatter.format('most %d', max)}" +
                " characters.")
        private String username;
        @NotNull
        @Size(min = 7, max = 50,
            message = "The password should be at " +
            "${min > validatedValue.length() ? formatter.format('least %d', min)" +
            ": formatter.format('most %d', max)}" +
            " characters.")
        private String password;
        @NotNull
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
