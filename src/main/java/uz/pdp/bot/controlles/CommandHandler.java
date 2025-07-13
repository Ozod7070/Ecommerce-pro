package uz.pdp.bot.controlles;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.XaridBot;
import uz.pdp.bot.enums.UserState;
import uz.pdp.bot.models.Product;
import uz.pdp.bot.services.OrderService;
import uz.pdp.bot.services.ProductService;
import uz.pdp.bot.session.UserSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandHandler {
    private final XaridBot bot;
    private final Map<Long, UserSession> sessions;
    private final ProductService productService;
    private final OrderService orderService;

    public CommandHandler(XaridBot bot, Map<Long, UserSession> sessions,
                          ProductService productService, OrderService orderService) {
        this.bot = bot;
        this.sessions = sessions;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void handleCommand(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();

        UserSession session = sessions.computeIfAbsent(chatId, UserSession::new);

        if (text.equals("/start") || text.equals("🏠 Bosh menu")) {
            session.setState(UserState.MAIN_MENU);
            bot.sendMessage(chatId, "🏪 Xush kelibsiz!", createMainMenuKeyboard());
        } else if (session.getState() == UserState.SEARCHING) {
            handleSearch(chatId, text);
        } else {
            bot.sendMessage(chatId, "❌ Noma’lum buyruq", createMainMenuKeyboard());
        }
    }

    private void handleSearch(Long chatId, String query) {
        List<Product> results = productService.searchProducts(query);
        UserSession session = sessions.get(chatId);

        if (results.isEmpty()) {
            bot.sendMessage(chatId, "❌ Mahsulot topilmadi!", createMainMenuKeyboard());
            session.setState(UserState.MAIN_MENU);
        } else {
            session.setLastSearchResults(results);
            session.setState(UserState.SEARCHING_RESULTS);

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
        row3.add(new KeyboardButton("🏠 Bosh menu"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}
