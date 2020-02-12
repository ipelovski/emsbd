package emsbj.mark;

import emsbj.GradeRepository;
import emsbj.SchoolClass;
import emsbj.SchoolClassRepository;
import emsbj.SchoolYear;
import emsbj.SchoolYearRepository;
import emsbj.Subject;
import emsbj.SubjectRepository;
import emsbj.Term;
import emsbj.TermRepository;
import emsbj.controller.LocalizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping("/marks")
public class MarkController implements LocalizedController {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private MarkRepository markRepository;

    @RequestMapping(method = RequestMethod.GET,
        produces = "application/json",
        value = "/year/{year}/term/{term}/subject/{subject}/school-class/{schoolClassId}")
    @ResponseBody
    public List<Mark> getMarksPerSubjectAndGrade(
            int year,
            @PathParam("term") String termName,
            @PathParam("subject") String subjectName,
            @PathParam("schoolClassId") Long schoolClassId) {
        SchoolYear schoolYear = schoolYearRepository
            .findByBeginYear(year)
            .orElseThrow(() -> new IllegalArgumentException("year"));
        Term term = termRepository
            .findBySchoolYearAndName(schoolYear, termName)
            .orElseThrow(() -> new IllegalArgumentException("term name"));
        SchoolClass schoolClass = schoolClassRepository
            .findById(schoolClassId)
            .orElseThrow(() -> new IllegalArgumentException("school class id"));
        Subject subject = subjectRepository
            .findByNameAndGrade(subjectName, schoolClass.getGrade())
            .orElseThrow(() -> new IllegalArgumentException("subject name"));
        List<Mark> marks = markRepository.findBySubjectAndStudentSchoolClass(subject, schoolClass);
        return marks;
    }
}
