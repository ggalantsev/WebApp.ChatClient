package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class GetThread implements Runnable {
    private final Gson gson;
//    private int n;
    private String login;

    public GetThread() {
        gson = new GsonBuilder().create();
    }

    public GetThread(String login) {
        this.login = login;
        gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        try {
            long seconds = LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond()-3600;
            // At start get messages for last hour
            while ( ! Thread.interrupted()) {
                URL url = new URL(com.company.Utils.getURL() + "/get?from=" + seconds);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                InputStream is = http.getInputStream();
                try {
                    byte[] buf = Utils.requestBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) {
                            if(m.getTo()==null||m.getTo().equals(this.getLogin())||m.getFrom().equals(this.getLogin())) {
                                System.out.println(m);
                                seconds = m.getDate().toInstant(ZoneOffset.UTC).getEpochSecond();
                            }
                        }
                    }
                } finally {
                    is.close();
                }

                Thread.sleep(1000);
            }
        } catch (SocketException e) {
            System.err.println("Server stop working"); return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
