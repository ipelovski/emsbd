package emsbd;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchoolYearRepositoryTest {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    @After
    public void cleanup() {
        schoolYearRepository.deleteAll();
    }

    @Test
    public void insertYear() {
        schoolYearRepository.save(new SchoolYear(2020, 2021));
        Assert.assertEquals(1, schoolYearRepository.count());
    }

    @Test
    public void findSchoolYearByBeginYear() {
        schoolYearRepository.save(new SchoolYear(2020, 2021));
        Optional<SchoolYear> optionalSchoolYear = schoolYearRepository
            .findByBeginYear(2020);
        Assert.assertTrue(optionalSchoolYear.isPresent());
        Assert.assertEquals(2020, optionalSchoolYear.get().getBeginYear());
    }
}
