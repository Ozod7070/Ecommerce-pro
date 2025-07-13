package uz.pdp.bot.models;

import uz.pdp.bot.enums.Category;

public class Product {
    private final Long id;
    private final String name;
    private final double price;
    private final Category category;
    private final String imageUrl;

    public Product(Long id, String name, double price, Category category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
