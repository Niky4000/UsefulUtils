package com.ibs.youtubedownload;

import com.github.axet.vget.VGet;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class StartYoutubeDownload {

	private static final String DIR = "-path";
	private static final String URL = "-url";

	public static void main(String[] args) {
		List<String> argList = Arrays.asList(args);
		if (argList.contains(DIR) && argList.contains(URL)) {
			try {
				File file = new File(getConfig(DIR, argList));
				String url = getConfig(URL, argList);
				VGet v = new VGet(new URL(url), file);
				v.download();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			System.out.println("Usage: java -jar YoutubeDownload.jar " + DIR + " /home/me/Downloads/YoutubeVideoFile " + URL + " \"https://www.youtube.com/watch?v=5G5fzf7dyhU\"");
		}
	}

	public static String getConfig(String arg, List<String> argList) {
		int indexOf = argList.indexOf(arg);
		if (indexOf >= 0) {
			return argList.get(indexOf + 1);
		} else {
			return null;
		}
	}
}
