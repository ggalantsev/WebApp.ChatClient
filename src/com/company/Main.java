package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		try {
			String login = Utils.login();
			if(login==null) return;
			Thread th = new Thread(new GetThread(login));
			th.setDaemon(true);
			th.start();

            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) continue;
				if (text.length()>4&&text.toLowerCase().substring(0,5).equals("room:")){
					GetThread.setRoom(text.substring(5,text.length()));
					System.out.println("Room changed to \""+text.substring(5,text.length())+"\"");
					continue;
				}
				Message m = new Message(login, text, GetThread.getRoom());
				int res = m.send(Utils.getURL() + "/add");
				if (res != 200 && res!=202) { // 200 OK
					System.out.println("HTTP error occured: " + res);
					return;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}
