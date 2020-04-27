package sasj.controller.principal;

import sasj.controller.Extensions;
import sasj.data.grade.Grade;
import sasj.data.grade.GradeRepository;
import sasj.data.subject.Subject;
import sasj.data.subject.SubjectRepository;
import sasj.data.subject.SubjectService;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/principal/subjects")
public class PrincipalSubjectController implements AuthorizedController {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private Extensions extensions;

    @GetMapping
    public String list(Model model) {
        Iterable<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);
        return "principal/subjects";
    }

    @GetMapping(WebMvcConfig.addPath)
    public String add(Model model) {
        model.addAttribute("subject", new SubjectForm());
        Iterable<Grade> grades = gradeRepository.findAll();
        model.addAttribute("grades", grades);
        return "principal/subject-details";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String add(SubjectForm subjectForm, Model model) {
        Optional<Grade> optionalGrade = gradeRepository.findById(subjectForm.gradeId);
        if (optionalGrade.isPresent()) {
            subjectService.create(subjectForm.name, optionalGrade.get());
            return "redirect:" + extensions.getPrincipalUrls().subjects();
        } else {
            model.addAttribute("error", ""); // TODO
            return "principal/subjects";
        }
    }

    public static class SubjectForm {
        private Long id;
        private String name;
        private Long gradeId;
        private boolean isNew;

        public SubjectForm() {
            this.isNew = true;
        }

        public SubjectForm(Subject subject) {
            this.id = subject.getId();
            this.name = subject.getName().getValue();
            this.gradeId = subject.getGrade().getId();
            this.isNew = false;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getGradeId() {
            return gradeId;
        }

        public void setGradeId(Long gradeId) {
            this.gradeId = gradeId;
        }

        public boolean isNew() {
            return isNew;
        }
    }
}
