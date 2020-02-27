package emsbj;

public class Lesson {
    private Course course;
    private WeeklySlot weeklySlot;

    public Lesson(Course course, WeeklySlot weeklySlot) {
        this.course = course;
        this.weeklySlot = weeklySlot;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public WeeklySlot getWeeklySlot() {
        return weeklySlot;
    }

    public void setWeeklySlot(WeeklySlot weeklySlot) {
        this.weeklySlot = weeklySlot;
    }
}
