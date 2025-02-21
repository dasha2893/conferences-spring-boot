package com.conferences.spring.service;

import com.conferences.common.entity.Conference;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserConference;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.spring.service.rest.UserConferenceRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserConferenceService {
    private final UserConferenceRestService userConferenceRestService;

    @Autowired
    public UserConferenceService(UserConferenceRestService userConferenceRestService){
        this.userConferenceRestService = userConferenceRestService;
    }

    public List<ConferenceDTO> getConferencesByUser(String userEmail){
        List<UserConferenceDTO> userConferences = userConferenceRestService.getUserConferences(userEmail);

        if(CollectionUtils.isEmpty(userConferences)){
            return Collections.emptyList();
        }

        return userConferences.stream().map(UserConferenceDTO::getConferenceDTO).collect(Collectors.toList());
    }

    public Optional<UserConferenceDTO> findUserConferenceByUserAndConference(ConferenceDTO conferenceDTO, String userEmail){
        return userConferenceRestService.findUserConferenceByUserAndConference(conferenceDTO, userEmail);
    }
}
