package org.example.instagrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class InstagramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstagramBotApplication.class, args);
    }

    @Value("${bot.token}")
    private String token;


    @Value("${bot.webhookUrl}")
    private String webhookUrl;

    @Bean
    public TelegramBot telegramBot() {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Telegram bot token is missing or invalid");
        }
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new SetWebhook().url(webhookUrl));
        return bot;
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
