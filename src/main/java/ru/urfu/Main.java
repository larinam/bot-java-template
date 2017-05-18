package ru.urfu;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Луис on 18.05.2017.
 */
public class Main {
    private static String TWITTER_API_URL = "http://127.0.0.1:8080/api/";
    private static String USER_CREDENTIALS = "Mjoy";
    private static String BOT_TOKEN = "343166696:AAHMGyo6SI2TKWp-J7ast6P_LZxzB5vn-BI";

    public static void main(String[] args) {
        TelegramBot bot = TelegramBotAdapter.build(BOT_TOKEN);
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {

                updates.forEach(update -> {
                    String[] message = update.message().text().split(" ");
                    if (message.length < 2)
                        return;
                    if (message[0].equals("/post")) {
                        String messageToSend = String.join(" ", Arrays.copyOfRange(message, 1, message.length));
                        String payload = getPayload(messageToSend);
                        sendMessage(payload);
                    } else if (message[0].equals("/pm")) {
                        String messageToSend = String.join(" ", Arrays.copyOfRange(message, 2, message.length));
                        String payload = getPayload(messageToSend);
                        sendPrivateMessage(message[1], payload);
                    }
                });

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    private static void sendMessage(String payload) {
        try {
            sendRequest(new URL(TWITTER_API_URL + "messages"), payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPrivateMessage(String id, String payload) {
        try {
            sendRequest(new URL(TWITTER_API_URL + "messages/"+ id + "/pm"), payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(URL url, String payload) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(payload.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("Authorization", "Basic " + USER_CREDENTIALS);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();

            connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getPayload(String message) {
        return "{\"text\":\"" + message + "\",\"time\":" + System.currentTimeMillis() + "}";
    }
}
