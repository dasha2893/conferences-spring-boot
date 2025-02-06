package com.conferences.user_consumer.repository;

import com.conferences.common.entity.Conference;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserConference;
import com.conferences.common.service.dto.UserConferenceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConferenceRepository extends JpaRepository<UserConference, Long> {
    Optional<UserConference> findUserConferenceByUserAndConference(User user, Conference conference);
    List<UserConference> findByUser(User user);
}
