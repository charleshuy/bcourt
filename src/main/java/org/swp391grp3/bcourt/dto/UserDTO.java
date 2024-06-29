package org.swp391grp3.bcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.swp391grp3.bcourt.entities.District;
import org.swp391grp3.bcourt.entities.FileData;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String name;
    private String phone;
    private Double walletAmount;
    private String email;
    private Boolean enabled;
    private String fileId;
    private RoleDTO role;
}
