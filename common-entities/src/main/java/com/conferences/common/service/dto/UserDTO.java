package com.conferences.common.service.dto;

import com.conferences.common.model.EducationStatus;
import com.conferences.common.model.EducationType;
import com.conferences.common.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserRole;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long id;

    @Size(max=50, message = "Max FirstName length 50")
    private String firstName;

    @Size(max = 50, message = "Max LastName length 50")
    private String lastName;

    @Size(max = 50, message = "Max ThirdName length 50")
    private String thirdName;

    @NotBlank(message = "{user.password.notBlank}")
    @Size(min = 5, max = 199, message = "Password must be between 5 and 199 characters long.")
    private String password;

    @Temporal(value = TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private Gender gender;

    @Size(max = 20, message = "Max Phone length 20")
    private String phone;

    @NotBlank(message = "{user.email.notBlank}")
    @Email(message = "{user.email.incorrect}")
    @Size(min = 5, max = 254, message = "Email must be between 5 and 254 characters long.")
    private String email;

    private String organization;

    private String university;

    private String department;

    private EducationType educationType;

    private EducationStatus educationStatus;

    @Size(max = 4, message = "Max GraduationYear length 4")
    private String graduationYear;

    private List<RoleDTO> rolesDTO;

    private List<ConferenceDTO> conferencesDTO;


    public UserDTO(User user) {
        this.id = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getUserLastName();
        this.thirdName = user.getUserThirdName();
        this.password = user.getPassword();
        this.dateOfBirth = user.getDateOfBirth();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.organization = user.getOrganization();
        this.university = user.getUniversity();
        this.department = user.getDepartment();
        this.educationType = user.getEducationType();
        this.educationStatus = user.getEducationStatus();
        this.graduationYear = user.getGraduationYear();
        this.rolesDTO = user.getUserRoles().stream().map(UserRole::getRole).map(RoleDTO::new).collect(Collectors.toList());
    }

    public UserDTO(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", thirdName='" + thirdName + '\'' +
                ", password='" + password + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", organization='" + organization + '\'' +
                ", university='" + university + '\'' +
                ", department='" + department + '\'' +
                ", educationType=" + educationType +
                ", educationStatus=" + educationStatus +
                ", graduationYear='" + graduationYear + '\'' +
                '}';
    }


}
