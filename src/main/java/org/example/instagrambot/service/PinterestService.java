package org.example.instagrambot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.pengrad.telegrambot.model.MessageEntity.Type.url;

@Service
@RequiredArgsConstructor
public class PinterestService {

    private final RestTemplate restTemplate;
    private final SendFileService sendFileService;


    public void fetchMediaUrl(Message message) {
        String mainUrl = "https://save.toolsed.com/wp-json/aio-dl/api/";
        String url = message.text();

        try {
            String requestUrl = mainUrl + "?url=" + url;

            String response = restTemplate.getForObject(requestUrl, String.class);

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray medias = jsonResponse.getJSONArray("medias");


            List<byte[]> fileDataList = new ArrayList<>();
            List<String> formats = new ArrayList<>(); // Fayl formatlarini saqlash uchun

            for (int i = 0; i < medias.length(); i++) {
                JSONObject media = medias.getJSONObject(i);
                String mediaUrl = media.getString("url");

                byte[] fileData = sendFileService.downloadFileAsBytes(mediaUrl);
                fileDataList.add(fileData);

                // URL dan formatni aniqlang yoki avtomatik belgilang
                String format = mediaUrl.substring(mediaUrl.lastIndexOf(".") + 1).toLowerCase();
                formats.add(format);
            }
            sendFileService.sendMediaGroupToTelegram(fileDataList, formats, message.chat().id(), message.messageId());


        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.status(500).body("Error fetching data from URL");
        }
    }
}
