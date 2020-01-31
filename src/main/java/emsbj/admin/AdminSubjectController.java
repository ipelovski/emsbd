package emsbj.admin;

import emsbj.Subject;
import emsbj.SubjectRepository;
import emsbj.Term;
import emsbj.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/subjects")
public class AdminSubjectController {
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);
        return "/admin/subjects.html";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("subject", new Subject());
        Iterable<Term> terms = termRepository.findAll();
        model.addAttribute("availableTerms", terms);
        return "/admin/subject-details.html";
    }

    @PostMapping("/add")
    public String add(Subject subject, Model model) {
        return "/admin/subject-details.html";
    }
}
