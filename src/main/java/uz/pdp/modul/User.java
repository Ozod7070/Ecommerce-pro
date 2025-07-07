package uz.pdp.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.enums.UserRole;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String fullName;
    private String userName;
    private String password;
    private UserRole role;
    private boolean isActive;
}