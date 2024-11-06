package org.example.instagrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.instagrambot.service.InstagramService;
import org.example.instagrambot.service.PinterestService;
import org.example.instagrambot.service.TikTokService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.example.instagrambot.entity.User;
import org.example.instagrambot.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class InstagramBot {

    private final UserRepo userRepo;
    private final TelegramBot telegramBot;
    private final InstagramService instagramService;
    private final TikTokService tikTokService;
    private final PinterestService pinterestService;

    public void onUpdateReceived(Update update) {
        if (update.message() != null && update.message().text() != null) {
            Message message = update.message();
            String text = message.text();
            String chatType = message.chat().type().toString();
            int messageId = message.messageId();
            Long chatId = message.chat().id();

            if (isGroupChat(chatType)) {
                processGroupMessage(text, chatId, messageId, message);
            } else if ("private".equalsIgnoreCase(chatType)) {
                processPrivateMessage(update, text, chatId, messageId, message);
            }
        }
    }

    private boolean isGroupChat(String chatType) {
        return "group".equalsIgnoreCase(chatType) || "supergroup".equalsIgnoreCase(chatType);
    }

    private void processGroupMessage(String text, Long chatId, int messageId, Message message) {
        if (text.contains("instagram.com")) {
            respondAndDownload(chatId, messageId, () -> instagramService.downloader(text, chatId, messageId));
        } else if (text.contains("tiktok.com")) {
            respondAndDownload(chatId, messageId, () -> tikTokService.download(message));
        } else if (text.contains("https://pin.it")) {
            respondAndDownload(chatId, messageId, () -> pinterestService.fetchMediaUrl(message));
        }
    }

    private void processPrivateMessage(Update update, String text, Long chatId, int messageId, Message message) {
        if ("/start".equalsIgnoreCase(text)) {
            startMethod(update);
        } else if (text.contains("instagram.com")) {
            respondAndDownload(chatId, messageId, () -> instagramService.downloader(text, message.from().id(), messageId));
        } else if (text.contains("tiktok.com")) {
            respondAndDownload(chatId, messageId, () -> tikTokService.download(message));
        } else if (text.contains("https://pin.it")) {
            respondAndDownload(chatId, messageId, () -> pinterestService.fetchMediaUrl(message));
        } else {
            telegramBot.execute(new DeleteMessage(chatId, messageId));
        }
    }

    private void respondAndDownload(Long chatId, int messageId, Runnable downloadTask) {
        SendMessage sendMessage = new SendMessage(chatId, "⌛").replyToMessageId(messageId);
        telegramBot.execute(sendMessage);
        downloadTask.run();
    }

    public void startMethod(Update update) {
        Long chatId = update.message().from().id();

        if (userRepo.findByChatId(chatId) == null) {
            User user = new User();
            user.setChatId(chatId);
            userRepo.save(user);
        }

        telegramBot.execute(new SendMessage(chatId,
                "Ushbu bot orqali siz Instagram'dan silka orqali videolarni osongina yuklab olishingiz mumkin. Silkani yuboring va botimiz sizga tez orada videoni taqdim etadi."));
    }
}
