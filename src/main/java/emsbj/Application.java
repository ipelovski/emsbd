package emsbj;

import emsbj.mark.Mark;
import emsbj.user.JournalUserDetailsService;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@SpringBootApplication
public class Application {

    public static final String[] supportedLocalesArray = { "en", "bg" };
    public static final Collection<String> supportedLocales = Arrays.asList(supportedLocalesArray);
    public static final Locale defaultLocale = Locale.forLanguageTag("en");

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

    @Bean
    public LocaleResolver localeResolver() {
        return new UrlLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/labels");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
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
