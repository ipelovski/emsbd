package emsbj.admin;

import emsbj.Extensions;
import emsbj.Grade;
import emsbj.GradeRepository;
import emsbj.Room;
import emsbj.RoomRepository;
import emsbj.SchoolClass;
import emsbj.SchoolClassRepository;
import emsbj.SchoolYear;
import emsbj.SchoolYearRepository;
import emsbj.Teacher;
import emsbj.TeacherRepository;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/school-classes")
public class AdminSchoolClassController implements LocalizedController {
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private Extensions extensions;

    @GetMapping
    public String list(Model model) {
        Iterable<SchoolClass> schoolClasses = schoolClassRepository.findAll();
        model.addAttribute("schoolClasses", schoolClasses);
        return "admin/school-classes.html";
    }

    @GetMapping("add")
    public String add(Model model) {
        model.addAttribute("schoolClass", new SchoolClass());
        int currentYear = LocalDate.now().getYear();
        Iterable<SchoolYear> schoolYears = schoolYearRepository.findByBeginYearGreaterThanEqual(currentYear);
        model.addAttribute("schoolYears", schoolYears);
        Iterable<Grade> grades = gradeRepository.findByOrderByOrdinalAsc();
        model.addAttribute("grades", grades);
        Iterable<Teacher> teachers = teacherRepository.findAll();
        model.addAttribute("teachers", teachers);
        Iterable<Room> rooms = roomRepository.findAll();
        model.addAttribute("rooms", rooms);
        return "admin/school-class-details.html";
    }

    @PostMapping("add")
    public String add(SchoolClass schoolClass, Model model) {
        schoolClassRepository.save(schoolClass);
        return "redirect:" + extensions.getAdminUrls().schoolClasses();
    }
}
