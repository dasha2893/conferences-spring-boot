package com.conferences.user_consumer.rest;

import com.conferences.common.entity.Conference;
import com.conferences.common.entity.User;
import com.conferences.common.entity.UserConference;
import com.conferences.common.service.dto.UserConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.user_consumer.service.UserConferenceService;
import com.conferences.user_consumer.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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
    private final UserService userService;

    @Autowired
    public UserConferenceRestController(UserConferenceService userConferenceService,
                                        UserService userService){
        this.userConferenceService = userConferenceService;
        this.userService = userService;
    }

    @PostMapping("/getByUser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserConferenceDTO>> getUserConferences(@RequestBody Map<String, Object>  requestedBody){
        log.info("getUserConferences JSON: {}", requestedBody);

        String email = (String) requestedBody.get("email");

        if(!StringUtils.hasLength(email)){
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isPresent()){
            List<UserConference> userConferences = userConferenceService.findConferencesByUser(userOpt.get());

            if(CollectionUtils.isEmpty(userConferences)){
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(userConferences.stream().map(UserConferenceDTO::new).collect(Collectors.toList()));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/getByUserAndConference")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserConferenceDTO> findUserConferenceByUserAndConference(@RequestBody Map<String, Object>  requestedBody){
        log.info("findUserConferenceByUserAndConference JSON: {}", requestedBody);

        String email = (String) requestedBody.get("email");
        Long conferenceId = Long.parseLong(String.valueOf(requestedBody.get("conferenceId")));

        if(!StringUtils.hasLength(email)){
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isPresent()){
            Optional<UserConference> userConferenceOpt = userConferenceService.findUserConferenceByUserAndConference(userOpt.get(), new Conference(conferenceId));
            if(userConferenceOpt.isPresent()){
                UserConferenceDTO userConferenceDTO = new UserConferenceDTO(userConferenceOpt.get());
                log.debug("userConferenceDTO: {}", userConferenceDTO);
                return ResponseEntity.ok(userConferenceDTO);
            }

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }
}
