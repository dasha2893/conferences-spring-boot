package com.conferences.user_consumer.rest;

import com.conferences.common.entity.User;
import com.conferences.common.service.dto.AuthDTO;
import com.conferences.common.service.dto.UserDTO;
import com.conferences.user_consumer.service.UserService;
import com.conferences.user_consumer.service.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserRestController {
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService,
                              JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthDTO authDTO) {
        log.info("UserRestController: login start");
        Map<String, String> response = new HashMap<>();

        try {

            String jwtToken = userService.handleLogin(authDTO);

            if (jwtToken != null) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                httpHeaders.set("Content-Encoding", "UTF-8");

                response.put("token", jwtToken);

                ResponseEntity<Map<String, String>> body = ResponseEntity.ok().headers(httpHeaders).body(response);
                return body;
            }

            response.put("errorMessage", "Invalid username or password");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/getByEmail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestBody String email){
        log.info("UserRestController: getUserByEmail start");

        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isPresent()){
            UserDTO userDTO = new UserDTO(userOpt.get());
            return ResponseEntity.ok().body(userDTO);
        }

        return ResponseEntity.notFound().build();
    }


}
