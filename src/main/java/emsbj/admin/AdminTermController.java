package emsbj.admin;

import emsbj.SchoolYear;
import emsbj.SchoolYearRepository;
import emsbj.Term;
import emsbj.TermRepository;
import emsbj.config.WebMvcConfig;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@Controller
@RequestMapping("/admin/terms")
public class AdminTermController implements LocalizedController {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;

    @GetMapping
    public String list(@RequestParam("school-year") Long schoolYearId, Model model) {
        Optional<SchoolYear> optionalSchoolYear = schoolYearRepository.findById(schoolYearId);
        if (optionalSchoolYear.isPresent()) {
            SchoolYear schoolYear = optionalSchoolYear.get();
            Iterable<Term> terms = termRepository.findBySchoolYear(schoolYear);
            model.addAttribute("terms", terms);
            model.addAttribute("schoolYear", schoolYear);
            return "/admin/terms.html";
        } else {
            return "";
        }
    }

    @PostMapping(WebMvcConfig.addPath)
    public String addSubmit(@RequestParam("school-year") Long schoolYearId) {
        Optional<SchoolYear> optionalSchoolYear = schoolYearRepository.findById(schoolYearId);
        if (optionalSchoolYear.isPresent()) {
            SchoolYear schoolYear = optionalSchoolYear.get();
            Term firstTerm = new Term(schoolYear, "I");
            firstTerm.setBegin(LocalDate.of(schoolYear.getBeginYear(), Month.SEPTEMBER, 15));
            firstTerm.setEnd(LocalDate.of(schoolYear.getEndYear(), Month.JANUARY, 31));
            termRepository.save(firstTerm);
            Term secondTerm = new Term(schoolYear, "II");
            secondTerm.setBegin(LocalDate.of(schoolYear.getEndYear(), Month.FEBRUARY, 1));
            secondTerm.setEnd(LocalDate.of(schoolYear.getEndYear(), Month.JUNE, 30));
            termRepository.save(secondTerm);
            return "redirect:/admin/terms?school-year=" + schoolYearId;
        } else {
            return "";
        }
    }

    @GetMapping("/{termId:\\d+}")
    public String details(@PathVariable("termId") Long termId, Model model) {
        Optional<Term> term = termRepository.findById(termId);
        model.addAttribute("term", term.orElse(null));
        return "/admin/term-details.html";
    }
}
