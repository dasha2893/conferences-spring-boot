package com.conferences.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USER_ROLES")
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class UserRole implements Serializable, GrantedAuthority {
    private static final long serialVersionUID = 271590053308860297L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Override
    public String getAuthority() {
        return "ROLE_" + getRole().getName();
    }

    public UserRole(Role role, User user){
        this.role = role;
        this.user = user;
    }
}
