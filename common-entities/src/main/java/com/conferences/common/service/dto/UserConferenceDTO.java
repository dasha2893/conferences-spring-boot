package com.conferences.common.service.dto;

import com.conferences.common.entity.UserConference;
import com.conferences.common.model.ReportStatus;
import com.conferences.common.model.UserConferenceRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class UserConferenceDTO {

    private Long userConferenceId;

    private UserDTO userDTO;

    private ConferenceDTO conferenceDTO;

    private String reportPath;

    private String reviewPath;

    private ReportStatus reportStatus;

    private UserConferenceRole role;

    public UserConferenceDTO(UserConference userConference){
        this.userConferenceId = userConference.getUserConferenceId();
        this.userDTO = new UserDTO(userConference.getUser());
        this.conferenceDTO = new ConferenceDTO(userConference.getConference());
        this.reportPath = userConference.getReportPath();
        this.reviewPath = userConference.getReviewPath();
        this.reportStatus = userConference.getReportStatus();
        this.role = userConference.getRole();
    }

    @Override
    public String toString() {
        return "UserConferenceDTO{" +
                "userConferenceId=" + userConferenceId +
                ", userDTO=" + userDTO +
                ", conferenceDTO=" + conferenceDTO +
                ", reportPath='" + reportPath + '\'' +
                ", reviewPath='" + reviewPath + '\'' +
                ", reportStatus=" + reportStatus +
                ", role=" + role +
                '}';
    }
}
