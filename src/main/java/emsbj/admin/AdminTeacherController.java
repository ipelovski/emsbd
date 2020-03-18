package emsbj.admin;

import emsbj.Extensions;
import emsbj.teacher.Teacher;
import emsbj.teacher.FormMaster;
import emsbj.teacher.TeacherRepository;
import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/admin/teachers")
public class AdminTeacherController implements AuthorizedController {
    public static final String selectFormMasterFragment = "selectFormMasterFragment";
    public static final String teacherList = "teacherList";
    @Autowired
    private Extensions extensions;
    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public String list(Model model) {
        Iterable<Teacher> teachers = teacherRepository.findAll();
        model.addAttribute("teachers", teachers);
        return "/admin/teachers.html";
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long teacherId, Model model
    ) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
        if (optionalTeacher.isPresent()) {
            model.addAttribute("user", optionalTeacher.get().getUser());
            return "admin/user-details.html";
        } else {
            return "";
        }
    }

    @GetMapping(value = "/select-form-master-fragment", name = selectFormMasterFragment)
    public String selectFormMasterFragment(
        @RequestParam("id") String id,
        @RequestParam("mode") String mode, Model model
    ) {
        Iterable<FormMaster> teacherInfos = teacherRepository.findAllOrderByFormMaster(
            new Sort(Sort.Direction.ASC,
                "schoolClasses", "firstName", "middleName", "lastName")
        );
        Iterable<Teacher> teachers = StreamSupport
            .stream(teacherInfos.spliterator(), false)
            .map(FormMaster::getTeacher)
            .collect(Collectors.toList());
        model.addAttribute("id", id);
        model.addAttribute("teachers", teachers);
        model.addAttribute("mode", mode);
        return "fragments/teacher-list::teacherList";
    }

    @GetMapping(value = "/teacher-list", name = teacherList)
    public String teacherList(
        @RequestParam("id") String id,
        @RequestParam("mode") String mode,
        @RequestParam(value = "teacherIds", required = false) List<Long> teacherIds,
        Model model
    ) {
        Iterable<Teacher> teachers;
        if (teacherIds != null) {
            teachers = teacherRepository.findAllById(teacherIds);
        } else {
            teachers = teacherRepository.findAll();
        }
        model.addAttribute("teachers", teachers);
        model.addAttribute("id", id);
        model.addAttribute("mode", mode);
        return "fragments/teacher-list::teacherList";
    }
}
