package emsbj.admin;

import emsbj.SchoolYear;
import emsbj.SchoolYearRepository;
import emsbj.config.WebMvcConfig;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/school-years")
public class AdminSchoolYearController implements LocalizedController {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<SchoolYear> schoolYears = schoolYearRepository.findAllWithAll();
        model.addAttribute("schoolYears", schoolYears);
        return "/admin/school-years.html";
    }

    @PostMapping(value = WebMvcConfig.addPath, name = WebMvcConfig.addName)
    public String addSubmit() {
        int currentYear = LocalDateTime.now().getYear();
        SchoolYear schoolYear = new SchoolYear(currentYear, currentYear + 1);
        schoolYearRepository.save(schoolYear);
        return "redirect:/admin/school-years";
    }
}
