package emsbj.controller;

import emsbj.user.UserService;
import emsbj.user.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@ControllerAdvice(assignableTypes = {HasUserController.class})
public class HasUserControllerAdvice {
    @Autowired
    private UserService userService;

    @ModelAttribute(name = "user")
    public UserViewModel addUser(Locale locale) {
        return userService.getUser(locale);
    }
}
