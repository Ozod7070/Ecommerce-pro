package uz.pdp.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.base.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cart extends BaseModel {
    public static class Item {
        UUID productId;
        Integer quantity;
    }
    private UUID userId;
    private List<Item> products;

    public Cart(UUID userId) {
        this.userId = userId;
        this.products = new ArrayList<>();
    }


}
