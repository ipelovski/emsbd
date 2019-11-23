package emsbd;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class PersonalInfo {
    @Id
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    protected PersonalInfo() {

    }

    public PersonalInfo(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
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
