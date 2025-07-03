package uz.pdp.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.base.BaseModel;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category extends BaseModel {
    private String name;
    private UUID parentId;
}