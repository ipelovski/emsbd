package emsbj;

import emsbj.config.WebMvcConfig;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class NoteURLs {
    public String addNote() {
        return URLBuilder.get(NoteController.class, WebMvcConfig.addName);
    }

    public String notes(CourseStudent student, Course course) {
        return new URLBuilder(NoteController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(NoteController.studentQueryParam, student.getId())
            .queryParam(NoteController.courseQueryParam, course.getId())
            .build();
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

    public String addNote(CourseStudent student, Course course, Lesson lesson) {
        return new URLBuilder(NoteController.class, WebMvcConfig.addName)
            .gatherNamedURIParams()
            .queryParam(NoteController.studentQueryParam, student.getId())
            .queryParam(NoteController.courseQueryParam, course.getId())
            .queryParam(NoteController.lessonQueryParam, lesson,
                Lesson::getId, Objects::nonNull)
            .build();
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
}
