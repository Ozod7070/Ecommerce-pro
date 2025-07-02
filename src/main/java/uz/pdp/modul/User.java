package uz.pdp.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.enums.UserRole;
@AllArgsConstructor
@NoArgsConstructor
@Data

public class User {
    private String fullName;
    private String userName;
    private String password;
    private UserRole role;


}
