package emsbj.note;

import emsbj.Breadcrumb;
import emsbj.util.Util;
import emsbj.course.Course;
import emsbj.course.CourseURLs;
import emsbj.lesson.Lesson;
import emsbj.lesson.LessonURLs;
import emsbj.student.Student;
import emsbj.web.URLBuilder;
import emsbj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class NoteURLs {
    @Autowired
    private CourseURLs courseURLs;
    @Autowired
    private LessonURLs lessonURLs;
    @Autowired
    private Util util;

    public String notes(Student student, Course course) {
        return notes(student, course, null);
    }

    public String notes(Student student, Course course, Lesson lesson) {
        return new URLBuilder(NoteController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(NoteController.studentQueryParam, student.getId())
            .queryParam(NoteController.courseQueryParam, course.getId())
            .queryParam(NoteController.lessonQueryParam, lesson,
                Lesson::getId, Objects::nonNull)
            .build();
    }

    public String addNote() {
        return URLBuilder.get(NoteController.class, WebMvcConfig.addName);
    }

    public String addNote(Student student, Course course) {
        return addNote(student, course, null);
    }

    public String addNote(Student student, Course course, Lesson lesson) {
        return new URLBuilder(NoteController.class, WebMvcConfig.addName)
            .gatherNamedURIParams()
            .queryParam(NoteController.studentQueryParam, student.getId())
            .queryParam(NoteController.courseQueryParam, course.getId())
            .queryParam(NoteController.lessonQueryParam, lesson,
                Lesson::getId, Objects::nonNull)
            .build();
    }

    public String editNote(Note note) {
        return URLBuilder.get(NoteController.class, WebMvcConfig.editName,
            WebMvcConfig.objectIdParamName, note.getId());
    }

    public String removeNote(Note note) {
        return URLBuilder.get(NoteController.class, WebMvcConfig.removeName,
            WebMvcConfig.objectIdParamName, note.getId());
    }

    public Breadcrumb listBreadcrumb(Student student, Course course, Lesson lesson) {
        if (lesson != null) {
            return new Breadcrumb(notes(student, course, lesson),
                util.capitalize("notes"), lessonURLs.detailsBreadcrumb(lesson));
        } else {
            return new Breadcrumb(notes(student, course, null),
                util.capitalize("notes"), courseURLs.courseBreadcrumb(course));
        }
    }

    public Breadcrumb addNoteBreadcrumb(Student student, Course course, Lesson lesson) {
        return new Breadcrumb(notes(student, course, null),
            util.capitalize("student.addNote"), listBreadcrumb(student, course, lesson));
    }

    public Breadcrumb editNoteBreadcrumb(Note note) {
        return new Breadcrumb(editNote(note), util.capitalize("student.editNote"),
            listBreadcrumb(note.getStudent(), note.getCourse(), note.getLesson()));
    }
}
