package emsbj;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Embeddable
public class PersonalInfo {
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String middleName;
    @Size(max = 50)
    private String lastName;
    @Size(max = 200)
    private String address;
    private LocalDate bornAt;
    @OneToOne(fetch = FetchType.LAZY)
    private Blob picture;

    public PersonalInfo() {

    }

    public String getFirstName() {
        return firstName;
    }

    public PersonalInfo setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public PersonalInfo setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonalInfo setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getName() {
        return String.format("%s %s %s", firstName, middleName, lastName);
    }

    public String getAddress() {
        return address;
    }

    public PersonalInfo setAddress(String address) {
        this.address = address;
        return this;
    }

    public LocalDate getBornAt() {
        return bornAt;
    }

    public PersonalInfo setBornAt(LocalDate bornAt) {
        this.bornAt = bornAt;
        return this;
    }

    public Optional<Integer> getAge() {
        if (bornAt != null) {
            return Optional.of(Period.between(LocalDate.now(), bornAt).getYears());
        } else {
            return Optional.empty();
        }
    }

    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }
}
