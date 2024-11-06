package org.example.instagrambot.configureWebhook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BotStartup implements CommandLineRunner {

    private final WebhookService webhookService;

    public BotStartup(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) throws Exception {
        webhookService.setWebhook();
    }
}
