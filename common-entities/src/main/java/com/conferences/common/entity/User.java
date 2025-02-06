package com.conferences.common.entity;



import com.conferences.common.model.EducationStatus;
import com.conferences.common.model.EducationType;
import com.conferences.common.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable, UserDetails {
    private static final long serialVersionUID = -9171559062981154228L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String userLastName;

    @Column(name = "THIRD_NAME")
    private String userThirdName;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    @Temporal(value = TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @Column(name = "GENDER", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "ORGANISATION")
    private String organization;

    @Column(name = "UNIVERSITY")
    private String university;

    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "EDUCATION_TYPE")
    @Enumerated(EnumType.STRING)
    private EducationType educationType;

    @Column(name = "EDUCATION_STATUS")
    @Enumerated(EnumType.STRING)
    private EducationStatus educationStatus;

    @Column(name = "GRADUATION_YEAR")
    private String graduationYear;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserConference> userConferences;

    public User(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public User(String userName,
                String userLastName,
                String userThirdName,
                String password,
                Date dateOfBirth,
                Gender gender,
                String email) {
        this.firstName = userName;
        this.userLastName = userLastName;
        this.userThirdName = userThirdName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + firstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", userThirdName='" + userThirdName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", organization='" + organization + '\'' +
                ", university='" + university + '\'' +
                ", department='" + department + '\'' +
                ", graduationYear='" + graduationYear + '\'' +
                ", educationStatus=" + educationStatus +
                ", educationType=" + educationType +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userRoles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
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
        return true;
    }
}
