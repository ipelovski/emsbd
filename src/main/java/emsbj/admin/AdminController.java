package emsbj.admin;

import emsbj.controller.SecuredController;
import emsbj.user.User;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController implements SecuredController {
    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers("/admin/*")
            .hasRole(User.Role.admin.name().toUpperCase());
    }

    @GetMapping
    public String index() {
        return "admin/index.html";
    }
}
