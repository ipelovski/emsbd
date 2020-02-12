package emsbj;

import emsbj.controller.LocalizedController;
import emsbj.controller.SecuredController;
import emsbj.mark.Mark;
import emsbj.mark.MarkRepository;
import emsbj.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/term-marks")
public class TermMarksController implements LocalizedController, SecuredController {
    @Autowired
    private MarkRepository markRepository;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(Application.localePathParam + "/term-marks")
            .hasRole(User.Role.student.name().toUpperCase());
    }

    @GetMapping("")
    public String get(Model model) {
        List<Mark> marks = StreamSupport
            .stream(markRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
        model.addAttribute("marks", marks);
        return "term-marks.html";
    }
}
