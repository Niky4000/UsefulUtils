/**
 *  This file is part of ytd2
 *
 *  ytd2 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ytd2 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with ytd2.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package zsk;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * http://www.youtube.com/watch?v=Cx6eaVeYXOs					4K (Ultra HD)
 * http://www.youtube.com/watch?v=9QFK1cLhytY					Javatar and .NOT
 * http://www.youtube.com/watch?v=Mt7zsortIXs				 	1080p "Lady Java"
 * http://www.youtube.com/watch?v=WowZLe95WDY					Tom Petty And the Heartbreakers - Learning to Fly (with lyrics)
 * http://www.youtube.com/watch?v=86OfBExGSE0					URZ 720p
 * http://www.youtube.com/watch?v=cNOP2t9FObw 					Blade 360 - 480
 * http://www.youtube.com/watch?v=HvQBrM_i8bU					MZ 1000 Street Fighter
 * http://www.youtube.com/watch?v=5fB_wIP21_Q					KTM 1190 Adventure vs. BMW R 1200GS 1080p (mp4, vorbis - audio)
 * http://www.youtube.com/watch?v=yVpbFMhOAwE					How Linux is build
 * http://www.youtube.com/watch?v=4XpnKHJAok8					Tech Talk: Linus Torvalds on git 
 * 
 * http://www.youtube.com/watch?v=5nj77mJlzrc  					BF109 G																																																																																								In lovely memory of my grandpa, who used to fly around the clouds. 
 * http://www.youtube.com/watch?v=I3lq1yQo8OY					Showdown: Air Combat - Me-109																																																																																		http://www.youtube.com/watch?v=yxXBhKJnRR8
 * http://www.youtube.com/watch?v=RYXd60D_kgQ					Me 262 Flys Again!
 * http://www.youtube.com/watch?v=6ejc9_yR5oQ					Focke Wulf 190 attacks Boeing B 17 in 2009 at Hahnweide
 *
 * 
 * https://www.youtube.com/watch?v=yNPECkESPbU					Linkin Park feat. Jay-Z - Numb/Encore
 * https://www.youtube.com/watch?v=XLLuZi012ok					The Notorious B.I.G. - Greatest Hits 
 * https://www.youtube.com/watch?v=tAGnKpE4NCI					Metallica - Nothing Else Matters [Official Music Video] 
 * https://www.youtube.com/watch?v=JU_6A23NvUg					Linkin Park Best Songs Ever 
 * 
 * 
 * technobase.fm / We Are One! 
 *
 * http://www.youtube.com/watch?v=ZoWB_IYoN60					"An unloaded weapon always shoots the loudest" Col. McQueen - Space 2063
 *
 * ODOT http://sourceforge.net/p/ytd2/bugs/7/ http://www.youtube.com/watch?v=fRVVzXnRsUQ   uses RTMPE (http://en.wikipedia.org/wiki/Protected_Streaming), which ytd2 cannot download atm 
 *
 */
public class YTDownloadThread extends Thread {
	
	static int THREAD_COUNT = 0;
	
	private boolean noDownload;
	private int threadNumber = YTDownloadThread.THREAD_COUNT++; // every download thread get its own number
	
	private String url = null;				// main URL (youtube start web page)
	private YoutubeUrl youtubeUrl = null;	// mail URL (youtube start web page) as object
	private String title = null;			// will be used as filename
	
	private String videoUrl = null;			// one video web resource
	private Vector<YoutubeUrl> nextVideoUrl = new Vector<YoutubeUrl>();	// list of URLs from webpage source
	private String fileName = null;			// contains the absolute filename
	//CookieStore bcs = null;				// contains cookies after first HTTP GET
	private boolean isInterrupted = false; 	// basically the same as Thread.isInterrupted()
	private int recursionCount = -1;		// counted in downloadone() for the 3 webrequest to one video

	private String contentType = null;
	private BufferedReader textReader = null;
	private BufferedInputStream	binaryReader = null;
	private HttpGet	request = null;
	private CloseableHttpClient	httpClient = null;
	private RequestConfig config = null;
	private HttpHost proxy = null;
	private CloseableHttpResponse response = null;
	
    
	public YTDownloadThread() {
		super();
		String sv = "thread started: ".concat(this.getMyName()); 
		outputDebugMessage(sv);
	} 
	
