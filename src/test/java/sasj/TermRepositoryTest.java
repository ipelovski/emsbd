package sasj;

import sasj.schoolyear.SchoolYear;
import sasj.schoolyear.SchoolYearRepository;
import sasj.term.Term;
import sasj.term.TermRepository;
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
public class TermRepositoryTest {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    private SchoolYear schoolYear;

    @Before
    public void setup() {
        schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);
    }

    @After
    public void cleanup() {
        termRepository.deleteAll();
        schoolYearRepository.deleteAll();
    }

    @Test
    public void insertTerm() {
        termRepository.save(new Term(schoolYear, "I"));
        Assert.assertEquals(1, termRepository.count());
    }

    @Test
    public void findTermBySchoolYearAndName() {
        String termName = "I";
        termRepository.save(new Term(schoolYear, termName));
        Optional<Term> optionalTerm = termRepository
            .findBySchoolYearAndName(schoolYear, termName);
        Assert.assertTrue(optionalTerm.isPresent());
        Assert.assertEquals(termName, optionalTerm.get().getName());
    }
}
