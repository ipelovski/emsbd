package emsbj;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class School implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static School instance = null;

    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private GradeRepository gradeRepository;

    private SchoolYear schoolYear;
    private Term term;
    private Map<Integer, Grade> grades;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        School.applicationContext = applicationContext;
        setInstance();
    }

    public static School getInstance() {
        if (instance == null) {
            throw new IllegalStateException("instance is not yet initialized");
        }
        return instance;
    }

    private static void setInstance() {
        instance = applicationContext.getBean(School.class);
        instance.initialize();
    }

    public SchoolYear getSchoolYear() {
        return schoolYear;
    }

    public Term getTerm() {
        return term;
    }

    public synchronized Map<Integer, Grade> getGrades() {
        return grades;
    }

    public void reset() {
        schoolYear = null;
        term = null;
        grades = null;
        initialize();
    }

    private synchronized void initialize() {
        LocalDate now = LocalDate.now();
        Iterable<SchoolYear> persistedSchoolYears = schoolYearRepository
            .findByBeginYearGreaterThanEqual(now.getYear() - 1);
        Optional<SchoolYear> optionalSchoolYear = StreamSupport.stream(persistedSchoolYears.spliterator(), false)
            .max(Comparator.comparingInt(SchoolYear::getBeginYear));
        if (!optionalSchoolYear.isPresent()) {
            return;
        } else {
            schoolYear = optionalSchoolYear.get();
        }

        term = schoolYear.getTerms().stream()
            .filter(aTerm -> now.isAfter(aTerm.getBegin()) && now.isBefore(aTerm.getEnd()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("no term found for now"));

        Iterable<Grade> persistedGrades = gradeRepository.findByOrderByOrdinalAsc();
        grades = new LinkedHashMap<>();
        for (Grade grade : persistedGrades) {
            grades.put(grade.getOrdinal(), grade);
        }
    }
}
