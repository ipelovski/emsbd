package emsbj;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        Subject subject = new Subject(new SubjectName("Биология"));
        Assert.assertTrue(subject.isNew());
        subjectRepository.save(subject);
        Assert.assertFalse(subject.isNew());
        Assert.assertEquals(1, subjectRepository.count());
    }

    @Test
    public void findSubjectByNameAndGrade() {
        Grade grade = new Grade("5");
        gradeRepository.save(grade);
        SubjectName subjectName = new SubjectName("Биология");
        subjectRepository.save(new Subject(subjectName, grade));
        Optional<Subject> optionalSubject = subjectRepository
            .findByNameAndGrade(subjectName.getValue(), grade.getName());
        Assert.assertTrue(optionalSubject.isPresent());
        Assert.assertEquals(subjectName.getValue(), optionalSubject.get().getName().getValue());
    }
}
