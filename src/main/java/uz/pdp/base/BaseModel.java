package uz.pdp.base;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public abstract class BaseModel implements Serializable {
    private final UUID id;
    @Setter
    private boolean active = true;

    public BaseModel() {
        this.id = UUID.randomUUID();
    }

    public BaseModel(UUID id) {
        this.id = id;
    }

    public abstract void setId(UUID uuid);
}
