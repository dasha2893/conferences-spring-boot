package com.conferences.conf_consumer.rest;

import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.conf_consumer.service.ConferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceRestConroller {
    private final ConferenceService conferenceService;

    public ConferenceRestConroller(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping("/getByTitle/{title}")
    public ResponseEntity<ConferenceDTO> getConferenceByTitle(@PathVariable String title) {
        return conferenceService.getConferenceByTitle(title).map(ConferenceDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/getById")
    public ResponseEntity<ConferenceDTO> getConferenceById(@RequestBody Long id) {
        return conferenceService.getConferenceById(id).map(ConferenceDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ConferenceDTO>> getAll() {
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
    public void deleteConference(@PathVariable long id) {
        conferenceService.deleteConference(id);
    }
}
