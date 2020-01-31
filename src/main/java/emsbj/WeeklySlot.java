package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
public class WeeklySlot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private DayOfWeek day;
    private int shift;
    private int ordinal;
    private LocalTime begin;
    private LocalTime end;

}
