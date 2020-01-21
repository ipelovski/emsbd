package emsbj;

import emsbj.admin.SecuredController;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController implements SecuredController {
    private static final GrantedAuthority anonymous = new SimpleGrantedAuthority("ROLE_ANONYMOUS");

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers("/", "/home")
            .permitAll();
    }

    @GetMapping({"", "home"})
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("isAnonymous",
            authentication.getAuthorities().contains(anonymous));
        if (authentication.getPrincipal() instanceof UserDetails) {
            model.addAttribute("user", authentication.getPrincipal());
        }
        return "home.html";
    }
}
