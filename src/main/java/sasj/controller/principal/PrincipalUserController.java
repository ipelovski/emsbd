package sasj.controller.principal;

import sasj.controller.Extensions;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.data.user.User;
import sasj.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/principal/users")
public class PrincipalUserController implements AuthorizedController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Extensions extensions;

    @GetMapping
    public String list(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "principal/users";
    }

    @GetMapping(WebMvcConfig.addPath)
    public String add(Model model) {
        model.addAttribute("user", new User());
        return "principal/user-details";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String addSubmit(User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "principal/user-details";
        } else {
            userRepository.save(user);
        }
        return "redirect:" + extensions.getPrincipalUrls().users();
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long userId, Model model
    ) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "principal/user-details";
        } else {
            return "";
        }
    }

    @PostMapping(WebMvcConfig.objectIdPathParam)
    public String detailsSubmit(
        @PathVariable(WebMvcConfig.objectIdParamName) Long userId, User user, Model model
    ) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.getPersonalInfo().setFirstName(
                user.getPersonalInfo().getFirstName());
            existingUser.getPersonalInfo().setMiddleName(
                user.getPersonalInfo().getMiddleName());
            existingUser.getPersonalInfo().setLastName(
                user.getPersonalInfo().getLastName());
            existingUser.getPersonalInfo().setAddress(
                user.getPersonalInfo().getAddress());
            existingUser.getPersonalInfo().setBornAt(
                user.getPersonalInfo().getBornAt());
            existingUser.getPersonalInfo().setGender(
                user.getPersonalInfo().getGender());
            userRepository.save(existingUser);
        }
        model.addAttribute("user", optionalUser.orElse(null));
        return "redirect:" + extensions.getPrincipalUrls().user(user);
    }
}
