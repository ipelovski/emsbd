package emsbj.admin;

import emsbj.Student;
import emsbj.StudentRepository;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController implements LocalizedController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        return "/admin/students.html";
    }
}
