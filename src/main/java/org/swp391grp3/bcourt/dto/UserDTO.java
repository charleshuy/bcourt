package org.swp391grp3.bcourt.dto;

import lombok.Getter;
import lombok.Setter;


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
    private RoleDTO role;

    public UserDTO() {
    }

}
