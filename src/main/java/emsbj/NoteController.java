package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.SecuredController;
import emsbj.user.User;
import emsbj.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController implements SecuredController, AuthorizedController {
    public static final String studentQueryParam = "student";
    public static final String courseQueryParam = "course";
    public static final String lessonQueryParam = "lesson";
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Extensions extensions;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/notes", WebMvcConfig.localePathParam + "/notes/")
            .hasRole(User.Role.student.name().toUpperCase())
            .antMatchers(WebMvcConfig.localePathParam + "/notes/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping
    public String list(
        @RequestParam(value = studentQueryParam, required = false) Long studentId,
        @RequestParam(value = courseQueryParam) Long courseId,
        @RequestParam(value = lessonQueryParam, required = false) Long lessonId,
        Model model
    ) {
        Course course = courseRepository.findById(courseId).get();
        User user = userService.getCurrentUser().get();
        Student student;
        if (user.getRole() == User.Role.student) {
            student = studentRepository.findByUserId(user.getId()).get();
        } else if (studentId != null) {
            student = studentRepository.findById(studentId).get();
        } else {
            return "";
        }
        Iterable<Note> notes;
        if (lessonId != null) {
            Lesson lesson = lessonRepository.findById(lessonId).get();
            notes = noteRepository.findByStudentAndCourseAndLesson(
                student, course, lesson);
            model.addAttribute("lesson", lesson);
        } else {
            notes = noteRepository.findByStudentAndCourse(student, course);
        }
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("notes", notes);
        return "notes";
    }

    @GetMapping(value = WebMvcConfig.addPath, name = WebMvcConfig.addName)
    public String addNoteForm(
        @RequestParam(studentQueryParam) Long studentId,
        @RequestParam(value = courseQueryParam) Long courseId,
        @RequestParam(value = lessonQueryParam, required = false) Long lessonId,
        Model model
    ) {
        Optional<Student> optionalStudent  = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Note note = new Note();
            note.setStudent(student);
            // TODO
            Course course = courseRepository.findById(courseId).get();
            note.setCourse(course);
            if (lessonId != null) {
                // TODO
                Lesson lesson = lessonRepository.findById(lessonId).get();
                note.setLesson(lesson);
            }
            model.addAttribute("note", note);
            return "add-note";
        } else {
            return "";
        }
    }

    @PostMapping(WebMvcConfig.addPath)
    public String addSubmit(
        Note note,
        @RequestParam(value = "redirect", required = false) String redirect
    ) {
        note.setCreatedOn(Instant.now());
        noteRepository.save(note);
        if (redirect != null) {
            return "redirect:" + redirect;
        } else {
            return "redirect:" + extensions.getURLs()
                .notes().notes(note.getStudent(), note.getCourse(), note.getLesson());
        }
    }

    @GetMapping(value = WebMvcConfig.objectIdPathParam + WebMvcConfig.editPath, name = WebMvcConfig.editName)
    public String edit(
        @PathVariable(WebMvcConfig.objectIdParamName) Long noteId,
        Model model
    ) {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isPresent()) {
            model.addAttribute("note", optionalNote.get());
            return "edit-note";
        } else {
            return "";
        }
    }

    @PostMapping(value = WebMvcConfig.objectIdPathParam + WebMvcConfig.editPath, name = WebMvcConfig.editName)
    public String editSubmit(
        @PathVariable(WebMvcConfig.objectIdParamName) Long noteId,
        Note note, Model model
    ) {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isPresent()) {
            Note existingNote = optionalNote.get();
            existingNote.setText(note.getText());
            noteRepository.save(existingNote);
            return "redirect:" + extensions.getURLs().notes().notes(
                existingNote.getStudent(), existingNote.getCourse(), existingNote.getLesson());
        } else {
            return "";
        }
    }

    @PostMapping(WebMvcConfig.objectIdPathParam + WebMvcConfig.removePath)
    public String remove(
        @PathVariable(WebMvcConfig.objectIdParamName) Long noteId,
        Model model
    ) {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isPresent()) {
            Note existingNote = optionalNote.get();
            Student student = existingNote.getStudent();
            Course course = existingNote.getCourse();
            Lesson lesson = existingNote.getLesson();
            noteRepository.delete(existingNote);
            return "redirect:" + extensions.getURLs().notes()
                .notes(student, course, lesson);
        } else {
            return "";
        }
    }
}
