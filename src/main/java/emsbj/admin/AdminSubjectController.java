package emsbj.admin;

import emsbj.grade.Grade;
import emsbj.grade.GradeRepository;
import emsbj.subject.Subject;
import emsbj.subject.SubjectRepository;
import emsbj.subject.SubjectService;
import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin/subjects")
public class AdminSubjectController implements AuthorizedController {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private GradeRepository gradeRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);
        return "/admin/subjects.html";
    }

    @GetMapping(WebMvcConfig.addPath)
    public String add(Model model) {
        model.addAttribute("subject", new SubjectForm());
        Iterable<Grade> grades = gradeRepository.findAll();
        model.addAttribute("grades", grades);
        return "/admin/subject-details.html";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String add(SubjectForm subjectForm, Model model) {
        Optional<Grade> optionalGrade = gradeRepository.findById(subjectForm.gradeId);
        if (optionalGrade.isPresent()) {
            subjectService.create(subjectForm.name, optionalGrade.get());
            return "redirect:/admin/subjects.html";
        } else {
            model.addAttribute("error", "");
            return "/admin/subjects.html";
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
