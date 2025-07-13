package uz.pdp.bot.enums;

public enum OrderStatus {
    PENDING("⏳ Kutilmoqda"),
    PROCESSING("🔄 Tayyorlanmoqda"),
    SHIPPED("🚚 Yo'lda"),
    DELIVERED("✅ Yetkazildi");

    private final String description;
    OrderStatus(String s) {
        this.description = s;
    }

    public String getDescription() {
        return description;
    }
}
