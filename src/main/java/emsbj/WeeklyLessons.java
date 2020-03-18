package emsbj;

import emsbj.lesson.Lesson;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeeklyLessons {
    private int maxShift = 0;
    private List<Integer> shifts;
    private int maxOrdinal = 0;
    private List<Integer> ordinals;
    private List<DayOfWeek> days;
    private Map<DayOfWeek, Map<Integer, List<Lesson>>> weeklyLessons;
    private List<Lesson> lessons;

    public WeeklyLessons(List<Lesson> lessons) {
        Map<DayOfWeek, Map<Integer, List<Lesson>>> weeklyLessons =
            new LinkedHashMap<>(DayOfWeek.values().length);
        for (Lesson lesson : lessons) {
            Map<Integer, List<Lesson>> dailyLessons = weeklyLessons.computeIfAbsent(
                lesson.getWeeklySlot().getDay(), dayOfWeek -> new LinkedHashMap<>());
            List<Lesson> shiftLessons = dailyLessons.computeIfAbsent(
                lesson.getWeeklySlot().getShift(), shift -> {
                    setMaxShift(shift);
                    return new ArrayList<>();
                });
            shiftLessons.add(lesson);
            shiftLessons.sort(Comparator.comparingInt(aLesson -> aLesson.getWeeklySlot().getOrdinal()));
            setMaxOrdinal(shiftLessons.size());
        }
        this.lessons = lessons;
        this.weeklyLessons = weeklyLessons;
        this.days = new ArrayList<>(weeklyLessons.keySet());
        this.days.sort(Comparator.comparingInt(DayOfWeek::getValue));
        this.shifts = generateInts(maxShift);
        this.ordinals = generateInts(maxOrdinal);
    }

    public boolean hasLessons() {
        return lessons.size() > 0;
    }

    public int getTotalShifts() {
        return shifts.size();
    }

    public List<Integer> getShifts() {
        return shifts;
    }

    public int getTotalOrdinals() {
        return ordinals.size();
    }

    public List<Integer> getOrdinals() {
        return ordinals;
    }

    public int getTotalDays() {
        return days.size();
    }

    public List<DayOfWeek> getDays() {
        return days;
    }

    public Lesson getLesson(DayOfWeek day, int shift, int ordinal) {
        List<Lesson> lessons = weeklyLessons
            .getOrDefault(day, Collections.emptyMap())
            .getOrDefault(shift, Collections.emptyList());
        if (lessons.size() >= ordinal) {
            return lessons.get(ordinal - 1);
        } else {
            return null;
        }
    }

    private void setMaxShift(int shift) {
        if (maxShift < shift) {
            maxShift = shift;
        }
    }

    private void setMaxOrdinal(int ordinal) {
        if (maxOrdinal < ordinal) {
            maxOrdinal = ordinal;
        }
    }

    private List<Integer> generateInts(int maxValue) {
        List<Integer> integers = new ArrayList<>(maxValue);
        for (int i = 1; i <= maxValue; i++) {
            integers.add(i);
        }
        return integers;
    }
}
