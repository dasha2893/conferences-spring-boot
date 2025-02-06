package com.conferences.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "CONFERENCES")
@Getter
@Setter
public class Conference implements Serializable {
    private static final long serialVersionUID = 5718761406143211625L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFERENCE_ID", nullable = false)
    private Long conferenceId;

    @Column(name = "TITLE", nullable = false, unique = true)
    private String title;

    @Column(name = "SHORT_DESCRIPTION", nullable = false)
    private String shortDescription;

    @Column(name = "FULL_DESCRIPTION", nullable = false)
    private String fullDescription;

    @Column(name = "CONF_LOCATION", nullable = false)
    private String location;

    @Column(name = "ORGANIZER", nullable = false)
    private String organizer;

    @Column(name = "CONTACTS", nullable = false)
    private String contacts;

    @Column(name = "START_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "END_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Column(name = "START_REGISTRATION", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startRegistration;

    @Column(name = "END_REGISTRATION", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endRegistration;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "conference", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserConference> userConferences;

    public Conference(String title,
                      String shortDescription,
                      String fullDescription,
                      String location,
                      String organizer,
                      String contacts,
                      Date startDate,
                      Date endDate,
                      Date startRegistration,
                      Date endRegistration,
                      Long createdBy) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.location = location;
        this.organizer = organizer;
        this.contacts = contacts;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startRegistration = startRegistration;
        this.endRegistration = endRegistration;
        this.createdBy = createdBy;
    }

    public Conference(Long conferenceId){
        this.conferenceId = conferenceId;
    }

    public Conference(Long conferenceId,
                      String title,
                      String shortDescription,
                      String fullDescription,
                      String location,
                      String organizer,
                      String contacts,
                      Date startDate,
                      Date endDate,
                      Date startRegistration,
                      Date endRegistration,
                      Long createdBy) {
        this.conferenceId = conferenceId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.location = location;
        this.organizer = organizer;
        this.contacts = contacts;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startRegistration = startRegistration;
        this.endRegistration = endRegistration;
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Conference{" +
                "shortDescription='" + shortDescription + '\'' +
                ", title='" + title + '\'' +
                ", conferenceId=" + conferenceId +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conference conference = (Conference) o;
        return Objects.equals(conferenceId, conference.conferenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(conferenceId);
    }
}
