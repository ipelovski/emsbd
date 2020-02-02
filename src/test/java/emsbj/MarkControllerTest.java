package emsbj;

import emsbj.mark.Mark;
import emsbj.mark.MarkController;
import emsbj.user.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MarkControllerTest {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MarkController markController;
    private SchoolYear schoolYear;
    private Term term;
    private Subject subject;
    private Grade grade;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
        term = new Term(schoolYear, "I");
        termRepository.save(term);
        grade = new Grade("3");
        gradeRepository.save(grade);
        subject = new Subject(new SubjectName("Биология"), grade);
        subjectRepository.save(subject);
        Student student = Utils.createStudent(
            "Гошко", "Иванов", "Петков", grade);
        student.getMarks().add(new Mark(student, subject, 599));
        studentRepository.save(student);
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
    public void getMarksPerSubjectAndGrade() {
        List<Mark> marks = markController.getMarksPerSubjectAndGrade(
            schoolYear.getBeginYear(),
            term.getName(),
            subject.getName().getValue(),
            grade.getName());
        Assert.assertEquals(1, marks.size());
        Assert.assertEquals(599, marks.get(0).getRawScore());
    }
}
