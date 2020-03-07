package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.SecuredController;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping
public class HomeController implements SecuredController, AuthorizedController {
    @Autowired
    private Extensions extensions;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers( WebMvcConfig.localePathParam, WebMvcConfig.localePathParam + "/",
                WebMvcConfig.localePathParam + "/home/**")
            .permitAll();
    }

    @GetMapping
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if (user.getRole() == User.Role.student) {
                model.addAttribute("lessons", new Object[]{
                    new Object() {
                        public Object weeklySlot = new Object() {
                            public int ordinal = 1;
                            public LocalTime begin = LocalTime.of(7, 30);
                        };
                        public Object subject = new Object() {
                            public String name = "Биология";
                        };
                        public Object room = new Object() {
                            public String name = "7";
                        };
                        public User teacher = userRepository.findFirstByRole(User.Role.teacher).get();
                    }
                });
                return "student-home";
            } else if (user.getRole() == User.Role.teacher) {
                LocalDate currentDate = LocalDate.now();
                Teacher teacher = teacherRepository.findByUserId(user.getId()).get();
                Iterable<AvailableLesson> lessonsToday = courseRepository
                    .findAllByTeacherAndDay(teacher, currentDate.getDayOfWeek());
                LocalTime currentTime = LocalTime.now();
                List<AvailableLesson> previousAvailableLessons = StreamSupport
                    .stream(lessonsToday.spliterator(), false)
                    .filter(availableLesson -> availableLesson.getWeeklySlot().getEnd().isBefore(currentTime))
                    .collect(Collectors.toList());
                List<AvailableLesson> nextAvailableLessons = StreamSupport
                    .stream(lessonsToday.spliterator(), false)
                    .filter(availableLesson -> availableLesson.getWeeklySlot().getBegin().isAfter(currentTime))
                    .collect(Collectors.toList());
                List<AvailableLesson> currentAvailableLessons = StreamSupport
                    .stream(lessonsToday.spliterator(), false)
                    .filter(availableLesson ->
                        availableLesson.getWeeklySlot().getBegin().isBefore(currentTime)
                        && availableLesson.getWeeklySlot().getEnd().isAfter(currentTime))
                    .collect(Collectors.toList());
                model.addAttribute("currentDate", currentDate);
                model.addAttribute("previousLessons", previousAvailableLessons);
                model.addAttribute("nextLessons", nextAvailableLessons);
                model.addAttribute("currentLessons", currentAvailableLessons);
                return "teacher-home";
            } else if (user.getRole() == User.Role.admin) {
                return "redirect:" + extensions.getAdminUrls().adminIndex();
            } else {
                return "home";
            }
        } else {
            return "home";
        }
    }
}
