package sasj.admin;

import sasj.schoolyear.SchoolYear;
import sasj.schoolyear.SchoolYearRepository;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/school-years")
public class AdminSchoolYearController implements AuthorizedController {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<SchoolYear> schoolYears = schoolYearRepository.findAllWithAll();
        model.addAttribute("schoolYears", schoolYears);
        return "admin/school-years";
    }
}
