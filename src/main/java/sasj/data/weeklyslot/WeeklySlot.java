package sasj.data.weeklyslot;

import sasj.data.JournalPersistable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class WeeklySlot extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private DayOfWeek day;
    private Integer shift;
    private Integer ordinal;
    private LocalTime begin;
    private LocalTime end;

    protected WeeklySlot() {

    }

    public WeeklySlot(DayOfWeek day, Integer shift, Integer ordinal, LocalTime begin, LocalTime end) {
        this.day = day;
        this.shift = shift;
        this.ordinal = ordinal;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public Long getId() {
        return id;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public Integer getShift() {
        return shift;
    }

    public void setShift(Integer shift) {
        this.shift = shift;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public LocalTime getBegin() {
        return begin;
    }

    public void setBegin(LocalTime begin) {
        this.begin = begin;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("Day: %s, shift: %d, lesson: %d, begins: %s, ends: %s",
            day, shift, ordinal, begin, end);
    }

    public int getValue() {
        return day.getValue() * 24 * 60 + begin.toSecondOfDay() / 60;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklySlot that = (WeeklySlot) o;
        if (id != null && that.id != null) {
            return id.equals(that.id);
        } else {
            return getValue() == that.getValue();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
