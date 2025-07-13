package uz.pdp.bot.services;

import uz.pdp.bot.enums.Category;
import uz.pdp.bot.models.Product;

import java.util.Arrays;
import java.util.List;

public class ProductService {
    private final List<Product> products = Arrays.asList(
            // ELECTRONICS

            new Product(307L, "LAMZIEN LZ3", 100.0, Category.ELECTRONICS,"alpomish.jpeg"),
            new Product(318L, "Artel Smart TV A55LU", 3500.0, Category.ELECTRONICS,"artelsmarttv.jpg"),
            new Product(319L, "Shivaki Konditsioner 12HF", 2800.0, Category.ELECTRONICS,"shivakikondisoner.jpg"),
            new Product(320L, "Xiaomi Redmi Note 13", 2500.0, Category.ELECTRONICS,"telefon.jpg"),
            new Product(321L, "Samsung Galaxy A54", 3300.0, Category.ELECTRONICS,"telefon.jpg"),
            new Product(322L, "Anker QuardBase Powerbank", 450.0, Category.ELECTRONICS,"bowerbank.jpg"),
            new Product(323L, "Realme Buds Wireless 2 Neo", 290.0, Category.ELECTRONICS,"telefon.jpg"),
            new Product(324L, "HP Victus 15 Gaming Laptop", 8900.0, Category.ELECTRONICS,"kompyuter.jpg"),

            // BOOKS
            new Product(308L, "Oʻtkan kunlar", 15.0, Category.BOOKS,"otgankunlar.jpg"),
            new Product(309L, "Yangi avlod", 20.0, Category.BOOKS,"kitoblarolami.jpg"),
            new Product(310L, "Kitoblar olami", 25.0, Category.BOOKS,"kitoblarolami.jpg"),
            new Product(311L, "Kitoblar dunyosi", 30.0, Category.BOOKS,"kitoblarolami.jpg"),
            new Product(312L, "Temur tuzuklari", 35.0, Category.BOOKS,"temurtuziklari.jpg"),
            new Product(313L, "Alpomish", 36.0, Category.BOOKS,"alpomish.jpg"),
            new Product(314L, "Qutadgʻu bilig", 37.0, Category.BOOKS,"qutadgubilig.jpg"),
            new Product(315L, "Yulduzli tunlar", 38.0, Category.BOOKS,"yulduzlitunlar.jpg"),
            new Product(316L, "Boburnoma", 39.0, Category.BOOKS,"boburnoma.jpg"),
            new Product(317L, "Hamsa", 40.0, Category.BOOKS,"xamsa.jpg"),
            new Product(325L, "Ulugʻbek Xazinasi", 22.0, Category.BOOKS,"kitoblarolami.jpg"),
            new Product(326L, "Mustaqillik Davri Adabiyoti", 28.0, Category.BOOKS,"kitoblarolami.jpg"),

            //HOME_APLIANCES
            new Product(401L, "Artel kir yuvish mashinasi", 2500.0, Category.HOME_APPLIANCES,"kirmoshina.jpg"),
            new Product(402L, "Samsung mikroto‘lqinli pech", 1100.0, Category.HOME_APPLIANCES,""),
            new Product(403L, "Philips changyutgich", 950.0, Category.HOME_APPLIANCES,"pech.jpg"),
            new Product(404L, "Bosch idish yuvish mashinasi", 3500.0, Category.HOME_APPLIANCES,"kirmoshina.jpg"),
            new Product(405L, "LG muzlatkich", 4300.0, Category.HOME_APPLIANCES,"muzlatgich.jpg"),
            new Product(406L, "Beko gaz plitasi", 1200.0, Category.HOME_APPLIANCES,"gazplita.jpg"),
            new Product(407L, "Redmond multivarka", 750.0, Category.HOME_APPLIANCES,"multivarka.jpg")
    );



    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getProductsByCategory(Category category) {
        return products.stream()
                .filter(p -> p.getCategory() == category)
                .toList();
    }

    public Product getProductById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> searchProducts(String query) {
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
