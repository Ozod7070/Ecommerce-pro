package uz.pdp.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.pdp.bot.controlles.CommandHandler;
import uz.pdp.bot.services.OrderService;
import uz.pdp.bot.services.ProductService;
import uz.pdp.bot.session.UserSession;
import uz.pdp.bot.controlles.CallbackQueryHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XaridBot extends TelegramLongPollingBot {
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    private final CommandHandler commandHandler = new CommandHandler(this, sessions, productService, orderService);
    private final CallbackQueryHandler callbackHandler = new CallbackQueryHandler(this, sessions, productService, orderService);

    @Override
    public String getBotUsername() {
        return "Xarid_24Bot";
    }

    @Override
    public String getBotToken() {
        return "7173310353:AAG1EbynnRf9-CKZoPqopUUV4Rx-t6V7xaE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.startsWith("/")) {
                commandHandler.handleCommand(update.getMessage());
            } else {
                callbackHandler.handleText(chatId, text);
            }
        }
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(keyboard)
                .build();
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
