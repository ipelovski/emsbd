package sasj.controller.home;

import sasj.controller.Extensions;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.data.user.User;
import sasj.data.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sasj.util.Util;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping
public class HomeController implements SecuredController, AuthorizedController {
    @Autowired
    private Extensions extensions;
    @Autowired
    private UserService userService;
    @Autowired
    private Util util;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers( WebMvcConfig.localePathParam,
                WebMvcConfig.localePathParam + "/",
                WebMvcConfig.localePathParam + "/home/**")
            .permitAll();
    }

    @GetMapping
    public String index(Model model) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            LocalDate currentDate = LocalDate.now();
            User user = optionalUser.get();
            if (user.getRole() == User.Role.student) {
                return util.redirectTo(extensions.getStudentURLs().home());
            } else if (user.getRole() == User.Role.teacher) {
                return util.redirectTo(extensions.getTeacherUrls().home());
            } else if (user.getRole() == User.Role.admin || user.getRole() == User.Role.principal) {
                return util.redirectTo(extensions.getPrincipalUrls().adminIndex());
            } else {
                return "home";
            }
        } else {
            return "home";
        }
    }
}
