package sasj.data.room;

import sasj.data.JournalPersistable;
import sasj.data.schoolclass.SchoolClass;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Room extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer floor;
    private String name;
    @OneToMany(mappedBy = "classRoom")
    private List<SchoolClass> classRoomOf;

    @Override
    public Long getId() {
        return id;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SchoolClass> getClassRoomOf() {
        return classRoomOf;
    }
}
