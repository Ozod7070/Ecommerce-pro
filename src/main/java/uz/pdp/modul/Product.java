package uz.pdp.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;


import java.io.Serializable;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseModel implements Serializable {
    private String name;
    private double price;
    private int quantity;
    private UUID categoryId;
    private UUID sellerId;
}
