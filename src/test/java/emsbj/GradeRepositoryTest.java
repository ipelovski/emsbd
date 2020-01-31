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
        gradeRepository.save(new Grade(schoolYear, new GradeName("3")));
        Assert.assertEquals(1, gradeRepository.count());
    }

    @Test
    public void findGrade() {
        GradeName gradeName = new GradeName("3");
        gradeRepository.save(new Grade(schoolYear, gradeName));
        Optional<Grade> optionalGrade = gradeRepository
            .findByNameAndSchoolYear(gradeName.getValue(), schoolYear);
        Assert.assertTrue(optionalGrade.isPresent());
        Assert.assertEquals(gradeName.getValue(), optionalGrade.get().getName().getValue());
    }
}