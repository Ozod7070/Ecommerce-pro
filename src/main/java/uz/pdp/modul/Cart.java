package uz.pdp.modul;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
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
    public Object isPaid() {
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private UUID productId;
        private Integer quantity;
    }

    private UUID userId;
    private List<Item> products;
    private boolean paid;

    public Cart(UUID userId) {
        this.userId = userId;
        this.products = new ArrayList<>();
    }


}
