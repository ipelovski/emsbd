package emsbj;

import emsbj.mark.Mark;
import emsbj.user.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentRepository studentRepository;
    private SchoolYear schoolYear;
    private Term term;
    private Grade grade;
    private Subject subject;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
        grade = new Grade(schoolYear, "3 а");
        gradeRepository.save(grade);
        term = new Term(schoolYear, "I");
        termRepository.save(term);
        subject = new Subject("Биология");
        subjectRepository.save(subject);
    }

    @After
    public void cleanup() {
        studentRepository.deleteAll();
        gradeRepository.deleteAll();
        subjectRepository.deleteAll();
        termRepository.deleteAll();
        schoolYearRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void insertStudent() {
        studentRepository.save(
            Utils.createStudent("Гошко", "Иванов", "Петков", grade));
        Assert.assertEquals(1, studentRepository.count());
    }

    @Test
    public void findStudentByLastName() {
        String lastName = "Петков";
        studentRepository.save(
            Utils.createStudent("Гошко", "Иванов", lastName, grade));
        studentRepository.save(
            Utils.createStudent("Тошко", "Георгиев", "Караиванов", grade));
        List<Student> students = studentRepository.findByLastName(lastName);
        Assert.assertEquals(1, students.size());
        Assert.assertEquals(lastName, students.get(0).getUser().getPersonalInfo().getLastName());
    }

    @Test
    @Transactional
    public void insertStudentWithMarks() {
        Student student = Utils.createStudent(
            "Гошко", "Иванов", "Петков", grade);
        student.getMarks().add(new Mark(student, subject, 599));
        studentRepository.save(student);
        Assert.assertEquals(1, studentRepository.count());
        Student persistedStudent = studentRepository.findAll().iterator().next();
        Assert.assertEquals(1, persistedStudent.getMarks().size());
        Assert.assertEquals(599, persistedStudent.getMarks().get(0).getRawScore(), 0);
    }


}
