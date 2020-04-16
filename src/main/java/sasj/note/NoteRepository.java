package sasj.note;

import sasj.course.Course;
import sasj.lesson.Lesson;
import sasj.student.Student;
import org.springframework.data.repository.CrudRepository;

public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findByStudentAndCourse(Student student, Course course);
    Iterable<Note> findByStudentAndCourseAndLesson(Student student, Course course, Lesson lesson);
}
