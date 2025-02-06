package com.conferences.user_consumer.service;

import com.conferences.common.entity.Conference;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserConference;
import com.conferences.user_consumer.repository.UserConferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class UserConferenceService {
    private final UserConferenceRepository userConferenceRepository;

    @Autowired
    public UserConferenceService(UserConferenceRepository userConferenceRepository){
        this.userConferenceRepository = userConferenceRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserConference> findUserConferenceByUserAndConference(User user, Conference conference){
       return userConferenceRepository.findUserConferenceByUserAndConference(user, conference);
    }

    @Transactional(readOnly = true)
    public List<UserConference> findConferencesByUser(User user){
        return userConferenceRepository.findByUser(user);
    }
}
