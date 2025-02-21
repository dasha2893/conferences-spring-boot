package com.conferences.conf_consumer.rest;

import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.conf_consumer.service.ConferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceRestConroller {
    private static final Logger log = LoggerFactory.getLogger(ConferenceRestConroller.class);
    private final ConferenceService conferenceService;

    public ConferenceRestConroller(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping("/getByTitle/{title}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConferenceDTO> getConferenceByTitle(@PathVariable String title) {
        log.info("[ConferenceRestConroller] getConferenceByTitle: {}", title);
        return conferenceService.getConferenceByTitle(title).map(ConferenceDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/getById")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConferenceDTO> getConferenceById(@RequestBody Long id) {
        log.info("[ConferenceRestConroller] getConferenceById: {}", id);
        return conferenceService.getConferenceById(id).map(ConferenceDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConferenceDTO>> getAll() {
        log.info("[ConferenceRestConroller] getAll");
        List<ConferenceDTO> conferences = Optional.ofNullable(conferenceService.getConferencesAfterYesterday())
                .orElse(Collections.emptyList())
                .stream()
                .map(ConferenceDTO::new)
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(conferences)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(conferences);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole(T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    public void deleteConference(@PathVariable long id) {
        log.info("[ConferenceRestConroller] deleteConference: {}", id);
        conferenceService.deleteConference(id);
    }
}
