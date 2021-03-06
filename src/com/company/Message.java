package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class Message {
	private LocalDateTime date = LocalDateTime.now();
	private String from;
	private String to;
	private String text;
	private String room = "";

	public Message(String from, String text, String room) {
		this.from = from;
		this.text = text;
		this.room = room;
	}

	public String toJSON() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public static Message fromJSON(String s) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(s, Message.class);
	}

	@Override
	public String toString() {
		if (this.getTo()==null){
			return new StringBuilder().append("[").append(getDateString())
					.append(", From: ").append(from)
					.append("] ").append(text)
					.toString();
		} else
			return new StringBuilder().append("[").append(getDateString())
					.append(", From: ").append(from).append(", To: ").append(to)
					.append("] ").append(text)
					.toString();
	}

	public int send(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
	
		OutputStream os = conn.getOutputStream();
		try {
			String json = toJSON();
			os.write(json.getBytes(StandardCharsets.UTF_8));
			int res = conn.getResponseCode();
			if (res ==202) {
				System.err.print(conn.getHeaderField("isOnline")==null?"":conn.getHeaderField("isOnline"));
				System.err.println(conn.getHeaderField("getUsers")==null?"":conn.getHeaderField("getUsers"));
			}
			return res;
		} finally {
			os.close();
		}
	}
	
	public LocalDateTime getDate() {
		return date;
	}

	private String getDateString(){
		return new StringBuilder().append(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault())).append(" ")
				.append(date.getDayOfMonth()).append(" ")
				.append(date.getHour()).append(":")
				.append(date.getMinute()).toString();
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}
}
