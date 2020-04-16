package sasj.admin;

import sasj.Extensions;
import sasj.schoolyear.SchoolYear;
import sasj.schoolyear.SchoolYearRepository;
import sasj.term.Term;
import sasj.term.TermRepository;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/admin/terms")
public class AdminTermController implements AuthorizedController {
    public static final String schoolYearQueryParam = "school-year";
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private Extensions extensions;

    @GetMapping
    public String list(@RequestParam(schoolYearQueryParam) Long schoolYearId, Model model) {
        Optional<SchoolYear> optionalSchoolYear = schoolYearRepository.findById(schoolYearId);
        if (optionalSchoolYear.isPresent()) {
            SchoolYear schoolYear = optionalSchoolYear.get();
            Iterable<Term> terms = termRepository.findBySchoolYear(schoolYear);
            model.addAttribute("terms", terms);
            model.addAttribute("schoolYear", schoolYear);
            return "admin/terms";
        } else {
            return "";
        }
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long termId, Model model
    ) {
        Optional<Term> term = termRepository.findById(termId);
        model.addAttribute("term", term.orElse(null)); // TODO
        return "admin/term-details";
    }

    @PostMapping(value = WebMvcConfig.objectIdPathParam, name = WebMvcConfig.detailsName)
    public String detailsSubmit(
        @PathVariable(WebMvcConfig.objectIdParamName) Long termId, Term term
    ) {
        Optional<Term> optionalTerm = termRepository.findById(termId);
        if (optionalTerm.isPresent()) {
            Term existingTerm = optionalTerm.get();
            existingTerm.setName(term.getName());
            existingTerm.setBegin(term.getBegin());
            existingTerm.setEnd(term.getEnd());
            termRepository.save(existingTerm);
            return "redirect:" + extensions.getAdminUrls()
                .termsBySchoolYear(existingTerm.getSchoolYear());
        } else {
            return "";
        }
    }
}
