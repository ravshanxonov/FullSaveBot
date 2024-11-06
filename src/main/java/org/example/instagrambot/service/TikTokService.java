package org.example.instagrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.request.SendVideo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final SendFileService sendFileService;
    private final TelegramBot telegramBot;

    @SneakyThrows
    public void download(Message message) {
        Path hasilPath = Paths.get("hasil.html");
        if (Files.exists(hasilPath)) {
            Files.delete(hasilPath);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", randomUserAgent());

        String initialUrl = "https://tiktokio.com/id/";
        String resText = httpGet(initialUrl, headers);

        Files.write(hasilPath, resText.getBytes());

        Document doc = Jsoup.parse(resText);
        Element prefixElement = doc.selectFirst("input[name=prefix]");
        if (prefixElement == null) {
            return;
        }
        String prefix = prefixElement.attr("value");

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Hx-Request", "true");
        headers.put("Hx-Target", "tiktok-parse-result");
        headers.put("Hx-Trigger", "search-btn");

        String postData = "prefix=" + prefix + "&vid=" + message.text();
        headers.put("Content-Length", String.valueOf(postData.length()));

        String postResponse = httpPost("https://tiktokio.com/api/v1/tk-htmx", postData, headers);

        Document postDoc = Jsoup.parse(postResponse);
        Elements videoLinks = postDoc.select("div.tk-down-link a");

        List<byte[]> fileDataList = new ArrayList<>();
        List<String> formats = new ArrayList<>();
        int counter = 0;

        for (Element link : videoLinks) {
            String href = link.attr("href");
            String linkText = link.text();

            if (linkText.contains("Download Photo")) {
                counter++;
                byte[] fileData = sendFileService.downloadFileAsBytes(href);
                if (fileData != null) {
                    fileDataList.add(fileData);
                    formats.add("png");
                }
            } else if (linkText.contains("Download without watermark")) {
                byte[] fileData = sendFileService.downloadFileAsBytes(href);
                if (fileData != null) {
                    File tempFile = createTempFileFromBytes(fileData, "video", ".mp4");
                    telegramBot.execute(new SendVideo(message.chat().id(), tempFile).replyToMessageId(message.messageId()));
                    tempFile.delete(); // Faylni o'chirish
                } else {
                }
                return;
            } else if (linkText.contains("Download Mp3") && counter != 0) {
                byte[] fileData = sendFileService.downloadFileAsBytes(href);
                if (fileData != null) {
                    File tempFile = createTempFileFromBytes(fileData, "audio", ".mp3");
                    telegramBot.execute(new SendAudio(message.chat().id(), tempFile).replyToMessageId(message.messageId()));
                    tempFile.delete(); // Faylni o'chirish
                }
            }
        }

        if (!fileDataList.isEmpty()) {
            sendFileService.sendMediaGroupToTelegram(fileDataList, formats, message.chat().id(), message.messageId());
        }
    }

    public File createTempFileFromBytes(byte[] data, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(data);
        }
        return tempFile;
    }

    private String httpGet(String urlString, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        headers.forEach(connection::setRequestProperty);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }
    }

    private String httpPost(String urlString, String data, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        headers.forEach(connection::setRequestProperty);

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(data);
            out.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }
    }

    private String randomUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
    }
}
