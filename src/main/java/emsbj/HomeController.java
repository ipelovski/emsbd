package emsbj;

import emsbj.controller.HasUserController;
import emsbj.controller.SecuredController;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(Application.localePathParam)
public class HomeController implements SecuredController, HasUserController {

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers("/", "/home/**", Application.localePathParam)
            .permitAll();
    }

    @GetMapping({"", "home"})
    public String index() {
        return "home.html";
    }
}
