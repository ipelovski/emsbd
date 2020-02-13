package emsbj.admin;

import emsbj.Grade;
import emsbj.GradeRepository;
import emsbj.config.WebMvcConfig;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/admin/grades")
public class AdminGradeController implements LocalizedController {
    @Autowired
    private GradeRepository gradeRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<Grade> grades = gradeRepository.findByOrderByOrdinalAsc();
        model.addAttribute("grades", grades);
        model.addAttribute("emptyGrade", new Grade((String)null));
        return "admin/grades.html";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String add(Grade grade, Model model, Locale locale) {
        Optional<Grade> optionalGrade = gradeRepository.findByName(
            grade.getName());
        if (optionalGrade.isPresent()) {
            model.addAttribute("error", "");
        } else {
            grade.setName(grade.getName().toLowerCase(locale));
            gradeRepository.save(grade);
        }
        return "redirect:/admin/grades";
    }
}
