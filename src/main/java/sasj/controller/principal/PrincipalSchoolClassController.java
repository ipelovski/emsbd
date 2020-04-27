package sasj.controller.principal;

import sasj.data.School;
import sasj.controller.student.course.StudentCourseController;
import sasj.controller.Extensions;
import sasj.data.grade.Grade;
import sasj.data.grade.GradeRepository;
import sasj.data.lesson.Lesson;
import sasj.data.lesson.LessonRepository;
import sasj.data.room.Room;
import sasj.data.room.RoomRepository;
import sasj.data.schoolclass.SchoolClass;
import sasj.data.schoolclass.SchoolClassRepository;
import sasj.data.schoolyear.SchoolYear;
import sasj.data.schoolyear.SchoolYearRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentRepository;
import sasj.controller.WeeklyLessons;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/principal/school-classes")
public class PrincipalSchoolClassController implements AuthorizedController {
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private Extensions extensions;
    @Autowired
    private School school;

    @GetMapping
    public String list(Model model) {
        Iterable<SchoolClass> schoolClasses = schoolClassRepository.findAll();
        model.addAttribute("schoolClasses", schoolClasses);
        return "principal/school-classes";
    }

    @GetMapping(WebMvcConfig.addPath)
    public String add(Model model) {
        model.addAttribute("schoolClass", new SchoolClass());
        int currentYear = LocalDate.now().getYear();
        Iterable<SchoolYear> schoolYears = schoolYearRepository.findByBeginYearGreaterThanEqual(currentYear);
        model.addAttribute("schoolYears", schoolYears);
        Iterable<Grade> grades = gradeRepository.findByOrderByOrdinalAsc();
        model.addAttribute("grades", grades);
        Iterable<Room> rooms = roomRepository.findAll();
        model.addAttribute("rooms", rooms);
        return "principal/school-class-details";
    }

    @PostMapping(WebMvcConfig.addPath)
    public String add(SchoolClass schoolClass, Model model) {
        schoolClassRepository.save(schoolClass);
        return "redirect:" + extensions.getPrincipalUrls().schoolClasses();
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long schoolClassId, Model model
    ) {
        Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);
        if (optionalSchoolClass.isPresent()) {
            model.addAttribute("schoolClass", optionalSchoolClass.get());
            Iterable<SchoolYear> schoolYears = schoolYearRepository.findAllWithAll();
            model.addAttribute("schoolYears", schoolYears);
            Iterable<Grade> grades = gradeRepository.findByOrderByOrdinalAsc();
            model.addAttribute("grades", grades);
            Iterable<Room> rooms = roomRepository.findAll();
            model.addAttribute("rooms", rooms);
            return "principal/school-class-details";
        } else {
            return "";
        }
    }
    @PostMapping(WebMvcConfig.objectIdPathParam)
    public String detailsSubmit(
        @PathVariable(WebMvcConfig.objectIdParamName) Long schoolClassId, SchoolClass schoolClass, Model model
    ) {
        Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);
        if (optionalSchoolClass.isPresent()) {
            SchoolClass existingSchoolClass = optionalSchoolClass.get();
            schoolClassRepository.save(schoolClass);
            List<Student> changedStudents = new ArrayList<>();
            for (Student student : existingSchoolClass.getStudents()) {
                if (!schoolClass.getStudents().contains(student)) {
                    student.setSchoolClass(null);
                    changedStudents.add(student);
                }
            }
            for (Student student : schoolClass.getStudents()) {
                if (!existingSchoolClass.getStudents().contains(student)) {
                    student.setSchoolClass(existingSchoolClass);
                    changedStudents.add(student);
                }
            }
            studentRepository.saveAll(changedStudents);
            return "redirect:" + extensions.getPrincipalUrls().schoolClass(existingSchoolClass);
        } else {
            return "";
        }
    }

    @GetMapping(value = WebMvcConfig.objectIdPathParam + "/schedule", name = StudentCourseController.schedule)
    public String schedule(
        @PathVariable(WebMvcConfig.objectIdParamName) Long schoolClassId,
        Model model
    ) {
        Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);
        if (optionalSchoolClass.isPresent()) {
            SchoolClass existingSchoolClass = optionalSchoolClass.get();
            List<Lesson> weeklyLessons = StreamSupport
                .stream(lessonRepository.findAllBySchoolClass(existingSchoolClass, school.getTerm()).spliterator(), false)
                .collect(Collectors.toList());
            model.addAttribute("weeklyLessons", new WeeklyLessons(weeklyLessons));
            return "principal/school-class-schedule";
        } else {
            return "";
        }
    }
}
