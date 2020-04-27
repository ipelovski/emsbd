package sasj;

import sasj.data.grade.Grade;
import sasj.data.grade.GradeRepository;
import sasj.data.schoolyear.SchoolYear;
import sasj.data.schoolyear.SchoolYearRepository;
import sasj.data.subject.Subject;
import sasj.data.subject.SubjectName;
import sasj.data.subject.SubjectNameRepository;
import sasj.data.subject.SubjectRepository;
import sasj.data.term.Term;
import sasj.data.term.TermRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubjectRepositoryTest {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectNameRepository subjectNameRepository;
    private SchoolYear schoolYear;
    private Term term;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
        term = new Term(schoolYear, "I");
        termRepository.save(term);
    }

    @After
    public void cleanup() {
        subjectRepository.deleteAll();
        termRepository.deleteAll();
        gradeRepository.deleteAll();
        schoolYearRepository.deleteAll();
    }

    @Test
    public void insertSubject() {
        Grade grade = TestUtils.createGrade(5);
        gradeRepository.save(grade);
        SubjectName subjectName = new SubjectName("Биология");
        subjectName = subjectNameRepository.save(subjectName);
        Subject subject = new Subject(subjectName, grade);
        Assert.assertTrue(subject.isNew());
        subjectRepository.save(subject);
        Assert.assertFalse(subject.isNew());
        Assert.assertEquals(1, subjectRepository.count());
    }

    @Test
    public void insertSubjectsSameNameDifferentGrades() {
        Grade grade1 = TestUtils.createGrade(1);
        gradeRepository.save(grade1);
        Grade grade2 = TestUtils.createGrade(2);
        gradeRepository.save(grade2);
        SubjectName subjectName = new SubjectName("Биология");
        subjectName = subjectNameRepository.save(subjectName);
        Subject subject1 = new Subject(subjectName, grade1);
        subject1 = subjectRepository.save(subject1);
        Subject subject2 = new Subject(subjectName, grade2);
        subject2 = subjectRepository.save(subject2);
        Assert.assertEquals(subject1.getName().getId(), subject2.getName().getId());
        Assert.assertNotEquals(subject1.getGrade().getId(), subject2.getGrade().getId());
    }

    @Test
    public void insertSubjectsSameNameAndSameGrade() {
        Grade grade = TestUtils.createGrade(5);
        gradeRepository.save(grade);
        SubjectName subjectName = new SubjectName("Биология");
        subjectName = subjectNameRepository.save(subjectName);
        Subject subject1 = new Subject(subjectName, grade);
        subjectRepository.save(subject1);
        Subject subject2 = new Subject(subjectName, grade);
        TestUtils.assertFails(DataIntegrityViolationException.class,
            () -> subjectRepository.save(subject2));
    }

    @Test
    public void findSubjectByNameAndGrade() {
        Grade grade = TestUtils.createGrade(5);
        gradeRepository.save(grade);
        SubjectName subjectName = new SubjectName("Биология");
        subjectName = subjectNameRepository.save(subjectName);
        subjectRepository.save(new Subject(subjectName, grade));
        Optional<Subject> optionalSubject = subjectRepository
            .findByNameAndGrade(subjectName.getValue(), grade);
        Assert.assertTrue(optionalSubject.isPresent());
        Assert.assertEquals(subjectName.getValue(), optionalSubject.get().getName().getValue());
    }
}
