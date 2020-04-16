package sasj;

import sasj.grade.Grade;
import sasj.grade.GradeRepository;
import sasj.mark.Mark;
import sasj.mark.MarkRepository;
import sasj.schoolyear.SchoolYear;
import sasj.schoolyear.SchoolYearRepository;
import sasj.student.Student;
import sasj.student.StudentRepository;
import sasj.subject.Subject;
import sasj.subject.SubjectRepository;
import sasj.subject.SubjectService;
import sasj.term.Term;
import sasj.term.TermRepository;
import sasj.user.User;
import sasj.user.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
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
    private SubjectService subjectService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MarkRepository markRepository;
    @Autowired
    private EntityManager entityManager;
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    private SchoolYear schoolYear;
    private Term term;
    private Grade grade;
    private Subject subject;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
        grade = new Grade(3);
        grade.setOrdinal(3);
        gradeRepository.save(grade);
        term = new Term(schoolYear, "I");
        termRepository.save(term);
        subject = subjectService.create("Биология", grade);
        subjectRepository.save(subject);
    }

    @After
    public void cleanup() {
        studentRepository.deleteAll();
        subjectRepository.deleteAll();
        gradeRepository.deleteAll();
        termRepository.deleteAll();
        schoolYearRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void insertStudent() {
        createStudent("Гошко", "Иванов", "Петков", grade);
        Assert.assertEquals(1, studentRepository.count());
    }

    @Test
    public void findStudentByName() {
        String lastName = "Петков";
        createStudent("Гошко", "Иванов", lastName, grade);
        createStudent("Тошко", "Георгиев", "Караиванов", grade);
        List<Student> students = studentRepository.findByName(lastName);
        Assert.assertEquals(1, students.size());
        Assert.assertEquals(lastName, students.get(0).getUser().getPersonalInfo().getLastName());
    }

    @Test
    @Transactional
    public void insertStudentWithMarks() {
        Student student = createStudent(
            "Гошко", "Иванов", "Петков", grade);
        markRepository.save(new Mark(student, subject, 599));
        Assert.assertEquals(1, studentRepository.count());
        entityManager.clear();
        Student persistedStudent = studentRepository.findAll().iterator().next();
        Assert.assertEquals(1, persistedStudent.getMarks().size());
        Assert.assertEquals(599, persistedStudent.getMarks().get(0).getRawScore(), 0);
    }

    private Student createStudent(String firstName, String middleName, String lastName, Grade grade) {
        User user = new User(firstName.toLowerCase() + "_" + lastName.toLowerCase());
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail(user.getUsername() + "@school.edu");
        user.getPersonalInfo()
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName);
        userRepository.save(user);
        return studentRepository.save(new Student(user));
    }
}
