package emsbj;

import emsbj.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@RequestMapping("/{locale}/")
public class HomeController implements SecuredController {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers("/", "/home/**", "/{locale}/")
            .permitAll();
    }

    @GetMapping({"", "home"})
    public String index(Model model, Locale locale) {
        userService.setUser(model, locale);
        return "home.html";
    }
}
