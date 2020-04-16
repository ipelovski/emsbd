package sasj;

import sasj.controller.SecuredController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LandingController implements SecuredController {
    @Autowired
    private Extensions extensions;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers("/")
            .permitAll();
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:" + extensions.getURLs().home();
    }
}
