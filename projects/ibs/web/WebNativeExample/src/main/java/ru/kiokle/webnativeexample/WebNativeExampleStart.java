package ru.kiokle.webnativeexample;

import java.io.IOException;

public class WebNativeExampleStart {

	public static void main(String[] args) throws IOException {
		System.out.println("Hello!");
		ServerListerner serverListerner = new ServerListerner(8080);
		serverListerner.listen();
	}
}
