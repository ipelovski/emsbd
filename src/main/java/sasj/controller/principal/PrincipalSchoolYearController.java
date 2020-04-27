package sasj.controller.principal;

import sasj.data.schoolyear.SchoolYear;
import sasj.data.schoolyear.SchoolYearRepository;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/principal/school-years")
public class PrincipalSchoolYearController implements AuthorizedController {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<SchoolYear> schoolYears = schoolYearRepository.findAllWithAll();
        model.addAttribute("schoolYears", schoolYears);
        return "principal/school-years";
    }
}
