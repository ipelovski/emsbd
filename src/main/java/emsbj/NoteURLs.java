package emsbj;

import emsbj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NoteURLs {
    @Autowired
    private NoteController noteController;
    //            public Node add() {
//                return new Node(URLBuilder.get(NoteController.class, WebMvcConfig.addName));
//            }
    public ViewInfo add(Student student, Course course, Lesson lesson) {
        return new ViewInfo(new URLBuilder(NoteController.class, WebMvcConfig.addName)
            .queryParam(NoteController.studentQueryParam, student.getId())
            .queryParam(NoteController.courseQueryParam, course.getId())
            .queryParam(NoteController.lessonQueryParam, lesson,
                Lesson::getId, Objects::nonNull)
            .build(),
            () -> {
                noteController.configure(null);
                return null;
            });
    }
}
