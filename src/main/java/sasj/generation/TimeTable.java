package sasj.generation;

import sasj.weeklyslot.WeeklySlot;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class TimeTable<O, T> {
    private static final int modifier = 10;
    private O owner;
    private List<WeeklySlot> weeklySlots;
    private LinkedHashMap<WeeklySlot, List<T>> occupiedWeeklySlots;

    TimeTable(O owner, List<WeeklySlot> weeklySlots) {
        this.owner = owner;
        this.weeklySlots = new ArrayList<>(weeklySlots);
        this.weeklySlots.sort(Comparator.comparingInt(WeeklySlot::getValue));
        this.occupiedWeeklySlots = new LinkedHashMap<>(weeklySlots.size());
        for (WeeklySlot weeklySlot : weeklySlots) {
            this.occupiedWeeklySlots.put(weeklySlot, new LinkedList<>());
        }
    }

    O getOwner() {
        return owner;
    }

    List<WeeklySlot> getWeeklySlots() {
        return weeklySlots;
    }

    boolean isOccupied(WeeklySlot weeklySlot) {
        return occupiedWeeklySlots.get(weeklySlot).size() > 0;
    }

    List<T> getSlotContent(WeeklySlot weeklySlot) {
        return occupiedWeeklySlots.get(weeklySlot);
    }

    void occupy(WeeklySlot weeklySlot, T value) {
        occupiedWeeklySlots.get(weeklySlot).add(value);
    }

    boolean move(int index, int places) {
        int nextIndex = (index + places) % weeklySlots.size();
        List<T> firstContent = occupiedWeeklySlots.get(weeklySlots.get(index));
        List<T> secondContent = occupiedWeeklySlots.get(weeklySlots.get(nextIndex));
        if (firstContent.size() > 0) {
            T value = firstContent.get(0);
            firstContent.remove(0);
            secondContent.add(value);
            return true;
        } else {
            return false;
        }
    }

    boolean swap(int index, int places) {
        int nextIndex = (index + places) % weeklySlots.size();
        List<T> firstContent = occupiedWeeklySlots.get(weeklySlots.get(index));
        List<T> secondContent = occupiedWeeklySlots.get(weeklySlots.get(nextIndex));
        if (firstContent.size() > 0 && secondContent.size() > 0) {
            T firstValue = firstContent.get(0);
            firstContent.remove(0);
            T secondValue = secondContent.get(0);
            secondContent.remove(0);
            firstContent.add(secondValue);
            secondContent.add(firstValue);
            return true;
        } else {
            return false;
        }
    }

    int score() {
        int score = 0;
        for (WeeklySlot weeklySlot : weeklySlots) {
            List<T> content = occupiedWeeklySlots.get(weeklySlot);
            score += (content.size() - 1) * modifier;
        }
        return score;
    }

    List<DayOfWeek> getDays() {
        return weeklySlots.stream()
            .map(WeeklySlot::getDay)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    List<WeeklySlot> getDailySlots(DayOfWeek day) {
        return weeklySlots.stream()
            .filter(weeklySlot -> weeklySlot.getDay() == day)
            .collect(Collectors.toList());
    }
}
