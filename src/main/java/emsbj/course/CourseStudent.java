package emsbj.course;

import emsbj.absence.Absence;
import emsbj.student.Student;
import emsbj.mark.Mark;
import emsbj.note.Note;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CourseStudent {
    private Long id;
    private Integer number;
    private String name;
    private List<Mark> marks;
    private Double averageMark;
    private List<String> notes;
    private double absences;
    private Course course;
    private Student student;

    public CourseStudent(Course course, Student student) {
        this.id = student.getId();
        this.number = student.getNumber();
        this.name = student.getUser().getPersonalInfo().getName();
        this.marks = student.getMarks().stream()
            .filter(mark -> Objects.equals(mark.getSubject().getId(), course.getSubject().getId()))
            .collect(Collectors.toList());
        if (this.marks.size() > 0) {
            this.averageMark = Math.round(
                this.marks.stream()
                    .map(Mark::getRawScore)
                    .reduce(0, Integer::sum)
                    / (double) this.marks.size())
                / 100.0;
        } else {
            this.averageMark = null;
        }
        this.notes = student.getNotes().stream()
            .map(Note::getText)
            .collect(Collectors.toList());
        this.absences = student.getAbsences().stream()
            .map(Absence::getValue)
            .reduce(0.0, Double::sum);
        this.course = course;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public Double getAverageMark() {
        return averageMark;
    }

    public List<String> getNotes() {
        return notes;
    }

    public double getAbsences() {
        return absences;
    }

    public Course getCourse() {
        return course;
    }

    public Student getStudent() {
        return student;
    }
}
