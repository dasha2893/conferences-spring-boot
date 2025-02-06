package com.conferences.common.entity;


import com.conferences.common.model.ReportStatus;
import com.conferences.common.model.UserConferenceRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Table(name = "USERS_CONFERENCES")
@Getter
@Setter
public class UserConference implements Serializable {
    private static final long serialVersionUID = 1740027978400292596L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_CONFERENCE_ID", nullable = false)
    private Long userConferenceId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conference;

    @Column(name = "REPORT_FILE_PATH")
    private String reportPath;

    @Column(name = "REVIEW_FILE_PATH")
    private String reviewPath;

    @Column(name = "REPORT_STATUS")
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private UserConferenceRole role;

    @Override
    public String toString() {
        return "UserConference{" +
                "userConferenceId=" + userConferenceId +
                ", reportStatus=" + reportStatus +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConference userConference = (UserConference) o;
        return Objects.equals(userConferenceId, userConference.userConferenceId);
    }

    public int hashCode() {
        return Objects.hashCode(userConferenceId);
    }
}
