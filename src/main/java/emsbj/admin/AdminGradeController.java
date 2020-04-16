package emsbj.admin;

import emsbj.Extensions;
import emsbj.grade.Grade;
import emsbj.grade.GradeRepository;
import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
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
public class AdminGradeController implements AuthorizedController {
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private Extensions extensions;

    public static final String modelDescription = "The object where the view model is kept.";

    /**
     * Lists all grades that are being taught in the school.
     * This implies there are school classes for each listed grade.
     * @param model {@value modelDescription}
     * @return The name of the template for this view.
     */
    @GetMapping
    public String list(Model model) {
        Iterable<Grade> grades = gradeRepository.findByOrderByOrdinalAsc();
        model.addAttribute("grades", grades);
        model.addAttribute("emptyGrade", new Grade((String)null));
        return "admin/grades";
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
        return "redirect:" + extensions.getAdminUrls().grades();
    }
}
