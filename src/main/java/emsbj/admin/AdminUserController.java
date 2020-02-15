package emsbj.admin;

import emsbj.config.WebMvcConfig;
import emsbj.controller.LocalizedController;
import emsbj.user.User;
import emsbj.user.UserRepository;
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
@RequestMapping("/admin/users")
public class AdminUserController implements LocalizedController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "/admin/users.html";
    }

    @GetMapping(WebMvcConfig.addPath)
    public String add(Model model) {
        model.addAttribute("user", new User());
        return "/admin/user-details.html";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String addSubmit(User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/user-details";
        } else {
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long userId, Model model
    ) {
        Optional<User> user = userRepository.findById(userId);
        model.addAttribute("user", user.orElse(null));
        return "/admin/user-details.html";
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
            userRepository.save(existingUser);
        }
        model.addAttribute("user", optionalUser.orElse(null));
        return "redirect:/admin/users/" + userId;
    }
}
