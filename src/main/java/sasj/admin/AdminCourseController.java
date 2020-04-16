package sasj.admin;

import sasj.course.Course;
import sasj.course.CourseRepository;
import sasj.Extensions;
import sasj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController implements AuthorizedController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private Extensions extensions;

    // TODO this lists all courses regardless of school year.
    /**
     * Lists all courses entered in the system.
     * These are the courses for all school years, all school classes,
     * and all subjects.
     * @param model The object where the view model is kept.
     * @return The name of the template for this view.
     */
    @GetMapping
    public String list(Model model) {
        Iterable<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "admin/courses";
    }
}
