package org.example.instagrambot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.example.instagrambot.InstagramBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final Gson gson = new Gson();
    private final InstagramBot instagramBot;

    @PostMapping("/")
    public String onUpdateReceived(@RequestBody String requestBody) throws JsonProcessingException {
        Update update = gson.fromJson(requestBody, Update.class);
        instagramBot.onUpdateReceived(update);
        return "Webhook received";
    }
}