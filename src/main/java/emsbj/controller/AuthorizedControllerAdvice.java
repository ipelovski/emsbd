package emsbj.controller;

import emsbj.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@ControllerAdvice(assignableTypes = {AuthorizedController.class})
public class AuthorizedControllerAdvice {
    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addActiveUser(Model model, Locale locale) {
        userService.setActiveUser(model, locale);
    }
}
