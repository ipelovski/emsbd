package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ordinal", "name"})})
public class Grade implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer ordinal;
    private String name;

    protected Grade() {

    }

    public Grade(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Grade(Integer ordinal, String name) {
        this.ordinal = ordinal;
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
