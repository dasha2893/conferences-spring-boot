package com.conferences.conf_consumer.repository;

import com.conferences.common.entity.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    Optional<Conference> findByTitle(String title);
    List<Conference> findByStartDateGreaterThan(Date startDate);
    List<Conference> findByCreatedBy(Long userId);
    void deleteById(Long id);
}
