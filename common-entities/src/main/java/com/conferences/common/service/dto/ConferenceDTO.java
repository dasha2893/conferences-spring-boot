package com.conferences.common.service.dto;


import com.conferences.common.entity.Conference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class ConferenceDTO {

    private Long id;

    @NotBlank(message = "{conference.field.notBlank}")
    @Size(min = 3, max = 256, message = "Title must be between 3 and 256 characters long.")
    private String title;

    @NotBlank(message = "{conference.field.notBlank}")
    private String shortDescription;

    private String fullDescription;

    @NotBlank(message = "{conference.field.notBlank}")
    private String location;

    @NotBlank(message = "{conference.field.notBlank}")
    private String organizer;

    @NotBlank(message = "{conference.field.notBlank}")
    private String contacts;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startRegistration;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endRegistration;

    private Long createdBy;

    public ConferenceDTO(Conference conference) {
        this.id = conference.getConferenceId();
        this.title = conference.getTitle();
        this.shortDescription = conference.getShortDescription();
        this.fullDescription = conference.getFullDescription();
        this.location = conference.getLocation();
        this.organizer = conference.getOrganizer();
        this.contacts = conference.getContacts();
        this.startDate = conference.getStartDate();
        this.endDate = conference.getEndDate();
        this.startRegistration = conference.getStartRegistration();
        this.endRegistration = conference.getEndRegistration();
        this.createdBy = conference.getCreatedBy();
    }

    public ConferenceDTO(Long id){
        this.id = id;
    }


    @Override
    public String toString() {
        return "ConferenceDTO{" +
                "conferenceId=" + id +
                ", title='" + title + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", location='" + location + '\'' +
                ", organizer='" + organizer + '\'' +
                ", contacts='" + contacts + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", startRegistration=" + startRegistration +
                ", endRegistration=" + endRegistration +
                ", createdBy=" + createdBy +
                '}';
    }
}
