package sasj.grade;

import sasj.JournalPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Grade extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    @NotNull
    private Integer ordinal;
    @Column(unique = true)
    private String name;

    protected Grade() {

    }

    public Grade(Integer name) {
        this.name = name.toString();
    }

    public Grade(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
