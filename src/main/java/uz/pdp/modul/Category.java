package uz.pdp.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseModel {
    private String name;
    private UUID parentId;
}