package sasj.controller.teacher.note;

import sasj.controller.Breadcrumb;
import sasj.controller.teacher.course.TeacherCourseUrls;
import sasj.data.note.Note;
import sasj.util.Util;
import sasj.data.course.Course;
import sasj.data.lesson.Lesson;
import sasj.controller.teacher.lesson.TeacherLessonUrls;
import sasj.data.student.Student;
import sasj.web.UrlBuilder;
import sasj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TeacherNoteUrls {
    @Autowired
    private TeacherCourseUrls teacherCourseUrls;
    @Autowired
    private TeacherLessonUrls teacherLessonURLs;
    @Autowired
    private Util util;

    public String notes(Student student, Course course) {
        return notes(student, course, null);
    }

    public String notes(Student student, Course course, Lesson lesson) {
        return new UrlBuilder(TeacherNoteController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(TeacherNoteController.studentQueryParam, student.getId())
            .queryParam(TeacherNoteController.courseQueryParam, course.getId())
            .queryParam(TeacherNoteController.lessonQueryParam, lesson,
                Lesson::getId, Objects::nonNull)
            .build();
    }

    public String addNote() {
        return UrlBuilder.get(TeacherNoteController.class, WebMvcConfig.addName);
    }

    public String addNote(Student student, Course course) {
        return addNote(student, course, null);
    }

    public String addNote(Student student, Course course, Lesson lesson) {
        return new UrlBuilder(TeacherNoteController.class, WebMvcConfig.addName)
            .gatherNamedURIParams()
            .queryParam(TeacherNoteController.studentQueryParam, student.getId())
            .queryParam(TeacherNoteController.courseQueryParam, course.getId())
            .queryParam(TeacherNoteController.lessonQueryParam, lesson,
                Lesson::getId, Objects::nonNull)
            .build();
    }

    public String editNote(Note note) {
        return UrlBuilder.get(TeacherNoteController.class, WebMvcConfig.editName,
            WebMvcConfig.objectIdParamName, note.getId());
    }

    public String removeNote(Note note) {
        return UrlBuilder.get(TeacherNoteController.class, WebMvcConfig.removeName,
            WebMvcConfig.objectIdParamName, note.getId());
    }

    public Breadcrumb listBreadcrumb(Student student, Course course, Lesson lesson) {
        if (lesson != null) {
            return new Breadcrumb(notes(student, course, lesson),
                util.capitalize("student.notes"), teacherLessonURLs.detailsBreadcrumb(lesson));
        } else {
            return new Breadcrumb(notes(student, course, null),
                util.capitalize("student.notes"), teacherCourseUrls.courseBreadcrumb(course));
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
