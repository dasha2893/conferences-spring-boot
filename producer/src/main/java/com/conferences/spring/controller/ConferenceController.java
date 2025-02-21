package com.conferences.spring.controller;

import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.spring.exception.NoSuchConferenceException;
import com.conferences.spring.service.ConferenceService;
import com.conferences.spring.service.UserConferenceService;
import com.conferences.spring.service.UserService;
import com.conferences.spring.service.kafka.DataSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ConferenceController {

    private final ConferenceService conferenceService;
    private final UserConferenceService userConferenceService;
    private final DataSender conferenceCreateSender;
    private final DataSender conferenceUpdateSender;
    private final DataSender conferenceDeleteSender;
    private final UserService userService;

    @Value("${msg.addconference.title}")
    private String newConferenceTitle;
    @Value("${msg.conference.profile.title}")
    private String profileTitle;
    @Value("${msg.updateconference.title}")
    private String updateConferenceTitle;
    @Value("${msg.userconference.title}")
    private String userConferenceTitle;
    @Value("${msg.conferences.title}")
    private String conferencesTitle;

    @Autowired
    public ConferenceController(ConferenceService conferenceService,
                                UserConferenceService userConferenceService,
                                @Qualifier("conferenceCreateSender") DataSender conferenceCreateSender,
                                @Qualifier("conferenceUpdateSender") DataSender conferenceUpdateSender,
                                @Qualifier("conferenceDeleteSender") DataSender conferenceDeleteSender, UserService userService) {
        this.conferenceService = conferenceService;
        this.userConferenceService = userConferenceService;
        this.conferenceCreateSender = conferenceCreateSender;
        this.conferenceUpdateSender = conferenceUpdateSender;
        this.conferenceDeleteSender = conferenceDeleteSender;
        this.userService = userService;
    }

    @PreAuthorize("hasRole(T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @GetMapping(value = "/conferences/add")
    public String addConference(@AuthenticationPrincipal UserDetails userDetails,
                                Model model,
                                HttpServletResponse response) {
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");
        model.addAttribute("title", newConferenceTitle);

        ConferenceDTO conferenceDTO = new ConferenceDTO();
        model.addAttribute("conference", conferenceDTO);

        if(userDetails != null){
          model.addAttribute("user", new UserDTO(userDetails.getUsername()));
        }

        return "add-conference";
    }

    @PreAuthorize("hasRole(T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @PostMapping(value = "/conferences/add")
    public String addConference(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("conference") ConferenceDTO conferenceDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> String.format(fieldError.getDefaultMessage(), fieldError.getField()))
                    .collect(Collectors.toList());
            model.addAttribute("errorMessage", errorMessages);
            return "add-conference";
        }

        Optional<ConferenceDTO> conference = conferenceService.getConferenceByTitle(conferenceDTO.getTitle(), userDetails.getUsername());
        if (conference.isPresent()) {
            model.addAttribute("errorMessage", "The conference already exists");
            return "add-conference";
        }

        UserDTO userDTO = userService.getUserByEmail(userDetails.getUsername());
        conferenceDTO.setCreatedBy(userDTO.getId());

        conferenceCreateSender.send(conferenceDTO);

        return "redirect:/all-conferences";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/conferences/user")
    public String showUserConferences(@AuthenticationPrincipal UserDetails userDetails,
                                      Model model){
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", userConferenceTitle);

        if(userDetails != null){
            String email = userDetails.getUsername();
            model.addAttribute("user", new UserDTO(userDetails.getUsername()));

            List<ConferenceDTO> conferences = userConferenceService.getConferencesByUser(email);
            model.addAttribute("conferences", conferences);
        }

        return "user-conferences";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/conferences/{conferenceId}/profile")
    public String showConferenceProfile(@PathVariable Long conferenceId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model,
                                        HttpServletResponse response){
        log.info("[ConferenceController] showConferenceProfile({})", conferenceId);
        if(userDetails == null) return "login";

        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");
        model.addAttribute("title", profileTitle);

        String email = userDetails.getUsername();

        UserDTO userDTO = new UserDTO(email);


        Optional<ConferenceDTO> optConference = conferenceService.getConferenceById(conferenceId, email);
            ConferenceDTO conferenceDTO;
            if(optConference.isPresent()){
                conferenceDTO = optConference.get();
                model.addAttribute("conference", conferenceDTO);
            }else{
                model.addAttribute("errorMessage", new NoSuchConferenceException().getMessage());
                return "conferences";
            }

            Optional<UserConferenceDTO> optUserConference = userConferenceService.findUserConferenceByUserAndConference(conferenceDTO, email);
            if(optUserConference.isPresent()){
                model.addAttribute("userconf", optUserConference.get());
            }else{
                model.addAttribute("userconf", new UserConferenceDTO());
            }


        model.addAttribute("user", userDTO);
        return "conference-profile";
    }

    @PreAuthorize("hasRole(T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @GetMapping(value = "/conferences/{conferenceId}/update")
    public String updateConference(@PathVariable String conferenceId,
                                   Model model){
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", updateConferenceTitle);

        return "update-conference";
    }

    @PreAuthorize("hasRole(T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @PostMapping(value = "/conferences/{conferenceId}/delete")
    public String deleteConference(@PathVariable Long conferenceId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model){
        conferenceDeleteSender.send(new ConferenceDTO(conferenceId));

        return "redirect:/conferences/user";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/all-conferences")
    public String showAllConferences(@AuthenticationPrincipal UserDetails userDetails,
                                     Model model,
                                     HttpServletResponse response){
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");
        model.addAttribute("title", conferencesTitle);

        if(userDetails != null){
            model.addAttribute("user", new UserDTO(userDetails.getUsername()));

            List<ConferenceDTO> conferencesDTO = conferenceService.getConferencesAfterYesterday(userDetails.getUsername());
            model.addAttribute("conferences", conferencesDTO);
        }

        return "conferences";
    }
}
