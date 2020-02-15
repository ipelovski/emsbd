package emsbj.admin;

import emsbj.Student;
import emsbj.StudentRepository;
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

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long studentId, Model model
    ) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            model.addAttribute("user", optionalStudent.get().getUser());
            return "admin/user-details.html";
        } else {
            return "";
        }
    }
}
