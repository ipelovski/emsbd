package emsbj.note;

import emsbj.course.Course;
import emsbj.lesson.Lesson;
import emsbj.student.Student;
import org.springframework.data.repository.CrudRepository;

public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findByStudentAndCourse(Student student, Course course);
    Iterable<Note> findByStudentAndCourseAndLesson(Student student, Course course, Lesson lesson);
}