	private boolean downloadOne(String url) throws IOException {
		
		boolean rc = false;
		boolean rc204 = false;
		boolean rc302 = false;
	
		this.recursionCount++;
		
		// stop recursion
		try {
			if (url.equals("")) {
				return(false);
			}
		} catch (NullPointerException npe) {
			return(false);
		}
		if (JFCMainClient.getQuitRequested()) {
			return(false); // try to get information about application shutdown
		}
		
		outputDebugMessage("start.");
		
		// TODO GUI option for proxy?
		// http://wiki.squid-cache.org/ConfigExamples/DynamicContent/YouTube
		// using local squid to save download time for tests

		try {
			// determine http_proxy environment variable
			if (!this.getProxy().equals("")) {
				// with proxy
				
				// maybe usefull for SOCKS http://stackoverflow.com/questions/1388822/how-can-i-configure-httpclient-to-authenticate-against-a-socks-proxy
				String sproxy = JFCMainClient.PROXY.toLowerCase().replaceFirst("http(s?)://", "") ;
				this.proxy = new HttpHost( sproxy.replaceFirst(":(.*)", ""), Integer.parseInt( sproxy.replaceAll("(.*):", "")), "http");
				this.config = RequestConfig.custom().setProxy(this.proxy).build();
			}
			
			this.request = new HttpGet( url );	
			this.request.setConfig(this.config);
			this.httpClient = HttpClients.createDefault();	
			
		} catch (Exception e) {
			outputDebugMessage(e.getMessage());
		}
        outputDebugMessage("executing request: ".concat( this.request.getRequestLine().toString()) );
        outputDebugMessage("uri: ".concat( this.request.getURI().toString()) );
        outputDebugMessage("using proxy: ".concat( this.getProxy() ));
        
        // TODO maybe we save the video IDs+res that were downloaded to avoid downloading the same video again?
       
		try {
			this.response = this.httpClient.execute(this.request);
		} catch (ClientProtocolException cpe) {
			outputDebugMessage(cpe.getMessage());
		} catch (UnknownHostException uhe) {
			output((JFCMainClient.isGerman()?"Fehler bei der Verbindung zu: ":"error connecting to: ").concat(uhe.getMessage()));
			outputDebugMessage(uhe.getMessage());
		} catch (IOException ioe) {
			outputDebugMessage(ioe.getMessage());
		} catch (IllegalStateException ise) {
			outputDebugMessage(ise.getMessage());
		}

		try {
			outputDebugMessage("HTTP response status line:".concat( this.response.getStatusLine().toString()) );

			// abort if HTTP response code is != 200, != 302 and !=204 - wrong URL?
			if (!(rc = this.response.getStatusLine().toString().toLowerCase().matches("^(http)(.*)200(.*)")) & 
					!(rc204 = this.response.getStatusLine().toString().toLowerCase().matches("^(http)(.*)204(.*)")) &
					!(rc302 = this.response.getStatusLine().toString().toLowerCase().matches("^(http)(.*)302(.*)"))) {
				outputDebugMessage(this.response.getStatusLine().toString().concat(" ").concat(url));
				output(this.response.getStatusLine().toString().concat(" \"").concat(this.title).concat("\""));
				return(rc & rc204 & rc302);
			}
			if (rc204) {
				rc = downloadOne(this.nextVideoUrl.get(0).getUrl());
				return(rc);
			}
			if (rc302) { 
				outputDebugMessage("location from HTTP Header: ".concat(this.response.getFirstHeader("Location").toString()));
			}

		} catch (NullPointerException npe) {
			// if an IllegalStateException was catched while calling httpclient.execute(httpget) a NPE is caught here because
			// response.getStatusLine() == null
			this.videoUrl = null;
		}
		
		HttpEntity entity = null;
        try {
            entity = this.response.getEntity();
        } catch (NullPointerException npe) {
        	//TODO catch must not be empty
        }
        
        // try to read HTTP response body
        if (entity != null) {
			try {
				if (this.response.getFirstHeader("Content-Type").getValue().toLowerCase().matches("^text/html(.*)")) {
					this.textReader = new BufferedReader(new InputStreamReader(entity.getContent()));
				} else {
					this.binaryReader = new BufferedInputStream( entity.getContent());
				}
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            try {
            	// test if we got a webpage
            	this.contentType = this.response.getFirstHeader("Content-Type").getValue().toLowerCase();
            	if (this.contentType.matches("^text/html(.*)")) {
            		rc = saveTextData();
            	// test if we got the binary content
            	} else if (this.contentType.matches("(audio|video)/(.*)|application/octet-stream")) {

            		// add audio stream URL if necessary
            		if (this.recursionCount==1) {
	    				outputDebugMessage("last response code==true - download: ".concat(this.nextVideoUrl.get(0).getYoutubeId()));
	    				if (this.nextVideoUrl.get(0).getAudioStreamUrl().equals("")) { 
	    					outputDebugMessage("download audio stream? no");
	    				} else {
	    					// FIXME audio stream has no filename if we add the direct URL to the GUI url list - we should add YTURL objects, not Strings!
	    					outputDebugMessage("download audio stream? yes - ".concat(this.nextVideoUrl.get(0).getAudioStreamUrl()));
	    					this.youtubeUrl.setTitle(this.getTitle());
	    					JFCMainClient.addYoutubeUrlToList(this.nextVideoUrl.get(0).getAudioStreamUrl(),this.getTitle(),"AUDIO");
	    				}
            		}            		
            		if (JFCMainClient.getNoDownload())
            			reportHeaderInfo();
            		else
            			saveBinaryData();
            	} else { // content-type is not video/
            		rc = false;
            		this.videoUrl = null;
            	}
            } catch (IOException ex) {
                try {
					throw ex;
				} catch (IOException e) {
					e.printStackTrace();
				}
            } catch (RuntimeException ex) {
                try {
					throw ex;
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        } //if (entity != null)
        
       	this.httpClient.close();

        outputDebugMessage("done: ".concat(url));
        if (this.videoUrl==null) {
        	this.videoUrl=""; // to prevent NPE
        }
        if (this.videoUrl.matches(JFCMainClient.URL_REGEX)) {
        	// enter recursion - download video resource
        	outputDebugMessage("try to download video from URL: ".concat(this.videoUrl));
        	rc = downloadOne(this.videoUrl);
        } else {
        	// no more recursion - html source hase been read
        	// rc==false if video could not downloaded because of some error (like wrong protocol or country restriction)
        	if (!rc) {
        		outputDebugMessage("cannot download video - URL does not seem to be valid or could not be found: ".concat(this.url));
        		output(JFCMainClient.isGerman()?"es gab ein Problem die Video URL zu finden! evt. wegen Landesinschränkung?!":"there was a problem getting the video URL! perhaps not allowed in your country?!");
        		output((JFCMainClient.isGerman()?"erwäge die URL dem Autor mitzuteilen!":"consider reporting the URL to author! - ").concat(this.url));
        		this.videoUrl = null;
        	}
        } 
        
        this.videoUrl = null;

    	return(rc);		
	} 

	private void reportHeaderInfo() {
		
		Vector<String> vsfnamewb = getFileNames(this.getTitle(),this.response.getFirstHeader("Content-Type").getValue());

		if (JFCMainClient.getNoDownload()) {
			outputDebugMessage("NO-DOWNLOAD mode active (ndl on)");
		}
		if (JFCMainClient.getAudioOnly()) {
			outputDebugMessage("AUDIO-ONLY mode active");
		}

		if (JFCMainClient.getDebug()) {
			
			outputDebugMessage("");
			
			if (JFCMainClient.getNoDownload()) {
				outputDebugMessage("NO-DOWNLOAD mode active (ndl on)");
			}
			if (JFCMainClient.getAudioOnly()) {
				outputDebugMessage("AUDIO-ONLY mode active");
			}
			
			outputDebugMessage("all HTTP header fields:");
			
			for (int i = 0; i < this.response.getAllHeaders().length; i++) {
				outputDebugMessage(this.response.getAllHeaders()[i].getName().concat("=").concat(this.response.getAllHeaders()[i].getValue()));
			}
			
			outputDebugMessage( String.format("filename would be: \"%s.%s\"",vsfnamewb.get(0),vsfnamewb.get(1)) ); // youtube video title will be transformed to filename
		} else {
			Long iFileSize = Long.parseLong(this.response.getFirstHeader("Content-Length").getValue());
			output("");
			
			if (JFCMainClient.getNoDownload()) {
				output("NO-DOWNLOAD mode active (ndl on)");
			}
			if (JFCMainClient.getAudioOnly()) {
				output("AUDIO-ONLY mode active");
			}
			
			output("some HTTP header fields:");
			output("content-type: ".concat( this.response.getFirstHeader("Content-Type").getValue()) );
			output("content-length: ".concat(iFileSize.toString()).concat(" Bytes").concat(" ~ ").concat(Long.toString((iFileSize/1024)).concat(" KiB")).concat(" ~ ").concat(Long.toString((iFileSize/1024/1024)).concat(" MiB")) );
			
			if (JFCMainClient.getNoDownload()) {
				output((JFCMainClient.isGerman()?"Dateiname würde sein: ":"filename would be: ").concat(String.format("\"%s.%s\"",vsfnamewb.get(0),vsfnamewb.get(1)))); // title contains just filename, no path
			}
		}
		this.videoUrl = null;
	} 
	
	
	/*
	 * generate Filename based on youtube video title and content type of MIME header
	 */
	
	public Vector<String> getFileNames(String title, String contentType) {
		String fileName = title;
		
		String fileNameAfterDot = contentType.replaceFirst("audio/|video/|application/", "").replaceAll("x-", "");
		fileNameAfterDot = fileNameAfterDot.replaceFirst("octet-stream", "mp4");
		fileNameAfterDot = fileNameAfterDot.replaceFirst("audio-stream", "mp3");
		
		Vector<String> rc = new Vector<String>();
		rc.add(fileName); rc.add(fileNameAfterDot);
		return rc;
	} 
	
	private int addWEBM_ULTRAHD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;
		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("272"),this.url,"",sourceCodeVideoUrls.get("140"))); // webm < ultra HD		size=3840x2160	type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("271"),this.url,"",sourceCodeVideoUrls.get("140"))); // webm < ultra HD		size=2560x1440	type=video/webm;+codecs="vp9
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("271"),this.url,"",sourceCodeVideoUrls.get("140"))); // webm < ultra HD 		size=2560x1440	type=video/webm;+codecs="vp9
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("212"),this.url,"",sourceCodeVideoUrls.get("140"))); // webm < ultra HD		size=3840x2160	type=video/webm;+codecs="vp9"
		}

		return newIndex;
	} 

	private int addMPEG_ULTRAHD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;
		if (!JFCMainClient.SAVE_DISKSPACE) {  //fmtUrlPair[0].equals( "266" )?"2160p mpeg, ":            // <4k HD  type=video/mp4;+codecs=%22avc1.640033" & size=3840x2160&
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("138"),this.url,"",sourceCodeVideoUrls.get("140"))); // mpeg ultra HD		size=4096x2304 type=video/mp4;+codecs="avc1.640033"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("266"),this.url,"",sourceCodeVideoUrls.get("140"))); // mpeg ultra HD		size=3840x2160 ype=video/mp4;+codecs=%22avc1.640033"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("264"),this.url,"",sourceCodeVideoUrls.get("140"))); // mpeg ultra HD		size=2560x1440 type=video/mp4;+codecs="avc1.640032"
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("264"),this.url,"",sourceCodeVideoUrls.get("140"))); // mpeg ultra HD		size=2560x1440 type=video/mp4;+codecs="avc1.640032"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("266"),this.url,"",sourceCodeVideoUrls.get("140"))); // mpeg ultra HD		size=3840x2160 ype=video/mp4;+codecs=%22avc1.640033"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("138"),this.url,"",sourceCodeVideoUrls.get("140"))); // mpeg ultra HD		size=4096x2304 type=video/mp4;+codecs="avc1.640033"
		}

		return newIndex;
	} 

	private int addMPEG_HD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;
		// try 3D HD first if 3D is selected 
		if (JFCMainClient.get3DButtonState())
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("84"),this.url,"3D")); // mpeg 3D full HD
		
		// if SDS is on reverse order! - 720p before 1080p for HD and so on
		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("137"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg full HD size=1920x1080 type=video/mp4;+codecs="avc1.640028"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("136"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg full HD size=1280x720  type=video/mp4;+codecs="avc1.4d401f"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("37"),this.url));										// mpeg full HD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("22"),this.url));										// mpeg half HD quality=hd720  type=video/mp4;+codecs="avc1.64001F%2C+mp4a.40.2" 
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("136"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg full HD size=1280x720  type=video/mp4;+codecs="avc1.4d401f"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("137"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg full HD size=1920x1080 type=video/mp4;+codecs="avc1.640028"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("22"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg half HD quality=hd720  type=video/mp4;+codecs="avc1.64001F%2C+mp4a.40.2" 
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("37"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg full HD size=1920x1080 type=video/mp4;+codecs="avc1.640028"
		}
		return newIndex;
	} 
	
	private int addWBEM_HD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;
		// try 3D HD first if 3D is selected 
		if (JFCMainClient.get3DButtonState())
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("100"),this.url,"3D")); // webm 3D HD

		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("248"),this.url,"",sourceCodeVideoUrls.get("171")));	// webm full HD size=1920x1080 type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("247"),this.url,"",sourceCodeVideoUrls.get("171")));	// webm half HD size=1280x720  type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("46"),this.url));										// webm full HD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("45"),this.url));										// webm half HD
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("46"),this.url));										// webm full HD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("45"),this.url));										// webm half HD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("247"),this.url,"",sourceCodeVideoUrls.get("171")));	// webm half HD size=1280x720  type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("248"),this.url,"",sourceCodeVideoUrls.get("171")));	// webm full HD size=1920x1080 type=video/webm;+codecs="vp9"
		}
		return newIndex;
	} 
	
	private int addWBEM_SD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;
		// try 3D  first if 3D is selected 
		if (JFCMainClient.get3DButtonState())
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("102"),this.url,"3D")); // webm 3D SD

		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("244"),this.url,"",sourceCodeVideoUrls.get("171")));	// webm SD size=854x480    type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("243"),this.url,"",sourceCodeVideoUrls.get("171")));	// webm SD size=640x360    type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("44"),this.url));										// webm SD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("43"),this.url));										// webm SD quality=medium  type=video/webm;+codecs="vp8.0%2C+vorbis"
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("43"),this.url));										// webm SD quality=medium  type=video/webm;+codecs="vp8.0%2C+vorbis"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("44"),this.url));										// webm SD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("243"),this.url,"",sourceCodeVideoUrls.get("140")));	// webm SD size=640x360    type=video/webm;+codecs="vp9"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("244"),this.url,"",sourceCodeVideoUrls.get("140")));	// webm SD size=854x480    type=video/webm;+codecs="vp9"
		}
		return newIndex;
	} 
	
	private int addFLV_SD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;

		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("35"),this.url)); // flv SD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("34"),this.url)); // flv SD
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("34"),this.url)); // flv SD
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("35"),this.url)); // flv SD
		}
		return newIndex;
	} 
	
	private int addMPEG_SD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;

		// try 3D first if "3D" is selected 
		if (JFCMainClient.get3DButtonState())
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("82"),this.url,"3D")); // mpeg 3D SD

		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("135"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg SD size=854x480    type=video/mp4;+codecs="avc1.4d401e"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("134"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg SD size=640x360    type=video/mp4;+codecs="avc1.4d401e"
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("134"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg SD size=640x360    type=video/mp4;+codecs="avc1.4d401e"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("135"),this.url,"",sourceCodeVideoUrls.get("140")));	// mpeg SD size=854x480    type=video/mp4;+codecs="avc1.4d401e"
		}
		
		this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("18"),this.url));											// mpeg SD quality=medium  type=video/mp4;+codecs="avc1.42001E%2C+mp4a.40.2"
		return newIndex;
	}  
	
	private int addMPEG_LD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;
		
		if (!JFCMainClient.SAVE_DISKSPACE) {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("133"),this.url,"LD",sourceCodeVideoUrls.get("140")));	// mpeg LD size=426x240  type=video/mp4;+codecs="avc1.4d4015"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("160"),this.url,"LD",sourceCodeVideoUrls.get("140")));	// mpeg LD size=256x144  type=video/mp4;+codecs="avc1.4d400c"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("36"),this.url,"LD"));										// mpeg LD quality=small type=video/3gpp;+codecs="mp4v.20.3%2C+mp4a.40.2"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("17"),this.url,"LD"));										// mpeg LD quality=small type=video/3gpp;+codecs="mp4v.20.3%2C+mp4a.40.2"
		} else {
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("17"),this.url,"LD"));										// mpeg LD quality=small type=video/3gpp;+codecs="mp4v.20.3%2C+mp4a.40.2"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("36"),this.url,"LD"));										// mpeg LD quality=small type=video/3gpp;+codecs="mp4v.20.3%2C+mp4a.40.2"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("160"),this.url,"LD",sourceCodeVideoUrls.get("140")));	// mpeg LD size=256x144  type=video/mp4;+codecs="avc1.4d400c"
			this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get("133"),this.url,"LD",sourceCodeVideoUrls.get("140")));	// mpeg LD size=426x240  type=video/mp4;+codecs="avc1.4d4015"
		}
		
		return newIndex;
	} 
	
	private int addFLV_LD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;

		this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get( "5"),this.url,"LD")); // flv LD quality=small type=video/x-flv
		return newIndex;
	}
	
	private int addWEBM_LD_Urls(int index, HashMap<String, String> sourceCodeVideoUrls) {
		int newIndex = index;

		this.nextVideoUrl.add(newIndex++, new YoutubeUrl(sourceCodeVideoUrls.get( "242"),this.url,"LD",sourceCodeVideoUrls.get("171"))); // flv LD  size=426x240 type=video/webm;+codecs="vp9"
		return newIndex;
	} 

	/**
	 * put URL parts between "&" in a sorted array and add "range=0-1000000000"
	 * 
	 * @param source
	 * @param delimiter
	 * @return
	 */
	private String sortStringAt ( String source, String delimiter ) {

		String sortedUrl = source.replaceFirst("\\?.*", ""); // http://...

		// FIXME range=0-999999999 lead to a maximum fraction of 1GB (not GiB !), but if the file is greater than 1GB we would have to download the whole video in more smaller parts (like the ytplayer does)
		// http://www.youtube.com/watch?v=x9WxkcUTGSY  4:14:33
		String[] unsortedUrl = source.replaceFirst("http.*\\?","").concat("&range=0-999999999").split(delimiter);
		
		Arrays.sort(unsortedUrl);
		
		for (int i=0;i<unsortedUrl.length-1;i++) {
			if (unsortedUrl[i].equals(unsortedUrl[i+1]))
				unsortedUrl[i]="";
		}
		
		sortedUrl += Arrays.toString(unsortedUrl);
		sortedUrl = sortedUrl.replaceAll("\\[, ", delimiter).replaceAll(", ", delimiter).replaceAll(",,", delimiter).replaceAll("]", "").replaceAll(delimiter+delimiter, delimiter);
		sortedUrl = sortedUrl.replaceFirst("/videoplayback\\[", "/videoplayback?").replaceFirst("/videoplayback&", "/videoplayback?");
		return sortedUrl;
				
	} 
	
	
	private boolean saveTextData() throws IOException {
		
		boolean rc = false;
		// read html lines one by one and search for java script array of video URLs
		String line = ""; 
		String line0 = ""; 
		String line1 = "";
		
		while (line != null) {
			line = this.textReader.readLine();
			try {
				// FIXME audio is missing .. video and audio are in separated streams in  adaptive_fmts
				if (this.recursionCount==0 && line.matches(".*(\"adaptive_fmts\":|\"url_encoded_fmt_stream_map\":).*")) { // behind that two strings are the comma separated video URLs we use 
					rc = true;
    				HashMap<String, String> sourceCodeVideoUrls = new HashMap<String, String>();

    				line = line.replaceAll(" ", "");
					line = line.replace("%25","%");
					line = line.replace("\\u0026", "&");
    				
					if (line.contains("\"url_encoded_fmt_stream_map\":\"")) {
    					line1 = StringUtils.substringBetween(line,"\"url_encoded_fmt_stream_map\":\"" , "\"");
    				}

					line0 = "";
					
					if (line.contains("\"adaptive_fmts\":\"")) {
						line0 = StringUtils.substringBetween(line,"\"adaptive_fmts\":\"" , "\"");
					}
					
					if (line0==null) {
						line0="";
					}
					if (line1==null) {
						line1="";
					}
					
					line = line0 + "," + line1;
					
					outputDebugMessage( String.format("length sline0 sline1: %d %d",line0.length(), line1.length()) );
					outputDebugMessage( String.format("sline0 (adaptive fmt) %s \n sline1 (url_encoded fmt): %s",line0, line1) );

					// by anonymous
					String[] sourceCodeYoutubeUrls = line.split(",");
					outputDebugMessage("ssourcecodeuturls.length: ".concat(Integer.toString(sourceCodeYoutubeUrls.length)));
					String resolutions = JFCMainClient.isGerman()?"gefundene Video URL für Auflösung: ":"found video URL for resolution: ";
					
					for (String url : sourceCodeYoutubeUrls) {

						// assuming rtmpe is used for all resolutions, if found once - end download
						if (url.matches(".*conn=rtmpe.*")) {
							outputDebugMessage("RTMPE found. cannot download this one!");
							output(JFCMainClient.isGerman()?"Herunterladen des Videos wegen nicht unterstütztem Protokoll (RTMPE) leider nicht möglich!":"Unable to download video due to unsupported protocol (RTMPE). sry!");
							break;
						}
						String[] fmtUrlPair = url.split("url=http(s)?",2);
						fmtUrlPair[1] = "url=http"+fmtUrlPair[1]+"&"+fmtUrlPair[0];
						// grep itag=xz out and use xy as hash key
						// 2013-02 itag now has up to 3 digits
						fmtUrlPair[0] = fmtUrlPair[1].substring(fmtUrlPair[1].indexOf("itag=")+5, fmtUrlPair[1].indexOf("itag=")+5+1+(fmtUrlPair[1].matches(".*itag=[0-9]{2}.*")?1:0)+(fmtUrlPair[1].matches(".*itag=[0-9]{3}.*")?1:0));
						
						if (this.request.getURI().toString().startsWith("https")) {
							fmtUrlPair[1] = fmtUrlPair[1].replaceFirst("url=http%3A%2F%2F", "https://"); // webpage source code only provides http urls if accessed via wget or ytd2 - the browser does something unknown so google sends back httpS urls within source code!
						} else {
							fmtUrlPair[1] = fmtUrlPair[1].replaceFirst("url=http%3A%2F%2F", "http://");
						}
						
						fmtUrlPair[1] = fmtUrlPair[1].replaceAll("%3F","?").replaceAll("%2F", "/").replaceAll("%3B",";")/*.replaceAll("%2C",",")*/.replaceAll("%3D","=").replaceAll("%26", "&").replaceAll("%252C", "%2C").replaceAll("sig=", "signature=").replaceAll("&s=", "&signature=").replaceAll("\\?s=", "?signature=");
						
						// remove duplicate parts between &
						String sortedUrl = this.sortStringAt( fmtUrlPair[1], "&" ) ;
						outputDebugMessage(String.format("video tag: %s url: %s",fmtUrlPair[0],sortedUrl));  // fmtUrlPair[1] -> ssortedURL
						fmtUrlPair[1] = sortedUrl;
								
						try {
							sourceCodeVideoUrls.put(fmtUrlPair[0], fmtUrlPair[1]); // save that URL
							//debugoutput(String.format( "video url saved with key %s: %s",fmtUrlPair[0],ssourcecodevideourls.get(fmtUrlPair[0]) ));
							resolutions = resolutions.concat(
									fmtUrlPair[0].equals( "138" )?"2304p mpeg, ":            // 4k HD   type=video/mp4;+codecs="avc1.640033" & size=4096x2304
									fmtUrlPair[0].equals( "264" )?"1440p mpeg, ":            // <4k HD  type=video/mp4;+codecs="avc1.640032" & size=2560x1440
									fmtUrlPair[0].equals( "266" )?"2160p mpeg, ":            // <4k HD  type=video/mp4;+codecs=%22avc1.640033" & size=3840x2160&
									fmtUrlPair[0].equals( "271" )?"1440p webm, ":            // <4k HD  type=video/webm;+codecs=%22vp9" & size=2560x1440
											
									fmtUrlPair[0].equals( "272" )?"2160p webm, ":            // <4k HD  type=video/webm;+codecs="vp9" &	size=3840x2160									
									fmtUrlPair[0].equals( "248" )?"1080p mpeg, ":            // HD      type=video/webm;+codecs="vp9" & size=1920x1080
									fmtUrlPair[0].equals(  "37" )?"1080p mpeg, ":            // HD      type=video/mp4;+codecs="avc1.64001F,+mp4a.40.2"
									fmtUrlPair[0].equals(  "22" )?"720p mpeg, ":             // HD      type=video/mp4;+codecs="avc1.64001F,+mp4a.40.2"
									fmtUrlPair[0].equals( "247" )?"1080p mpeg, ":            // HD      type=video/webm;+codecs="vp9" & size=1280x720
									fmtUrlPair[0].equals(  "84" )?"1080p 3d mpeg, ":         // HD 3D   type=video/mp4;+codecs="avc1.64001F,+mp4a.40.2"
									fmtUrlPair[0].equals(  "18" )?"360p mpeg, ":             // SD      type=video/mp4;+codecs="avc1.42001E,+mp4a.40.2"
									fmtUrlPair[0].equals(  "35" )?"480p flv, ":              // SD      type=video/x-flv
									fmtUrlPair[0].equals(  "34" )?"360p flv, ":              // SD      type=video/x-flv
									fmtUrlPair[0].equals(  "82" )?"360p 3d mpeg, ":          // SD 3D   type=video/mp4;+codecs="avc1.42001E,+mp4a.40.2"
									fmtUrlPair[0].equals(  "36" )?"240p mpeg 3gpp, ":        // LD      type=video/3gpp;+codecs="mp4v.20.3,+mp4a.40.2"
									fmtUrlPair[0].equals(  "17" )?"114p mpeg 3gpp, ":        // LD      type=video/3gpp;+codecs="mp4v.20.3,+mp4a.40.2"
										
									fmtUrlPair[0].equals(  "46" )?"1080p webm, ":            // HD      type=video/webm;+codecs="vp8.0,+vorbis"
									fmtUrlPair[0].equals(  "45" )?"720p webm, ":             // HD      type=video/webm;+codecs="vp8.0,+vorbis"
									fmtUrlPair[0].equals( "100" )?"1080p 3d webm, ":         // HD 3D   type=video/webm;+codecs="vp8.0,+vorbis"
									fmtUrlPair[0].equals(  "44" )?"480p webm, ":             // SD      type=video/webm;+codecs="vp8.0,+vorbis"
									fmtUrlPair[0].equals(  "43" )?"360p webm, ":             // SD      type=video/webm;+codecs="vp8.0,+vorbis"
									fmtUrlPair[0].equals( "102" )?"360p 3d webm, ":          // SD 3D   type=video/webm;+codecs="vp8.0,+vorbis"
									fmtUrlPair[0].equals( "244" )?"480p webm, ":             // SD      type=video/webm;+codecs="vp9" & size=854x480
									
									fmtUrlPair[0].equals(  "5"  )?"240p flv, " :             // LD      type=video/x-flv
										
									fmtUrlPair[0].equals( "137" )?"1080p mpeg, ":            // HD      type=video/mp4;+codecs="avc1.640028" & size=1920x1080
									fmtUrlPair[0].equals( "136" )?"720p mpeg, ":             // HD      type=video/mp4;+codecs="avc1.4d401f" & size=1280x720
									fmtUrlPair[0].equals( "135" )?"480p mpeg, ":             // SD      type=video/mp4;+codecs="avc1.4d401f" & size=854x480
									fmtUrlPair[0].equals( "134" )?"360p mpeg, ":             // SD      type=video/mp4;+codecs="avc1.4d401e" & size=640x360
									fmtUrlPair[0].equals( "133" )?"240p mpeg, ":             // LD      type=video/mp4;+codecs="avc1.4d4015" & size=426x240 
									fmtUrlPair[0].equals( "160" )?"144p mpeg, ":             // LD      type=video/mp4;+codecs="avc1.42c00c" & size=256x144
									fmtUrlPair[0].equals( "243" )?"360p webm, ":             // LD      type=video/webm;+codecs="vp9"
									fmtUrlPair[0].equals( "242" )?"240p webm, ":             // LD      type=video/webm;+codecs="vp9"
												
									fmtUrlPair[0].equals( "140" )?"mpeg audio only, ":       // ??      type=audio/mp4;+codecs="mp4a.40.2 & bitrate=127949
									fmtUrlPair[0].equals( "171" )?"ogg vorbis audio only, ": // ??      audio/webm;+codecs="vorbis" & bitrate=127949
											
									"unknown resolution! (".concat(fmtUrlPair[0]).concat(") "));
						} catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
							//TODO catch must not be empty
						}
					} 
					
					if (JFCMainClient.FRAME!=null) {
						output(resolutions);
					}
					outputDebugMessage(resolutions);
					
					// TODO move scope into the if blocks
					int index = 0;
					this.nextVideoUrl.removeAllElements();
					
					outputDebugMessage("ssourcecodevideourls.length: ".concat(Integer.toString(sourceCodeVideoUrls.size())));
					// figure out what resolution-button is pressed right now (!!) and fill list with possible URLs of video currently being processed
					
					// try AUDIO ONLY setting before any video resolution
					if (JFCMainClient.getAudioOnly()) {
						if (JFCMainClient.getMpgButtonState()) {
							this.nextVideoUrl.add(index++, new YoutubeUrl(sourceCodeVideoUrls.get("140"),this.url,"AUDIO")); // MP4 AUDIO ONLY  mime=audio/mp4  type=audio/mp4;+codecs=%22mp4a.40.2%22
							this.nextVideoUrl.add(index++, new YoutubeUrl(sourceCodeVideoUrls.get("171"),this.url,"AUDIO")); // WEBM AUDIO ONLY mime=audio/webm type=audio/webm;+codecs=%22vorbis%22
						} else {
							this.nextVideoUrl.add(index++, new YoutubeUrl(sourceCodeVideoUrls.get("171"),this.url,"AUDIO")); // WEBM AUDIO ONLY mime=audio/webm type=audio/webm;+codecs=%22vorbis%22
							this.nextVideoUrl.add(index++, new YoutubeUrl(sourceCodeVideoUrls.get("140"),this.url,"AUDIO")); // MP4 AUDIO ONLY  mime=audio/mp4  type=audio/mp4;+codecs=%22mp4a.40.2%22
						}
					} else {
						switch (JFCMainClient.getIdButtonState()) {
						case 8: // ULTRA HD
							
							// try 1440p, 2160p and 2304p first, if selected in GUI
							if (JFCMainClient.getMpgButtonState()) {
								index = addMPEG_ULTRAHD_Urls(index, sourceCodeVideoUrls);
							}
							if (JFCMainClient.getWebmButtonState()) {
								index = addWEBM_ULTRAHD_Urls(index, sourceCodeVideoUrls);
							}	
							//$FALL-THROUGH$
						case 4: // HD
							// try 1080p/720p in selected format first. if it's not available than the other format will be used 
							if (JFCMainClient.getMpgButtonState()) {
								index=addMPEG_HD_Urls(index,sourceCodeVideoUrls);
							}						
							if (JFCMainClient.getWebmButtonState()) {
								index=addWBEM_HD_Urls(index,sourceCodeVideoUrls);
							}
							// there are no FLV HD URLs for now, so at least try mpg, webm HD then
							index = addMPEG_HD_Urls(index, sourceCodeVideoUrls);
							index = addWBEM_HD_Urls(index, sourceCodeVideoUrls);
							
							//$FALL-THROUGH$
						case 2: // SD
							// try to download desired format first, if it's not available we take the other of same res 
							if (JFCMainClient.getMpgButtonState()) {
								index=addMPEG_SD_Urls(index, sourceCodeVideoUrls);
							}
							if (JFCMainClient.getWebmButtonState()) {
								index=addWBEM_SD_Urls(index,sourceCodeVideoUrls);
							}
							if (JFCMainClient.getFlvButtonState()) {
								index=addFLV_SD_Urls(index, sourceCodeVideoUrls);
							}	
							index = addMPEG_SD_Urls(index, sourceCodeVideoUrls);
							index = addWBEM_SD_Urls(index, sourceCodeVideoUrls);
							index = addFLV_SD_Urls(index, sourceCodeVideoUrls);
	
							//$FALL-THROUGH$
						case 1:	// LD
					
							if (JFCMainClient.getMpgButtonState()) {
								index = addMPEG_LD_Urls(index, sourceCodeVideoUrls);
							}
							if (JFCMainClient.getWebmButtonState()) {
								index = addWEBM_LD_Urls(index, sourceCodeVideoUrls);
							}
							if (JFCMainClient.getFlvButtonState()) {
								index = addFLV_LD_Urls(index, sourceCodeVideoUrls);
							}
							// we must ensure all (16) possible URLs get added so the list of URLs is never empty
							index = addMPEG_LD_Urls(index, sourceCodeVideoUrls);
							index = addWEBM_LD_Urls(index, sourceCodeVideoUrls);
							index = addFLV_LD_Urls(index, sourceCodeVideoUrls);
	
							break;
						default:
							//this.vNextVideoURL = null;
							this.videoUrl = null;
							break;
						}
					}
					// if the first 2 entries are null than there are no URLs for the selected resolution
					// strictly speaking this is only true for HD as there are only two URLs in contrast to three of SD - in this case the output will not be shown but downloading should work anyway
					// 2014-01-25 since there is another block of video urls (including one for audio only) and a new list of itags, we check only the first entry
					try {
						if(this.nextVideoUrl.get(0).getUrl()==null) {
							String smsg = JFCMainClient.isGerman()?"Video URL für ausgewählte Auflösung nicht gefunden! versuche geringere Auflösung...":"could not find video url for selected resolution! trying lower res...";
							output(smsg); outputDebugMessage(smsg);
						}
					} catch (java.lang.ArrayIndexOutOfBoundsException aioob) {
						String smsg = JFCMainClient.isGerman()?"Video URL für ausgewählte Auflösung nicht gefunden! versuche geringere Auflösung...":"could not find video url for selected resolution! trying lower res...";
						output(smsg); outputDebugMessage(smsg);

					} 
					
					// remove null entries in list - we later try to download the first (index 0) and if it fails the next one (at index 1) and so on
					for (int x = this.nextVideoUrl.size() - 1; x >= 0; x--) {
						if (this.nextVideoUrl.get(x).getUrl() == null) {
							this.nextVideoUrl.remove(x);
						}
					}
					
					try {
						this.videoUrl = this.nextVideoUrl.get(0).getUrl();
						outputDebugMessage(String.format("trying this one: %s %s %s %s",this.nextVideoUrl.get(0).getHtmlTagId(),this.nextVideoUrl.get(0).getQuality(),this.nextVideoUrl.get(0).getSize(),this.nextVideoUrl.get(0).getHtmlType()));
					} catch (ArrayIndexOutOfBoundsException aioobe) {
						//TODO  catch must not be empty
					}
					
					this.setTitle( this.getTitle()
							.concat(JFCMainClient.isSaveIdInFilename()?"."+this.nextVideoUrl.get(0).getYoutubeId():"")
							.concat(!this.nextVideoUrl.get(0).getRespart().equals("")?"."+this.nextVideoUrl.get(0).getRespart():""));
					// java.lang.ArrayIndexOutOfBoundsException would be thrown here if no known resolution was found until here

				}
				
				if (this.recursionCount==0 && line.matches("(.*)<meta name=\"title\" content=(.*)")) {
					String stmp = line.replaceFirst("(.*)<meta name=\"title\" content=", "").trim();
					// change html characters to their UTF8 counterpart
					stmp = UTF8.changeHTMLtoUTF8(stmp);
					stmp = stmp.replaceFirst("^\"", "").replaceFirst("\">$", "");
					
					// http://msdn.microsoft.com/en-us/library/windows/desktop/aa365247%28v=vs.85%29.aspx
					// 
					stmp = stmp.replaceAll("<", "");
					stmp = stmp.replaceAll(">", "");
					stmp = stmp.replaceAll(":", "");
					stmp = stmp.replaceAll("/", " ");
					stmp = stmp.replaceAll("\\\\", " ");
					stmp = stmp.replaceAll("|", "");
					stmp = stmp.replaceAll("\\?", "");
					stmp = stmp.replaceAll("\\*", "");
					stmp = stmp.replaceAll("/", " ");
					stmp = stmp.replaceAll("\"", " ");
					stmp = stmp.replaceAll("%", "");
										
					this.setTitle( stmp ); // complete file name without path 
				}

			} catch (NullPointerException npe) {
			}
		} 
		return rc;
	} 

	private void saveBinaryData() throws IOException {

		FileOutputStream fos = null;
		
		try {
			File file; 
			Integer idUpCount = 0;
			String directoryChoosed;

			directoryChoosed=(String) JFCMainClient.CONFIG.getProperty("savefolder");

    		outputDebugMessage("title: ".concat(this.getTitle()).concat("sfilename: ").concat(this.getTitle()));
			do {
				Vector<String> fileNames = getFileNames(this.getTitle(), this.response.getFirstHeader("Content-Type").getValue());

				file = new File(directoryChoosed, fileNames.get(0).concat((idUpCount>0?"(".concat(idUpCount.toString()).concat(")"):"")).concat(".").concat(fileNames.get(1)));
				idUpCount += 1;
			} while (file.exists());
			this.setFileName(file.getAbsolutePath());
			
			Long bytesReadSum = (long) 0;
			Long percentage = (long) -1;
			Long bytesMax = Long.parseLong(this.response.getFirstHeader("Content-Length").getValue());
			fos = new FileOutputStream(file);
			
			outputDebugMessage(String.format("writing %d bytes to: %s",bytesMax,this.getFileName()));
			output((JFCMainClient.isGerman()?"Dateigröße von \"":"file size of \"").concat(this.getTitle()).concat("\" = ").concat(bytesMax.toString()).concat(" Bytes").concat(" ~ ").concat(Long.toString((bytesMax/1024)).concat(" KiB")).concat(" ~ ").concat(Long.toString((bytesMax/1024/1024)).concat(" MiB")));
		    
			byte[] bytes = new byte[4096];
			Integer bytesRead = 1;
			
			// adjust blocks of percentage to output - larger files are shown with smaller pieces
			Integer blocks = 10; if (bytesMax>20*1024*1024) blocks=4; if (bytesMax>32*1024*1024) blocks=2; if (bytesMax>56*1024*1024) blocks=1;
			while (!this.isInterrupted && bytesRead>0) {
				bytesRead = this.binaryReader.read(bytes);
				bytesReadSum += bytesRead;
				
				// update percentage of download 
				if ( (((bytesReadSum*100/bytesMax) / blocks) * blocks) > percentage ) {
					percentage = (((bytesReadSum*100/bytesMax) / blocks) * blocks);
					this.youtubeUrl.setPercentage(percentage.intValue());
					
					JFCMainClient.updateYoutubeUrlInList(this.youtubeUrl);
					 
				}
				// TODO calculate and show ETA for bigger downloads (remaining time > 60s) - every 20%?
				
				try {fos.write(bytes,0,bytesRead);} catch (IndexOutOfBoundsException ioob) {}
				this.isInterrupted = JFCMainClient.getQuitRequested(); // try to get information about application shutdown
			} 
			this.youtubeUrl.setDownloadingFinished();
			JFCMainClient.updateYoutubeUrlInList(this.youtubeUrl);
			
			// rename files if download was interrupted before completion of download
			if (this.isInterrupted && bytesReadSum<bytesMax) {
				try {
					// this part is especially for our M$-Windows users because of the different behavior of File.renameTo() in contrast to non-windows
					// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6213298  and others
					// even with Java 1.6.0_22 the renameTo() does not work directly on M$-Windows! 
					fos.close();
				} catch (Exception e) {
				}
//				System.gc(); // we don't have to do this but to be sure the file handle gets released we do a thread sleep 
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
		         
				// this part runs on *ix platforms without closing the FileOutputStream explicitly
				this.httpClient.close(); // otherwise binaryreader.close() would cause the entire datastream to be transmitted 
				outputDebugMessage(String.format("download canceled. (%d)",(bytesRead)));
				changeFileNamewith("CANCELED.");
				String message = (JFCMainClient.isGerman()?"benenne unfertige Datei um zu: ":"renaming unfinished file to: ").concat( this.getFileName() );
				output(message); outputDebugMessage(message);
				
				// CANCELED filenames overwrite others as we do not test for CANCELED one, two...
				if (!file.renameTo(new File( this.getFileName()))) {
					message = (JFCMainClient.isGerman()?"Fehler beim Umbenennen der unfertigen Datei zu: ":"error renaming unfinished file to: ").concat( this.getFileName() );
    				output(message); outputDebugMessage(message);
				}
			}
			outputDebugMessage("done writing.");
		} catch (FileNotFoundException fnfe) {
			throw(fnfe)		;
		} catch (IOException ioe) {
			outputDebugMessage("IOException");
			throw(ioe);
		} finally {
			this.videoUrl = null;
			try {
				fos.close();
			} catch (Exception e) {
			}
            try {
				this.textReader.close();
			} catch (Exception e) {
			}
            try {
				this.binaryReader.close();
			} catch (Exception e) {
			}
		}
	} 

	private void changeFileNamewith(String string) {
		
		File file = null;
		Integer idUpCount = 0;
		String fileSeparator = System.getProperty("file.separator");
		
		if (fileSeparator.equals("\\")) {
			fileSeparator += fileSeparator; // on m$-windows we need to escape the \
		}

		String directoryChoosed="";
		String[] renFileName = this.getFileName().split(fileSeparator);
		
		try {
			for (int i = 0; i < renFileName.length-1; i++) {
				directoryChoosed += renFileName[i].concat((i<renFileName.length-1)?fileSeparator:""); // constructing folder where file is saved now (could be changed in GUI already)
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {}
		
		String fileName = renFileName[renFileName.length-1];
		outputDebugMessage("changeFileNamewith() sfilename: ".concat(fileName));
		do {
			 // filename will be prepended with a parameter string and possibly with a duplicate counter
			file = new File(directoryChoosed, string.concat((idUpCount>0?"(".concat(idUpCount.toString()).concat(")"):"")).concat(fileName));
			idUpCount += 1;
		} while (file.exists());
		
		outputDebugMessage("changeFileNamewith() new filename: ".concat(file.getAbsolutePath()));
		this.setFileName(file.getAbsolutePath());
		
	} 

	public String getProxy() {
		String sproxy = JFCMainClient.PROXY;
		if (sproxy==null) return(""); else return(sproxy);
	}  

	public String getTitle() {
		if (this.title != null) return this.title; else return("");
	} 

	public void setTitle(String sTitle) {
		this.title = sTitle;
	} 
	
	public String getFileName() {
		if (this.fileName != null) {
			return this.fileName;
		}  else {
			return("");
		}
	} 

	public void setFileName(String sFileName) {
		this.fileName = sFileName;
	} 

	private synchronized void outputDebugMessage (String s) {
		
		if (!JFCMainClient.getDebug()) {
			return;
		}
			
		// sometimes this happens:  Exception in thread "Thread-2" java.lang.Error: Interrupted attempt to aquire write lock (on quit only)
		try {
			JFCMainClient.addTextToConsole("#DEBUG ".concat(this.getMyName()).concat(" ").concat(s));
		} catch (Exception e) {
			try { 
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				//TODO catch must not be empty
			}
			try { 
				JFCMainClient.addTextToConsole("#DEBUG ".concat(this.getMyName()).concat(" ").concat(s)); 
			} catch (Exception e2) {
				//TODO catch must not be empty				
			}
		}
	}
	
	private void output (String s) {
		if (JFCMainClient.getDebug()) {
			return;
		}
		JFCMainClient.addTextToConsole("#info - ".concat(s));
	}
	
	public String getMyName() {
		return this.getClass().getName().concat(Integer.toString(this.threadNumber));
	}
			
	public void run() {
		boolean downloadOK = false;
		while (!this.isInterrupted) {
			try {
				synchronized (JFCMainClient.DLM) {
//					debugoutput("going to sleep.");
					JFCMainClient.DLM.wait(1000); // check for new URLs (if they got pasted faster than threads removing them)
//					debugoutput("woke up ".concat(this.getClass().getName()));
					this.isInterrupted = JFCMainClient.getQuitRequested(); // if quit was pressed while this threads works it would not get the InterruptedException and therefore prevent application shutdown
					
//					debugoutput("URLs remain in list: ".concat(Integer.toString(JFCMainClient.dlm.size())));
					// running in CLI mode?
					if (JFCMainClient.FRAME == null) {
						if (JFCMainClient.DLM.size() == 0) {
							outputDebugMessage(this.getMyName().concat(" ran out of work."));
							if (YTDownloadThread.THREAD_COUNT == 0) {
								// this is the last DownloadThread so shutdown Application as well
								outputDebugMessage("all DownloadThreads ended. shuting down ytd2.");
								JFCMainClient.shutdownApp();
							} else {
								// this is not the last DownloadThread so shutdown this thread only
								this.isInterrupted = true;
								outputDebugMessage("end this thread.");
								throw new NullPointerException("end this thread.");
							}
						}
					}
				
					this.youtubeUrl = JFCMainClient.getFirstYoutubeUrlFromList();
					this.url = this.youtubeUrl.toString();
					output((JFCMainClient.isGerman()?"versuche herunterzuladen: ":"try to download: ").concat(this.url));
					
				}

				this.youtubeUrl.setDownloading();
				JFCMainClient.updateYoutubeUrlInList(this.youtubeUrl);
				
				this.noDownload = JFCMainClient.getNoDownload(); // copy ndl-state because this thread should end with a complete file (and report so) even if someone switches to no-download before this thread is finished
				
				// download one webresource and show result
				downloadOK = downloadOne(this.youtubeUrl.getUrl()); this.recursionCount=-1;
				if (downloadOK && !this.noDownload)
					output((JFCMainClient.isGerman()?"fertig heruntergeladen: ":"download complete: ").concat("\"").concat(this.getTitle()).concat("\"").concat(" to ").concat(this.getFileName()));
				else
					output((JFCMainClient.isGerman()?"Nicht heruntergeladen: ":"not downloaded: ").concat("\"").concat(this.getTitle()).concat("\"")); // not downloaded does not mean it was erroneous
				
				// running in CLI mode?
				if (JFCMainClient.FRAME == null)
					JFCMainClient.removeYoutubeUrlFromList(this.youtubeUrl); // JFCMainClient.removeURLFromList(this.sURL);
				else
					JFCMainClient.removeYoutubeUrlFromList(this.youtubeUrl); // JFCMainClient.removeURLFromList(JFCMainClient.szDLSTATE.concat(this.sURL));
				
			} catch (InterruptedException e) {
				this.isInterrupted = true;
			} catch (NullPointerException npe) {
				//TODO catch must not be empty	- debugoutput("npe - nothing to download?");
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} 
		outputDebugMessage("thread ended: ".concat(this.getMyName()));
		YTDownloadThread.THREAD_COUNT--;
	} 
} 