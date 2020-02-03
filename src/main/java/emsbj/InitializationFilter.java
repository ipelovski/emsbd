package emsbj;

import emsbj.mark.Mark;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1000000)
public class InitializationFilter implements Filter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
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
    private boolean initialized;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!initialized) {
            initialize();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void initialize() {
        User admin = new User("admin");
        admin.setRole(User.Role.admin);
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);
        JournalAuditAware.setCurrentUser(admin);
        createSchoolYear();
        initialized = true;
    }

    private void createSchoolYear() {
        SchoolYear schoolYear = new SchoolYear(2020, 2021);
        schoolYearRepository.save(schoolYear);

        Term firstTerm = new Term(schoolYear, "I");
        termRepository.save(firstTerm);
        Term secondTerm = new Term(schoolYear, "II");
        termRepository.save(secondTerm);

        Map<Integer, Grade> grades = new HashMap<>(4);
        for (Integer i = 9; i <= 12; i++) {
            Grade grade = new Grade(i);
            gradeRepository.save(grade);
            grades.put(i, grade);
        }

        Subject subject = new Subject(new SubjectName("Биология"), grades.get(9));
        subjectRepository.save(subject);

        Student student = new Student(
            new User("гошко"),
            grades.get(9));
        student.getUser().getPersonalInfo()
            .setFirstName("Гошко")
            .setMiddleName("Георгиев")
            .setLastName("Гошев");
        student.getMarks().add(new Mark(student, subject, 599));
        studentRepository.save(student);
    }
}
