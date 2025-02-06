package com.conferences.spring.service.kafka;

import com.conferences.common.service.dto.ConferenceDTO;
import com.conferences.common.service.dto.UserDTO;

public interface DataSender {
    void send(UserDTO userDTO);
    void send(ConferenceDTO conferenceDTO);
}
