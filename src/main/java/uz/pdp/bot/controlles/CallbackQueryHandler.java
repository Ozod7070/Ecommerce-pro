package uz.pdp.bot.controlles;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.XaridBot;
import uz.pdp.bot.enums.Category;
import uz.pdp.bot.enums.UserState;
import uz.pdp.bot.models.Order;
import uz.pdp.bot.models.Product;
import uz.pdp.bot.services.OrderService;
import uz.pdp.bot.services.ProductService;
import uz.pdp.bot.session.UserSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallbackQueryHandler {
    private final XaridBot bot;
    private final Map<Long, UserSession> sessions;
    private final ProductService productService;
    private final OrderService orderService;


    public CallbackQueryHandler(XaridBot bot, Map<Long, UserSession> sessions,
                                ProductService productService, OrderService orderService) {
        this.bot = bot;
        this.sessions = sessions;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void handleText(Long chatId, String text) {
        UserSession session = sessions.computeIfAbsent(chatId, k -> new UserSession(chatId));


        if (session.getState() == UserState.SEARCHING) {
            List<Product> results = productService.searchProducts(text);

            if (results.isEmpty()) {
                bot.sendMessage(chatId, "❌ Mahsulot topilmadi", createMainMenuKeyboard());
            } else {
                session.setLastSearchResults(results);
                session.setState(UserState.SEARCHING_RESULTS);
                showSearchResults(chatId, results);
            }

            return;
        }



        switch (text) {
            case "🏠 Bosh menu" -> showMainMenu(chatId);
            case "📦 Mahsulotlar" -> showCategories(chatId);
            case "📱 Electronics" -> showCategoryProducts(chatId, Category.ELECTRONICS);
            case "📚 Books" -> showCategoryProducts(chatId, Category.BOOKS);
            case "Maishiy texnika" -> showCategoryProducts(chatId, Category.HOME_APPLIANCES);
            case "🛒 Savatim" -> showCart(chatId);
            case "📋 Buyurtmalarim" -> showOrders(chatId);
            case "ℹ️ Biz haqimizda" -> showAbout(chatId);
            case "🔍 Qidirish" -> startSearch(chatId);
            default -> handleProductSelection(chatId, text);
        }
    }


    private void showMainMenu(Long chatId) {
        bot.sendMessage(chatId, "🏪 Bosh menu:", createMainMenuKeyboard());
    }

    private void showCategories(Long chatId) {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📱 Electronics"));
        row1.add(new KeyboardButton("📚 Books"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Maishiy texnika"));
        row2.add(new KeyboardButton("🏠 Bosh menu"));

        rows.add(row1);
        rows.add(row2);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();

        bot.sendMessage(chatId, "📦 Kategoriyani tanlang:", keyboard);
    }

    private void showCategoryProducts(Long chatId, Category category) {
        List<Product> products = productService.getProductsByCategory(category);
        if (products.isEmpty()) {
            bot.sendMessage(chatId, "❌ Mahsulotlar topilmadi", createMainMenuKeyboard());
            return;
        }

        UserSession session = sessions.get(chatId);
        session.setLastCategoryProducts(products);

        List<KeyboardRow> rows = new ArrayList<>();
        for (Product product : products) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(product.getName()));
            rows.add(row);
        }

        KeyboardRow backRow = new KeyboardRow();
        backRow.add(new KeyboardButton("🏠 Bosh menu"));
        rows.add(backRow);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();

        bot.sendMessage(chatId, "🛍 Mahsulotni tanlang:", keyboard);
    }

    private void handleProductSelection(Long chatId, String text) {
        UserSession session = sessions.get(chatId);

        List<Product> products;


        if (session.getState() == UserState.SEARCHING_RESULTS) {
            products = session.getLastSearchResults();
        } else {
            products = session.getLastCategoryProducts();
        }

        if (products == null || products.isEmpty()) {
            bot.sendMessage(chatId, "📭 Mahsulotlar ro‘yxati bo‘sh!", createMainMenuKeyboard());
            return;
        }

        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(text.trim())) { // ✅ trim qo‘shildi
                session.addToCart(product);
                bot.sendMessage(chatId, "✅ " + product.getName() + " savatga qo‘shildi!", createMainMenuKeyboard());
                session.setState(UserState.MAIN_MENU);
                return;
            }
        }

        bot.sendMessage(chatId, "❌ Mahsulot topilmadi!", createMainMenuKeyboard());
    }

    private void showCart(Long chatId) {
        UserSession session = sessions.get(chatId);
        List<Product> cartItems = session.getCart();

        if (cartItems.isEmpty()) {
            bot.sendMessage(chatId, "🛒 Savat bo‘sh!", createMainMenuKeyboard());
            return;
        }

        StringBuilder sb = new StringBuilder("🛒 Savatingiz:\n\n");
        double total = 0;
        for (Product p : cartItems) {
            sb.append("• ").append(p.getName()).append(" - ").append(p.getPrice()).append(" $\n");
            total += p.getPrice();
        }
        sb.append("\n💰 Jami: ").append(total).append(" $");

        bot.sendMessage(chatId, sb.toString(), createMainMenuKeyboard());
    }

    private void showOrders(Long chatId) {
        List<Order> orders = orderService.getUserOrders(chatId);
        if (orders.isEmpty()) {
            bot.sendMessage(chatId, "📋 Buyurtmalar topilmadi!", createMainMenuKeyboard());
            return;
        }

        StringBuilder sb = new StringBuilder("📋 Buyurtmalaringiz:\n\n");
        for (Order o : orders) {
            sb.append("🆔 #").append(o.getId()).append("\n")
                    .append("📦 ").append(o.getProductName()).append("\n")
                    .append("📅 ").append(o.getOrderDate()).append("\n")
                    .append("🚚 ").append(o.getStatus().getDescription()).append("\n\n");
        }

        bot.sendMessage(chatId, sb.toString(), createMainMenuKeyboard());
    }

    private void showAbout(Long chatId) {
        String about = """
                ℹ️ Biz haqimizda

                🏪 Bizning do‘kon - O‘zbekistondagi eng yaxshi texnologiya do‘koni!

                📱 Taklif qilamiz:
                • So‘nggi telefonlar
                • Sifatli noutbuklar
                • Planshetlar va aksessuarlar

                🚚 Tez yetkazib berish
                💯 Sifat kafolati
                🎧 24/7 qo‘llab-quvvatlash

                ☎️ Tel: 95 898 45 55
                """;
        bot.sendMessage(chatId, about, createMainMenuKeyboard());
    }

    private void startSearch(Long chatId) {
        UserSession session = sessions.get(chatId);
        session.setState(UserState.SEARCHING);
        bot.sendMessage(chatId, "🔍 Qidiruvni boshlang. Mahsulot nomini yozing:", createMainMenuKeyboard());
    }

    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📦 Mahsulotlar"));
        row1.add(new KeyboardButton("🛒 Savatim"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("📋 Buyurtmalarim"));
        row2.add(new KeyboardButton("ℹ️ Biz haqimizda"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("🔍 Qidirish"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    private ReplyKeyboardMarkup createCategoryBackKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("◀️ Ortga"));
        row1.add(new KeyboardButton("🏠 Bosh menu"));

        rows.add(row1);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }

    private void showSearchResults(Long chatId, List<Product> results) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (Product product : results) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(product.getName()));
            rows.add(row);
        }

        KeyboardRow backRow = new KeyboardRow();
        backRow.add(new KeyboardButton("🏠 Bosh menu"));
        rows.add(backRow);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();

        bot.sendMessage(chatId, "🔍 Qidiruv natijalari:", keyboard);
    }


}


