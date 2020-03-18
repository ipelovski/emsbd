package emsbj.user;

import emsbj.JournalPersistable;
import emsbj.PersonalInfo;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class User extends JournalPersistable implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String username;
    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String email;
    @NotNull
    @NotEmpty
    private String password;
    @CreatedDate
    private Instant createdOn;
    @LastModifiedDate
    private Instant updatedOn;
    @Embedded
    @NotNull
    private PersonalInfo personalInfo;
    @NotNull
    private Role role;
    @NotNull
    private Status status;
    @Transient
    private Collection<SimpleGrantedAuthority> grantedAuthorities;

    public User() {
        this.role = Role.user;
        this.status = Status.active;
        this.personalInfo = new PersonalInfo();
    }

    public User(String userName) {
        this();
        this.username = userName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        grantedAuthorities = null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (grantedAuthorities == null) {
            grantedAuthorities = Collections.singletonList(
                new SimpleGrantedAuthority(role.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == Status.active;
    }

    public enum Status {
        active, inactive, waitingApproval
    }

    public enum Role {
        anonymous, user, admin, principal, teacher, student;
        private final String name;
        private static final Map<String, Role> valuesMap = new HashMap<>();

        static {
            for (Role role : EnumSet.allOf(Role.class)) {
                valuesMap.put(role.name, role);
            }
        }

        Role() {
            this.name = "ROLE_" + name().toUpperCase();
        }

        public String getName() {
            return name;
        }

        public static Role from(String name) {
            return valuesMap.computeIfAbsent(name,
                roleName -> { throw new IllegalArgumentException("There is no value for " + roleName); });
        }

        public String includes(Role... includeRoles) {
            return Arrays.stream(includeRoles)
                .map(role -> this.getName() + " > " + role.getName())
                .collect(Collectors.joining(" and "));
        }

        public static String createHierarchy(Map<Role, Role[]> hierarchy) {
            return hierarchy.entrySet().stream()
                .map(roleEntry -> roleEntry.getKey().includes(roleEntry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
        }

        public static String createHierarchy(Role[]... rules) {
            Map<Role, Role[]> hierarchy = new LinkedHashMap<>(rules.length);
            for (int i = 0; i < rules.length; i++) {
                Role[] rule = rules[i];
                assert rule.length > 1;
                hierarchy.put(rule[0], Arrays.copyOfRange(rule, 1, rule.length));
            }
            return createHierarchy(hierarchy);
        }
    }
}
