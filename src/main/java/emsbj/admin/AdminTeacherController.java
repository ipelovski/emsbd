package emsbj.admin;

import emsbj.Teacher;
import emsbj.TeacherRepository;
import emsbj.config.WebMvcConfig;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin/teachers")
public class AdminTeacherController implements LocalizedController {
    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<Teacher> teachers = teacherRepository.findAll();
        model.addAttribute("teachers", teachers);
        return "/admin/teachers.html";
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long teacherId, Model model
    ) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
        if (optionalTeacher.isPresent()) {
            model.addAttribute("user", optionalTeacher.get().getUser());
            return "admin/user-details.html";
        } else {
            return "";
        }
    }
}
