package sasj.data.note;

import sasj.data.course.Course;
import sasj.data.lesson.Lesson;
import sasj.data.student.Student;
import org.springframework.data.repository.CrudRepository;

public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findByStudentAndCourse(Student student, Course course);
    Iterable<Note> findByStudentAndCourseAndLesson(Student student, Course course, Lesson lesson);
}
