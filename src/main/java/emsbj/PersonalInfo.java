package emsbj;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

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

    public String getAddress() {
        return address;
    }

    public PersonalInfo setAddress(String address) {
        this.address = address;
        return this;
    }
}
