package emsbj.admin;

import emsbj.Teacher;
import emsbj.TeacherRepository;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
