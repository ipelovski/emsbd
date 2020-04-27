package sasj.controller.user;

import sasj.controller.AuthorizedController;
import sasj.controller.home.HomeUrls;
import sasj.data.user.User;
import sasj.data.user.UserRepository;
import sasj.data.user.UserService;
import sasj.util.Util;
import sasj.web.RedirectingAuthenticationSuccessHandler;
import sasj.config.WebMvcConfig;
import sasj.controller.SecuredController;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.Optional;

@Controller
public class UserController implements SecuredController, AuthorizedController {
    public static final String signIn = "signIn";
    public static final String signInRole = "signInRole";
    public static final String signUp = "signUp";
    public static final String signOut = "signOut";
    public static final String profile = "profile";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedirectingAuthenticationSuccessHandler successHandler;
    @Autowired
    private UserService userService;
    @Autowired
    private Util util;
    @Autowired
    private UserUrls userURLs;
    @Autowired
    private HomeUrls homeURLs;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/sign-up", WebMvcConfig.localePathParam + "/sign-in*")
            .permitAll();
    }

    @GetMapping(value = "/sign-in", name = signIn)
    public String signIn(Model model, String error, String logout, String requested) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            return "redirect:" + homeURLs.home();
        } else {
            if (error != null) {
                model.addAttribute("error", util.localize("user.usernamePasswordIncorrect"));
            }
            if (logout != null) {
                model.addAttribute("logout", util.localize("user.signedOut"));
            }
            model.addAttribute("requested", requested);
            return "sign-in";
        }
    }

    @PostMapping(value = "/sign-in-role", name = signInRole)
    public void signInRole(HttpServletRequest request, HttpServletResponse response,
        User.Role role, RedirectAttributes model) throws Exception {
        Optional<User> optionalUser = userRepository.findFirstByRole(role);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String redirectUrl = successHandler.getTargetUrl(request, response);
            URI uri = new URI(redirectUrl);
            response.sendRedirect(uri.toASCIIString());
        } else {
            model.addFlashAttribute("error", util.localize("user.noUserFoundWithRole",
                util.localize("user.role." + role.name().toLowerCase())));
            response.sendRedirect(userURLs.signIn());
        }
    }

    @GetMapping(value = "/sign-up", name = signUp)
    public String signUpForm(SignUpForm signUpForm) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            return "redirect:" + homeURLs.home();
        } else {
            return "sign-up";
        }
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
                    "username", "unique", util.localize("user.usernameTaken"));
                return "sign-up";
            }
            User user = new User();
            String encodedPassword = passwordEncoder.encode(password);
            user.setUsername(username);
            user.setPassword(encodedPassword);
            user.setEmail(signUpForm.getEmail());
            user.setRole(User.Role.valueOf(signUpForm.getRole().name()));
            userRepository.save(user);
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:" + homeURLs.home();
        } catch(Exception e) {
            e.printStackTrace();
            signUpForm.setError(e.getMessage());
            return "sign-up";
        }
    }

    @GetMapping("/sign-out")
    public String signOut() {
        return "";
    }

    @GetMapping(value = "/profile", name = profile)
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser()
            .orElseThrow(() -> new IllegalStateException("no current user."));
        User user = userRepository.findById(currentUser.getId()).get();
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return ""; // TODO
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
        @NotNull
        private String email;
        @NotNull
        private SignUpRole role;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public SignUpRole getRole() {
            return role;
        }

        public void setRole(SignUpRole role) {
            this.role = role;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public enum SignUpRole {
        student, teacher
    }
}
