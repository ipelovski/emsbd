package sasj;

import sasj.grade.Grade;
import sasj.grade.GradeRepository;
import sasj.schoolyear.SchoolYear;
import sasj.schoolyear.SchoolYearRepository;
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
public class GradeRepositoryTest {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private GradeRepository gradeRepository;
    private SchoolYear schoolYear;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
    }

    @After
    public void cleanup() {
        gradeRepository.deleteAll();
        schoolYearRepository.deleteAll();
    }

    @Test
    public void insertGrade() {
        gradeRepository.save(TestUtils.createGrade(3));
        Assert.assertEquals(1, gradeRepository.count());
    }

    @Test
    public void insertGradesWithSameName() {
        gradeRepository.save(TestUtils.createGrade(3));
        TestUtils.assertFails(DataIntegrityViolationException.class,
            () -> gradeRepository.save(TestUtils.createGrade(3)));
    }

    @Test
    public void findGrade() {
        String name = "3";
        gradeRepository.save(TestUtils.createGrade(3));
        Optional<Grade> optionalGrade = gradeRepository
            .findByName(name);
        Assert.assertTrue(optionalGrade.isPresent());
        Assert.assertEquals(name, optionalGrade.get().getName());
    }
}