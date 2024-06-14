package org.swp391grp3.bcourt.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Setter
@Getter
public class UserDTO {
    private String userId;
    private String name;
    private String password;
    private String address;
    private String phone;
    private Double walletAmount;
    private String email;
    private String roleName;

    public UserDTO() {
    }

}
