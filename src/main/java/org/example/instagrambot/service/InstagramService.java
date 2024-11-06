package org.example.instagrambot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InputMedia;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.model.request.InputMediaVideo;
import com.pengrad.telegrambot.request.SendMediaGroup;
import com.pengrad.telegrambot.request.SendMessage;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class InstagramService {

    private final WebClient webClient;
    private final TelegramBot telegramBot;
    private final SendFileService sendFileService;

    public InstagramService(WebClient.Builder webClientBuilder, TelegramBot telegramBot, SendFileService sendFileService) {
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(
                        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)))
                .responseTimeout(Duration.ofSeconds(50))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 50000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(50))
                        .addHandlerLast(new WriteTimeoutHandler(50)));

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.telegramBot = telegramBot;
        this.sendFileService = sendFileService;
    }

    public void downloader(String instagramUrl, Long chatId, int replyToMessageId) {
        String apiUrl = "http://localhost:8000/api/v2/download?url=" + instagramUrl;

        Mono<String> responseMono = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(response -> {
            List<byte[]> fileDataList = new ArrayList<>();
            List<String> formats = new ArrayList<>();
            List<String> urls = extractUrlsFromJson(response);

            urls.forEach(url -> {
                byte[] fileData = sendFileService.downloadFileAsBytes(url);
                if (fileData != null) {
                    fileDataList.add(fileData);

                    if (url.contains("png") || url.contains("jpg")) {
                        formats.add("png");
                    }else if (url.contains("mp4")) {
                        formats.add("mp4");
                    }
                }
            });

            sendFileService.sendMediaGroupToTelegram(fileDataList, formats, chatId, replyToMessageId);
        }, error -> {
            telegramBot.execute(new SendMessage(chatId, "Bu bot faqat ochiq foydalanuvchilarning kontentlarini yuklab beradi!"));
            System.err.println("Error occurred: " + error.getMessage());
        });
    }

    private List<String> extractUrlsFromJson(String jsonResponse) {
        List<String> urlList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode urlNode = root.path("url");

            if (urlNode.isArray()) {
                Iterator<JsonNode> elements = urlNode.elements();
                while (elements.hasNext()) {
                    JsonNode element = elements.next();
                    urlList.add(element.asText());
                }
            } else {
                urlList.add(urlNode.asText());
            }

        } catch (IOException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

        return urlList;
    }
}
