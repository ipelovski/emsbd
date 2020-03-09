package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.SecuredController;
import emsbj.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/lessons")
public class LessonController implements AuthorizedController, SecuredController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;
    @Autowired
    private TeacherService teacherService;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/lessons/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping
    public String list(Model model) {
        Optional<Teacher> optionalTeacher = teacherService.getCurrentTeacher();
        if (optionalTeacher.isPresent()) {
            Iterable<WeeklySlot> weeklySlots = weeklySlotRepository.findAll();
            Iterable<Lesson> teacherLessons = lessonRepository.findAllByCourseTeacher(optionalTeacher.get());
            List<Lesson> lessons = StreamSupport
                .stream(weeklySlots.spliterator(), false)
                .sorted((o1, o2) -> {
                    if (o1.getDay() != o2.getDay()) {
                        return o1.getDay().getValue() - o2.getDay().getValue();
                    } else {
                        return o1.getBegin().toSecondOfDay() - o2.getBegin().toSecondOfDay();
                    }
                })
                .map(weeklySlot -> {
                    Optional<Lesson> optionalLesson = StreamSupport
                        .stream(teacherLessons.spliterator(), false)
                        .filter(lesson -> lesson.getWeeklySlot().getId().equals(weeklySlot.getId()))
                        .findAny();
                    if (optionalLesson.isPresent()) {
                        return optionalLesson.get();
                    } else {
                        Lesson emptyLesson = new Lesson();
                        emptyLesson.setWeeklySlot(weeklySlot);
                        return emptyLesson;
                    }
                })
                .collect(Collectors.toList());
            model.addAttribute("lessons", lessons);
            model.addAttribute("weeklyLessons", new WeeklyLessons(lessons));
            return "lessons";
        } else {
            return "";
        }
    }
}
