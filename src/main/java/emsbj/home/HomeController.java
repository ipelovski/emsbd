package emsbj.home;

import emsbj.Extensions;
import emsbj.School;
import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.ControllerBag;
import emsbj.controller.SecuredController;
import emsbj.controller.WebController;
import emsbj.lesson.Lesson;
import emsbj.lesson.LessonRepository;
import emsbj.teacher.Teacher;
import emsbj.teacher.TeacherRepository;
import emsbj.user.User;
import emsbj.user.UserRepository;
import emsbj.user.UserService;
import emsbj.util.UrlPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping
public class HomeController implements SecuredController, AuthorizedController, ControllerBag {
    @Autowired
    private Extensions extensions;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private School school;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers( WebMvcConfig.localePathParam, WebMvcConfig.localePathParam + "/",
                WebMvcConfig.localePathParam + "/home/**")
            .permitAll();
    }

    @GetMapping
    public String index(Model model) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getRole() == User.Role.student) {
                return "student-home";
            } else if (user.getRole() == User.Role.teacher) {
                LocalDate currentDate = LocalDate.now();
                Teacher teacher = teacherRepository.findByUserId(user.getId()).get();
                Iterable<Lesson> lessonsToday = lessonRepository
                    .findAllByTeacherAndDay(teacher, currentDate.getDayOfWeek(), school.getTerm());
                LocalTime currentTime = LocalTime.now();
                List<Lesson> previousLessons = StreamSupport
                    .stream(lessonsToday.spliterator(), false)
                    .filter(lesson -> lesson.getWeeklySlot().getEnd().isBefore(currentTime))
                    .collect(Collectors.toList());
                List<Lesson> nextLessons = StreamSupport
                    .stream(lessonsToday.spliterator(), false)
                    .filter(lesson -> lesson.getWeeklySlot().getBegin().isAfter(currentTime))
                    .collect(Collectors.toList());
                List<Lesson> currentLessons = StreamSupport
                    .stream(lessonsToday.spliterator(), false)
                    .filter(lesson ->
                        lesson.getWeeklySlot().getBegin().isBefore(currentTime)
                        && lesson.getWeeklySlot().getEnd().isAfter(currentTime))
                    .collect(Collectors.toList());
                model.addAttribute("currentDate", currentDate);
                model.addAttribute("previousLessons", previousLessons);
                model.addAttribute("nextLessons", nextLessons);
                model.addAttribute("currentLessons", currentLessons);
                if (currentLessons.size() > 0) {
                    // TODO here too
                    model.addAttribute("currentLesson", currentLessons.get(0));
                }
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

    public static class IndexInput extends WebController.Input {
        public String name;
        public int times;
        public String self;
    }

    @Component
    public class Index extends WebController<IndexInput> {
        {
            setSupportedMethods(METHOD_GET);
        }

        @Override
        public UrlPattern getUrlPattern() {
            return new UrlPattern()
                .addPath("home2")
                .addPathVariable("name")
                .addQueryParameter("times");
        }

        @Override
        public void configure(ExpressionUrlAuthorizationConfigurer<?>.AuthorizedUrl authorizedUrl) {
            authorizedUrl.permitAll();
        }

        @Override
        protected HomeView buildView(IndexInput input) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", input.name);
            data.put("times", input.times);
            input.self = getUrlPattern().buildURI(data).toString();
            return new HomeView(input);
        }
    }
}
