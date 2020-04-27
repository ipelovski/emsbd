package sasj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {LocalizedController.class})
public class LocalizedControllerAdvice {
    @Autowired
    private Extensions extensions;

    @ModelAttribute(name = "x")
    public Object addExtensions() {
        return extensions;
    }
}
