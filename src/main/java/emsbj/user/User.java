package emsbj.user;

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
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String username;
    private String email;
    private String password;
    @CreatedDate
    private Instant createdOn;
    @LastModifiedDate
    private Instant updatedOn;
    @Embedded
    private PersonalInfo personalInfo;
    private Role role;
    private boolean active;
    @Transient
    private Collection<SimpleGrantedAuthority> grantedAuthorities;

    protected User() {
        this.active = true;
    }

    public User(String userName) {
        this.username = userName;
        this.role = Role.user;
        this.active = true;
        this.personalInfo = new PersonalInfo();
    }

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

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        grantedAuthorities = null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        return active;
    }

    public enum Role {
        anonymous, user, admin, principal, teacher, student, parent;
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
    }
}
