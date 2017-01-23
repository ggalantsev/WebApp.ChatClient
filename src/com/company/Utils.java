package com.company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {
    private static final String URL = "http://127.0.0.1";
    private static final int PORT = 8080;

    public static String getURL() {
        return URL + ":" + PORT;
    }

    public static String login () throws IOException {

        URL obj = null;
        HttpURLConnection conn = null;
        OutputStream os = null;
        for (; true; ) {
            try {
                URL url = new URL(getURL() + "/login");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                os = conn.getOutputStream();

                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter your login: ");
                String login = scanner.nextLine();
                if (login.equals("")) continue;
                System.out.println("Enter your password: ");
                String pass = scanner.nextLine();
                if (pass.equals("")) continue;

                os.write((login.concat(":").concat(pass)).getBytes(StandardCharsets.UTF_8));

                byte[] bytes = requestBodyToArray(conn.getInputStream());
                String resp = new String(bytes, StandardCharsets.UTF_8);
                System.out.println(resp);
                if (conn.getResponseCode() == 200) {
                    System.err.println(conn.getHeaderField("Response"));
                    return login;
                }
            } catch (ConnectException e){
                System.err.print("Server not working. ");
                System.err.println("Connection refused.");
            } catch (IOException e) {
                if (conn.getHeaderField("Response") != null) {
                    System.out.println(">> " + conn.getHeaderField("Response"));
                } else {
                    e.printStackTrace();
                    System.err.println("Server not working");
                    return null;
                }
            } finally {
                os.close();
            }
        }
    }

    public static byte[] requestBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

}
