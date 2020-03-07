package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.controller.SecuredController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notes")
public class NoteController implements SecuredController {
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {

    }

    @GetMapping(value = WebMvcConfig.addPath, name = WebMvcConfig.addName)
    public String addNoteForm(
        @RequestParam("studentId") Long studentId,
        @RequestParam("lessonId") Long lessonId,
        Model model
    ) {
        Student student = studentRepository.findById(studentId).get();
        Lesson lesson = lessonRepository.findById(lessonId).get();
        Note note = new Note();
        note.setStudent(student);
        note.setLesson(lesson);
        model.addAttribute("note", note);
        return "fragments/add-note::addNote";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String addSubmit(Note note, @RequestParam("redirect") String redirect) {
        noteRepository.save(note);
        return "redirect:" + redirect;
    }
}
