package sasj;

import sasj.schoolyear.SchoolYearRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InitializationTest {
    @Autowired
    private InitializationFilter initializationFilter;
    @Autowired
    private SchoolYearRepository schoolYearRepository;



    @Test
    public void initialize() {
        initializationFilter.initialize();
        long count = StreamSupport
            .stream(schoolYearRepository.findAll().spliterator(), false)
            .count();
        Assert.assertTrue(count > 0);
    }
}
