package emsbj.mark;

import emsbj.Grade;
import emsbj.GradeRepository;
import emsbj.SchoolYear;
import emsbj.SchoolYearRepository;
import emsbj.Subject;
import emsbj.SubjectRepository;
import emsbj.Term;
import emsbj.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping("/marks")
public class MarkController {
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private MarkRepository markRepository;

    @RequestMapping(method = RequestMethod.GET,
        produces = "application/json",
        value = "/year/{year}/term/{term}/subject/{subject}/grade/{gradeName}")
    @ResponseBody
    public List<Mark> getMarksPerSubjectAndGrade(
            int year,
            @PathParam("term") String termName,
            @PathParam("subject") String subjectName,
            @PathParam("gradeName") String gradeName) {
        SchoolYear schoolYear = schoolYearRepository
            .findByBeginYear(year)
            .orElseThrow(() -> new IllegalArgumentException("year"));
        Term term = termRepository
            .findBySchoolYearAndName(schoolYear, termName)
            .orElseThrow(() -> new IllegalArgumentException("term name"));
        Grade grade = gradeRepository
            .findByName(gradeName)
            .orElseThrow(() -> new IllegalArgumentException("grade name"));
        Subject subject = subjectRepository
            .findByNameAndGrade(subjectName, grade)
            .orElseThrow(() -> new IllegalArgumentException("subject name"));
        List<Mark> marks = markRepository.findBySubjectAndStudentGrade(subject, grade);
        return marks;
    }
}
