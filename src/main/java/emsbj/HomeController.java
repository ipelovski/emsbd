package emsbj;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    private static final GrantedAuthority anonymous = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
    @GetMapping({"", "home"})
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", authentication);
        model.addAttribute("isAnonymous",
            authentication.getAuthorities().contains(anonymous));
        return "home.html";
    }
}
