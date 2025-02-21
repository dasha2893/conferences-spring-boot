package com.conferences.spring.controller;

import com.conferences.common.entity.User;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.spring.service.kafka.DataSender;
import com.conferences.spring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class UserController {

    @Value("${msg.index.title}")
    private String title;
    @Value("${msg.profile.title}")
    private String profileTitle;
    @Value("${msg.authorise.title}")
    private String authoriseTitle;

    private final UserService userService;
    private final DataSender userCreateSender;
    private final DataSender userUpdateSender;

    @Autowired
    public UserController(UserService userService,
                          @Qualifier("userCreateSender") DataSender userCreateSender,
                          @Qualifier("userUpdateSender") DataSender userUpdateSender) {
        this.userService = userService;
        this.userCreateSender = userCreateSender;
        this.userUpdateSender = userUpdateSender;
    }

    @GetMapping(value = {"/", "/index"})
    public String index(@AuthenticationPrincipal UserDetails userDetails,
                        Model model,
                        HttpServletResponse response) {
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        model.addAttribute("title", title);

        if (userDetails != null) {
            String email = userDetails.getUsername();
            model.addAttribute("user", new UserDTO(email));
        }

        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");

        return "index";
    }

    @GetMapping(value = "/login")
    public String showLoginPage(@AuthenticationPrincipal User user,
                                Model model,
                                HttpServletResponse response) {
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");

        model.addAttribute("title", authoriseTitle);

        UserDTO userDTO;
        if (user != null) {
            userDTO = new UserDTO(user);
        } else {
            userDTO = new UserDTO();
        }

        model.addAttribute("user", userDTO);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("user") UserDTO userDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletResponse response) {
        log.debug("[UserController]processLogin: start");
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors("password").stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            List<String> emailErrors = bindingResult.getFieldErrors("email").stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            errors.addAll(emailErrors);

            if (!CollectionUtils.isEmpty(errors)) {
                model.addAttribute("errorMessage", errors);
                return "login";
            }
        }

        try {
            String jwtToken = userService.authorizeUser(userDTO.getEmail(), userDTO.getPassword());

            Cookie jwtCookie = new Cookie("JWT", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }

        return "redirect:/profile";
    }

    @GetMapping(value = "/register")
    public String showRegisterPage(Model model, HttpServletResponse response) {
        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);
        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");
        model.addAttribute("title", profileTitle);

        UserDTO userDTO = new UserDTO();
        model.addAttribute("user", userDTO);

        return "add-user";
    }

    @PostMapping(value = "/user/create")
    public String createUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("errorMessage",
                    errorMessages
            );
            return "redirect:/register";
        }

        if (userDTO.getFirstName().isEmpty() ||
                userDTO.getLastName().isEmpty() ||
                userDTO.getEmail().isEmpty() ||
                userDTO.getPassword().isEmpty() ||
                userDTO.getPhone().isEmpty() ||
                userDTO.getUniversity().isEmpty()
        ) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mandatory Fields must not be empty"
            );
            return "redirect:/register";
        }


        try {
            userCreateSender.send(userDTO);
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpServletResponse response) {
        log.debug("[UserController]showProfile: start");
        if (userDetails == null) return "redirect:/login";

        String nonce = UUID.randomUUID().toString();
        model.addAttribute("nonce", nonce);

        UserDTO userDTO = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", userDTO);

        response.setHeader("Content-Security-Policy", "script-src 'self' 'nonce-" + nonce + "'");
        model.addAttribute("title",
                profileTitle
        );
        return "user-profile";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        log.info("[UserController]logout: start");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userService.logout(userDetails.getUsername());
        }

        Cookie jwtCookie = new Cookie("JWT", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return "redirect:/login";
    }
}
