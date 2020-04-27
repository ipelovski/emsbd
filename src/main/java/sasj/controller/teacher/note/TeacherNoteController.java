package sasj.controller.teacher.note;

import sasj.controller.Breadcrumbs;
import sasj.data.course.Course;
import sasj.data.course.CourseRepository;
import sasj.controller.Extensions;
import sasj.data.lesson.Lesson;
import sasj.data.lesson.LessonRepository;
import sasj.data.note.Note;
import sasj.data.note.NoteRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentRepository;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.data.user.User;
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
@RequestMapping("/teacher/notes")
public class TeacherNoteController implements SecuredController, AuthorizedController {
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
    private Extensions extensions;
    @Autowired
    private TeacherNoteUrls teacherNoteURLs;

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
        @RequestParam(value = studentQueryParam) Long studentId,
        @RequestParam(value = courseQueryParam) Long courseId,
        @RequestParam(value = lessonQueryParam, required = false) Long lessonId,
        Model model
    ) {
        Course course = courseRepository.findById(courseId).get();
        Student student;
        if (studentId != null) {
            student = studentRepository.findById(studentId).get();
        } else {
            return "";
        }
        Iterable<Note> notes;
        Lesson lesson = null;
        if (lessonId != null) {
            lesson = lessonRepository.findById(lessonId).get();
            notes = noteRepository.findByStudentAndCourseAndLesson(
                student, course, lesson);
        } else {
            notes = noteRepository.findByStudentAndCourse(student, course);
        }
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);
        model.addAttribute("notes", notes);
        model.addAttribute(Breadcrumbs.modelAttributeName, teacherNoteURLs.listBreadcrumb(student, course, lesson).build());
        return "teacher/notes";
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
            Lesson lesson = null;
            if (lessonId != null) {
                // TODO
                lesson = lessonRepository.findById(lessonId).get();
                note.setLesson(lesson);
            }
            model.addAttribute("note", note);
            model.addAttribute(Breadcrumbs.modelAttributeName, teacherNoteURLs.addNoteBreadcrumb(student, course, lesson).build());
            return "teacher/add-note";
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
            return "redirect:" + extensions.getTeacherUrls()
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
            Note note = optionalNote.get();
            model.addAttribute("note", note);
            model.addAttribute(Breadcrumbs.modelAttributeName, teacherNoteURLs.editNoteBreadcrumb(note).build());
            return "teacher/edit-note";
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
            return "redirect:" + extensions.getTeacherUrls().notes().notes(
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
            return "redirect:" + extensions.getTeacherUrls().notes()
                .notes(student, course, lesson);
        } else {
            return "";
        }
    }
}
