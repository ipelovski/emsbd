package sasj.controller.home;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import sasj.data.lesson.Lesson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class HomeService {
    public void populateHomeModel(Model model, Iterable<Lesson> lessonsToday, LocalDate currentDate) {
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
            model.addAttribute("currentLesson", currentLessons.get(0));
        }
    }
}
