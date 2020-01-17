package emsbj;

import emsbj.mark.Mark;
import emsbj.user.JournalUserDetailsService;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public JournalUserDetailsService userDetailsService() {
        return new JournalUserDetailsService();
    }

    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Component
    private static class Setup {
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

//        @PostConstruct
        public void setup() {
            User admin = new User("admin");
            admin.setRole(User.Role.admin);
            userRepository.save(admin);
            EmsbdAuditAware.setCurrentUser(admin);
            SchoolYear schoolYear = new SchoolYear(2020, 2021);
            schoolYearRepository.save(schoolYear);
            Term term = new Term(schoolYear, "I");
            termRepository.save(term);
            Subject subject = new Subject(term, "Биология");
            subjectRepository.save(subject);
            Grade grade = new Grade(schoolYear, "3 а");
            gradeRepository.save(grade);
            Student student = new Student(
                new User("гошко"),
                grade);
            student.getUser().getPersonalInfo()
                .setFirstName("Гошко")
                .setMiddleName("Георгиев")
                .setLastName("Гошев");
            student.getMarks().add(new Mark(student, subject, 599));
            studentRepository.save(student);
        }
    }
}
