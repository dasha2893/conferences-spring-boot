package com.conferences.spring.controller;

import com.conferences.common.entity.Conference;
import com.conferences.common.entity.User;
import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserConferenceDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.spring.exception.NoSuchConferenceException;
import com.conferences.spring.service.ConferenceService;
import com.conferences.spring.service.UserConferenceService;
import com.conferences.spring.service.kafka.DataSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                @Qualifier("conferenceDeleteSender") DataSender conferenceDeleteSender) {
        this.conferenceService = conferenceService;
        this.userConferenceService = userConferenceService;
        this.conferenceCreateSender = conferenceCreateSender;
        this.conferenceUpdateSender = conferenceUpdateSender;
        this.conferenceDeleteSender = conferenceDeleteSender;
    }

    @PreAuthorize("hasRole('ROLE_' + T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @GetMapping(value = "/conferences/add")
    public String addConference(@AuthenticationPrincipal User user,
                                Model model) {
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", newConferenceTitle);

        ConferenceDTO conferenceDTO = new ConferenceDTO();
        model.addAttribute("conference", conferenceDTO);

        if(user != null){
          model.addAttribute("user", new UserDTO(user));
        }

        return "add-conference";
    }

    @PreAuthorize("hasRole('ROLE_' + T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @PostMapping(value = "/conferences/add")
    public String addConference(@AuthenticationPrincipal User user,
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

        Optional<ConferenceDTO> conference = conferenceService.getConferenceByTitle(conferenceDTO.getTitle());
        if (conference.isPresent()) {
            model.addAttribute("errorMessage", "The conference already exists");
            return "add-conference";
        }

        conferenceDTO.setCreatedBy(user.getUserId());
        conferenceCreateSender.send(conferenceDTO);

        return "redirect:/all-conferences";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/conferences/user")
    public String showUserConferences(@AuthenticationPrincipal User user,
                                      Model model){
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", userConferenceTitle);

        if(user != null){
            model.addAttribute("user", new UserDTO(user));

            List<ConferenceDTO> conferences = userConferenceService.getConferencesByUser(user);
            model.addAttribute("conferences", conferences);
        }

        return "user-conferences";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/conferences/{conferenceId}/profile")
    public String showConferenceProfile(@PathVariable Long conferenceId,
                                        @AuthenticationPrincipal User user,
                                        Model model){
        if(user == null) return "login";

        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", profileTitle);

        UserDTO userDTO = new UserDTO(user);


        Optional<ConferenceDTO> optConference = conferenceService.getConferenceById(conferenceId);
            ConferenceDTO conferenceDTO;
            if(optConference.isPresent()){
                conferenceDTO = optConference.get();
                model.addAttribute("conference", conferenceDTO);
            }else{
                model.addAttribute("errorMessage", new NoSuchConferenceException().getMessage());
                return "conferences";
            }

            Optional<UserConferenceDTO> optUserConference = userConferenceService.findUserConferenceByUserAndConference(userDTO, conferenceDTO);
            if(optUserConference.isPresent()){
                model.addAttribute("userconf", optUserConference.get());
            }else{
                model.addAttribute("userconf", new UserConferenceDTO());
            }


        model.addAttribute("user", userDTO);
        return "conference-profile";
    }

    @PreAuthorize("hasRole('ROLE_' + T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @GetMapping(value = "/conferences/{conferenceId}/update")
    public String updateConference(@PathVariable String conferenceId,
                                   Model model){
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", updateConferenceTitle);

        return "update-conference";
    }

    @PreAuthorize("hasRole('ROLE_' + T(com.conferences.common.model.UserRoles).ADMIN.getRole())")
    @PostMapping(value = "/conferences/{conferenceId}/delete")
    public String deleteConference(@PathVariable Long conferenceId,
                                   @AuthenticationPrincipal User user,
                                   Model model){
        conferenceService.deleteConference(conferenceId);

        return "redirect:/conferences/user";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/all-conferences")
    public String showAllConferences(@AuthenticationPrincipal User user,
                                     Model model){
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", conferencesTitle);

        List<ConferenceDTO> conferencesDTO = conferenceService.getConferencesAfterYesterday();
        model.addAttribute("conferences", conferencesDTO);

        if(user != null){
            model.addAttribute("user", new UserDTO(user));
        }

        return "conferences";
    }
}
