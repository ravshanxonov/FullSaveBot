package org.example.instagrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InputMedia;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.model.request.InputMediaVideo;
import com.pengrad.telegrambot.request.SendMediaGroup;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.*;
import java.io.InputStream;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SendFileService {

    private final TelegramBot telegramBot;

    public SendFileService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public byte[] downloadFileAsBytes(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            try (InputStream in = url.openStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, n);
                }
                return baos.toByteArray(); // Faylni byte[] shaklida qaytaradi
            }
        } catch (Exception e) {
            System.err.println("Error while downloading file: " + e.getMessage());
            return null;
        }
    }



    public void sendMediaGroupToTelegram(List<byte[]> fileDataList, List<String> formats, Long chatId, int replyToMessageId) {
        List<InputMedia> mediaList = new ArrayList<>();

        for (int i = 0; i < fileDataList.size(); i++) {
            byte[] fileData = fileDataList.get(i);
            String format = formats.get(i).toLowerCase();

            try {
                File tempFile = createTempFileFromBytes(fileData, "media", "." + format);
                System.out.println(format);
                if (format.equals("jpg") || format.equals("jpeg") || format.equals("png")) {
                    mediaList.add(new InputMediaPhoto(tempFile));
                } else if (format.equals("mp4") || format.equals("mov")) {
                    mediaList.add(new InputMediaVideo(tempFile));
                } else {
                    System.err.println("Unsupported file type for album: " + format);
                }

                tempFile.deleteOnExit();
            } catch (IOException e) {
                System.err.println("Error while creating temp file: " + e.getMessage());
            }
        }

        if (!mediaList.isEmpty()) {
            SendMediaGroup sendMediaGroup = new SendMediaGroup(chatId, mediaList.toArray(new InputMedia[0]));
            sendMediaGroup.replyToMessageId(replyToMessageId);
            telegramBot.execute(sendMediaGroup);
        }
    }


    public File createTempFileFromBytes(byte[] data, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(data);
        }
        return tempFile;
    }


}
