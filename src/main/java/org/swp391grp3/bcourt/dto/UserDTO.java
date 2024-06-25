package org.swp391grp3.bcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String name;
    private String password;
    private String address;
    private String phone;
    private Double walletAmount;
    private String email;
    private Boolean enabled;
    private RoleDTO role;
}
