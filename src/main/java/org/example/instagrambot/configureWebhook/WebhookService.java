package org.example.instagrambot.configureWebhook;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {
    private final String TELEGRAM_API_URL = ";
    private final String BOT_TOKEN = "";  // Bu yerda bot tokeningizni kiriting
    private final String WEBHOOK_URL = "";  // Ngrok webhook URL
    private final RestTemplate restTemplate;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setWebhook() {
        String getWebhookUrl = TELEGRAM_API_URL + BOT_TOKEN + "/getWebhookInfo";
        String setWebhookUrl = TELEGRAM_API_URL + BOT_TOKEN + "/setWebhook?url=" + WEBHOOK_URL;
        try {
            String response = restTemplate.getForObject(getWebhookUrl, String.class);
            System.out.println("Get Webhook Response: " + response);
            if (!response.contains(WEBHOOK_URL)) {
                String setResponse = restTemplate.getForObject(setWebhookUrl, String.class);
                System.out.println("Set Webhook Response: " + setResponse);
            } else {
                System.out.println("Webhook already set.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to set webhook");
        }
    }

}
