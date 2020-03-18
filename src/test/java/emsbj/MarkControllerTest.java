package emsbj;

import emsbj.grade.Grade;
import emsbj.grade.GradeRepository;
import emsbj.mark.Mark;
import emsbj.mark.MarkController;
import emsbj.schoolclass.SchoolClass;
import emsbj.schoolclass.SchoolClassRepository;
import emsbj.schoolyear.SchoolYear;
import emsbj.schoolyear.SchoolYearRepository;
import emsbj.student.Student;
import emsbj.student.StudentRepository;
import emsbj.subject.Subject;
import emsbj.subject.SubjectRepository;
import emsbj.subject.SubjectService;
import emsbj.term.Term;
import emsbj.term.TermRepository;
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
    private SubjectService subjectService;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private MarkController markController;
    private SchoolYear schoolYear;
    private Term term;
    private Subject subject;
    private Grade grade;
    private SchoolClass schoolClass;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
        term = new Term(schoolYear, "I");
        termRepository.save(term);
        grade = new Grade(3);
        gradeRepository.save(grade);
        subject = subjectService.create("Биология", grade);
        schoolClass = new SchoolClass();
        schoolClass.setName("А");
        schoolClass.setBeginningSchoolYear(schoolYear);
        schoolClass.setBeginningGrade(grade);
        schoolClassRepository.save(schoolClass);
        Student student = Utils.createStudent(
            "Гошко", "Иванов", "Петков", grade);
        student.getMarks().add(new Mark(student, subject, 599));
        student.setSchoolClass(schoolClass);
        studentRepository.save(student);
    }

    @After
    public void cleanup() {
        studentRepository.deleteAll();
        subjectRepository.deleteAll();
        schoolClassRepository.deleteAll();
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
            schoolClass.getId());
        Assert.assertEquals(1, marks.size());
        Assert.assertEquals(599, marks.get(0).getRawScore());
    }
}
