package com.conferences.user_consumer.rest;

import com.conferences.common.entity.Conference;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserConference;
import com.conferences.common.service.dto.UserConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.user_consumer.service.UserConferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user-conferences")
public class UserConferenceRestController {
    private final UserConferenceService userConferenceService;

    @Autowired
    public UserConferenceRestController(UserConferenceService userConferenceService){
        this.userConferenceService = userConferenceService;
    }

    @PostMapping("/getByUser")
    public ResponseEntity<List<UserConferenceDTO>> getUserConferences(@RequestBody Map<String, Object>  requestedBody){
        log.info("getUserConferences JSON: {}", requestedBody);

        Long userId = Long.parseLong((String) requestedBody.get("userId"));
        String email = (String) requestedBody.get("email");

        List<UserConference> userConferences = userConferenceService.findConferencesByUser(new User(userId, email));

        if(CollectionUtils.isEmpty(userConferences)){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(userConferences.stream().map(UserConferenceDTO::new).collect(Collectors.toList()));
    }

    @PostMapping("/getByUserAndConference")
    public ResponseEntity<UserConferenceDTO> findUserConferenceByUserAndConference(@RequestBody Map<String, Object>  requestedBody){
        log.info("findUserConferenceByUserAndConference JSON: {}", requestedBody);

        Long userId = Long.parseLong((String) requestedBody.get("userId"));
        String email = (String) requestedBody.get("email");
        Long conferenceId = Long.parseLong((String) requestedBody.get("conferenceId"));

        Optional<UserConference> userConference = userConferenceService.findUserConferenceByUserAndConference(new User(userId, email), new Conference(conferenceId));

        return userConference.map(conference -> ResponseEntity.ok(new UserConferenceDTO(conference))).orElseGet(() -> ResponseEntity.noContent().build());

    }
}
