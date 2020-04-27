package sasj.controller.principal;

import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.data.user.User;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/principal")
public class PrincipalHomeController implements SecuredController, AuthorizedController {
    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/principal/**")
            .hasRole(User.Role.admin.name().toUpperCase());
    }

    /**
     * An index page for the principal containing links to the rest of principal views.
     * @return The name of the template for this view.
     */
    @GetMapping
    public String index() {
        return "principal/home";
    }
}
