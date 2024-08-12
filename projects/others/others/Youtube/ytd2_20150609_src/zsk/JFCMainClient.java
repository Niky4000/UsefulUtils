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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.cli.Options;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * knoedel@section61:~/Development/ytd2/ytd2> echo " *" `egrep -v "(^\s*(\/\*|\*|//)|^\s*$)" src/zsk/*java | wc -l` java code lines && echo -e " *" `egrep "(^\s*(\/\*|\*|//)|^\s*$)" src/zsk/*java | wc -l` empty/comment lines "\n *"
 * 2165 java code lines
 * 778 empty/comment lines 
 *  
 * knoedel@section61:~/Development/ytd2/ytd2> date && uname -a && lsb_release -a && java -version
 * Sun May  3 15:41:23 CEST 2015
 * Linux section61 3.16.7-21-desktop #1 SMP PREEMPT Tue Apr 14 07:11:37 UTC 2015 (93c1539) x86_64 x86_64 x86_64 GNU/Linux
 * LSB Version:    core-2.0-noarch:core-3.2-noarch:core-4.0-noarch:core-2.0-x86_64:core-3.2-x86_64:core-4.0-x86_64:desktop-4.0-amd64:desktop-4.0-noarch:graphics-2.0-amd64:graphics-2.0-noarch:graphics-3.2-amd64:graphics-3.2-noarch:graphics-4.0-amd64:graphics-4.0-noarch
 * Distributor ID: openSUSE project
 * Description:    openSUSE 13.2 (Harlequin) (x86_64)
 * Release:        13.2
 * Codename:       Harlequin
 * openjdk version "1.8.0_45"
 * OpenJDK Runtime Environment (build 1.8.0_45-b14)
 * OpenJDK 64-Bit Server VM (build 25.45-b02, mixed mode)
 * 
 * using Eclipse 3.5/3.6/3.7/4.2/4.3.2 and Oracle Jave + Maven
 * Apache SonarQube 4.5
 * TODOs are for Eclipse IDE - Tasks View
 * 
 * tested on GNU/Linux Oracle Java & IBM OpenJDK JRE 1.6-/1.8 64bit, M$-Windows XP 64bit JRE 1.6.0 32&64Bit and M$-Windows 7 32Bit JRE 1.6.0 32Bit, M$-Windows 7 64 Bit JRE 1.7.0 64Bit
 * using Mozilla Firefox 3.6-37, Google Chrome (38), M$-IE (8-11)
 * 
 * 
 * NEW PROBLEMS 2015:
 * - mpeg video and mpeg audio are separated in two streams
 * - some videos are filtered and are not playable unchanged (vlc filter Wall 1:1 corrects this)
 * 
 * 
 * source code compliance level is 1.5
 * java files are UTF-8 encoded
 * javac showd no warning
 * SonarQube 4.5.4 LTS .. :)
 *
 * @author Stefan "MrKnödelmann" K.
 *
 */
public class JFCMainClient extends JFrame implements ActionListener, WindowListener, DocumentListener, ChangeListener, DropTargetListener, ItemListener {
	public static final String VERSION = "V20150609_2340 by MrKnödelmann";
	
	private static final long serialVersionUID = 6791957129816930254L;

	private static final String NEWLINE = "\n";
	
	// more or less (internal) output
	// set to True or add 'd' after mod-time in szVersion
	private boolean isDebug = JFCMainClient.VERSION.matches("V[0-9]+_[0-9]+d.*");
	
	// just report file size of HTTP header - don't download binary data (the video)
	private boolean noDownload = this.isDebug;

	// save diskspace - try to download e.g. 720p before 1080p if HD is set
	public static boolean SAVE_DISKSPACE = false;
	
	// ONLY download audio file, the one with a video stream in it (if available), only applies to adaptive format URLs
	private boolean audioOnly = false;
	
	// append youtube ID (http://www.youtube.com/watch?v=XY) to filename 
	public static boolean SAVE_ID_IN_FILENAME = false;

	public static String PROXY = null;
	public static String[] ARGS = null;
	
	public static String DOWNLOAD_STATE = "downloading ";
	
	// something like [http://][www.]youtube.[cc|to|pl|ev|do|ma|in]/watch?v=0123456789A   or  youtu.be/9irsg1vBmq0
	public static final String YOUTUBE_REGEX = "(?i)^(http(s)?://)?(www\\.)?(youtube\\..{2,5}/watch\\?v=|youtu\\.be/)[^&]{11}"; // http://de.wikipedia.org/wiki/CcTLD
	
	// RFC-1123 ? hostname [with protocol]	
	public static final String PROXY_REGEX = "(?i)(^(http(s)?://)?([a-z0-9]+:[a-z0-9]+@)?([a-z0-9\\-]{0,61}[a-z0-9])(\\.([a-z0-9\\-]{0,61}[a-z0-9]))*(:[0-90-90-90-9]{1,4})?$)|()";
	
	// RFC-1738 URL characters - not a regex for a real URL!!
	public static final String URL_REGEX = "(?i)^(http(s)?://)?[a-z0-9;/\\?@=&\\$\\-_\\.+!*\\'\\(\\),%]+$"; // without ":", plus "%"
	
	// TODO tor https://www.youtube.com/watch?v=z-rftpZ7kCY
	
	private static final String PLAYLIST_REGEX = "(?i)/view_play_list\\?p=([a-z0-9]*)&playnext=[0-9]{1,2}&v=";
	
	static JFCMainClient FRAME = null;

	private static Boolean QUIT_REQUESTED = false;
	
	static YTDownloadThread THREAD_1;
	static YTDownloadThread THREAD_2;
	static YTDownloadThread THREAD_3;
	static YTDownloadThread THREAD_4;
	static YTDownloadThread THREAD_5;
	static YTDownloadThread THREAD_6;
	
	private JPanel panel = null;
	private JSplitPane middlePane = null;
	private JTextArea textArea = null;
	private JList<YoutubeUrl> urlList = null;
	private JButton quitButton = null;
	private JButton directoryButton = null;
	private JTextField directoryTextField = null;
	private JTextField textInputField = null;
	private JPopupMenu popup = null;
	private JRadioButton fourkButton = null;
	private JRadioButton hdButton = null;
	private JRadioButton stdButton = null;
	private JRadioButton ldButton = null;
	private JRadioButton mpgButton = null;
	private JRadioButton flvButton = null;
	private JRadioButton webmButton = null;
	private JCheckBox saveConfigCheckBox = null;
	private JCheckBox save3dCheckBox = null;
	 
	static DefaultListModel<YoutubeUrl> DLM = null;
	
	final static String CONFIG_FILE = "ytd2.config.xml";
	static XMLConfiguration CONFIG = null;
	
	static boolean IS_CLI = false;

	enum CLI_DownloadQuality { LD, SD, HD } ;
	static CLI_DownloadQuality CLI_DOWNLOAD_QUALITIES ;
	enum CLI_DownloadFormat { MPG, WEBM, FLV };
	static CLI_DownloadFormat CLI_DOWNLOAD_FORMATS; 
	
	static Options CLI_OPTIONS = new Options();
	
	
    class PopupListener extends MouseAdapter {
        JPopupMenu popup;
 
        PopupListener(JPopupMenu popupMenu) {
            this.popup = popupMenu;
        }
 
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
 
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
 
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                this.popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
    }
	
	static {
		initializeOptions();
	}

	// by Benjamin Reed for Mac OS
	private static void initializeOptions() {
//		sCLIOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("This help.").create('?'));
//		sCLIOptions.addOption(OptionBuilder.withLongOpt("debug").withDescription("Enable debug output.").create('d'));
//		sCLIOptions.addOption(OptionBuilder.withDescription("Dowload low-quality video. (default: standard-quality)").withLongOpt("low").create('l'));
//		sCLIOptions.addOption(OptionBuilder.withDescription("Dowload high-quality video. (default: standard-quality)").withLongOpt("high").create('h'));
//		sCLIOptions.addOption(OptionBuilder.withDescription("FLV/WEBM format. (default: MPEG)").withLongOpt("flv").create('f'));
	}
	
	public static synchronized Boolean getQuitRequested() {
		return QUIT_REQUESTED;
	}


	public synchronized static void setQuitRequested(Boolean quitRequested) {
		JFCMainClient.QUIT_REQUESTED = quitRequested;
	}
	
	/**
	 * get state of downloadbutton as Integer 
	 * 
	 * @return
	 */
	public synchronized static int getIdButtonState() {
		if (IS_CLI) {
			return ((JFCMainClient.CLI_DOWNLOAD_QUALITIES==JFCMainClient.CLI_DownloadQuality.HD?4:0) + (JFCMainClient.CLI_DOWNLOAD_QUALITIES==JFCMainClient.CLI_DownloadQuality.SD?2:0) + (JFCMainClient.CLI_DOWNLOAD_QUALITIES==JFCMainClient.CLI_DownloadQuality.LD?1:0));
		} else {
			return ((JFCMainClient.FRAME.fourkButton.isSelected()?8:0) + (JFCMainClient.FRAME.hdButton.isSelected()?4:0) + (JFCMainClient.FRAME.stdButton.isSelected()?2:0) + (JFCMainClient.FRAME.ldButton.isSelected()?1:0));
		}
	}

	
	/**
	 * get state of formatbutton for mpg as Boolean 
	 * 
	 * @return
	 */
	public synchronized static Boolean getMpgButtonState() {
		if (IS_CLI) {
			return (JFCMainClient.CLI_DOWNLOAD_FORMATS==JFCMainClient.CLI_DownloadFormat.MPG);
		} else {
			return (JFCMainClient.FRAME.mpgButton.isSelected()); 
		}
	} 
	
	/**
	 * get state of formatbutton for flv as Boolean 
	 * 
	 * @return
	 */
	public synchronized static Boolean getFlvButtonState() {
		if (IS_CLI) {
			return (JFCMainClient.CLI_DOWNLOAD_FORMATS==JFCMainClient.CLI_DownloadFormat.FLV);
		} else {
			return (JFCMainClient.FRAME.flvButton.isSelected()); 
		}
	} 
	
	/**
	 * get state of formatbutton for webm as Boolean 
	 * 
	 * @return
	 */
	public synchronized static Boolean getWebmButtonState() {
		if (IS_CLI) {
			return (JFCMainClient.CLI_DOWNLOAD_FORMATS==JFCMainClient.CLI_DownloadFormat.WEBM);
		} else {
			return (JFCMainClient.FRAME.webmButton.isSelected()); 
		}
	} 


	/**
	 * get state of 3dbutton as Boolean 
	 * 
	 * @return
	 */
	public synchronized static Boolean get3DButtonState() {
		if (IS_CLI) {
			return (false);
		} else {
			return (JFCMainClient.FRAME.save3dCheckBox.isSelected()); 
		}
	}	
	
	/**
	 * append text to textarea
	 * 
	 * @param Object o
	 */
	public static void addTextToConsole( Object o ) {
		try {
			// append() is threadsafe
			JFCMainClient.FRAME.textArea.append( o.toString().concat( NEWLINE ) );
			JFCMainClient.FRAME.textArea.setCaretPosition( JFCMainClient.FRAME.textArea.getDocument().getLength() );
			JFCMainClient.FRAME.textInputField.requestFocusInWindow();
			System.out.println(o);
		} catch (NullPointerException npe) {
			// for CLI run only
			System.out.println(o);
		} catch (Exception e) {
			@SuppressWarnings( "unused" ) // for debuging
			String s = e.getMessage();
		}
	} 
	
	
	public static void addYoutubeUrlToList( String name ) {
		synchronized (JFCMainClient.DLM) {
			JFCMainClient.DLM.addElement( new YoutubeUrl(name) );
			debugOutput("notify() ");
			JFCMainClient.DLM.notify();
		}
	} // addYoutubeUrlToList()
	
	
	public static void addYoutubeUrlToList( String name, String title ) {
		YoutubeUrl yturl = new YoutubeUrl(name);
		yturl.setTitle(title);
		synchronized (JFCMainClient.DLM) {
			JFCMainClient.DLM.addElement( yturl );
			debugOutput("notify() ");
			JFCMainClient.DLM.notify();
		}
	} // addYoutubeUrlToList()
	
	public static void addYoutubeUrlToList( String name, String title, String respart ) {
		YoutubeUrl yturl = new YoutubeUrl(name);
		yturl.setTitle(title);
		yturl.setRespart(respart);
		synchronized (JFCMainClient.DLM) {
			JFCMainClient.DLM.addElement( yturl );
			debugOutput("notify() ");
			JFCMainClient.DLM.notify();
		}
	} // addYoutubeUrlToList()
	
	public static void updateYoutubeUrlInList( YoutubeUrl youtubeUrl ) {
		synchronized (JFCMainClient.DLM) {
			try {
				int i = JFCMainClient.DLM.indexOf(youtubeUrl);
				JFCMainClient.DLM.setElementAt(youtubeUrl, i);
			} catch (IndexOutOfBoundsException ioobe) {}
		}
	} 

	public static void removeUrlFromList( String name ) {
		synchronized (JFCMainClient.DLM) {
			try {
				int i = JFCMainClient.DLM.indexOf( name );
				JFCMainClient.DLM.remove( i );
			} catch (IndexOutOfBoundsException ioobe) {}
		}
	} 
	
	public static void addYoutubeUrlToList( YoutubeUrl youtubeUrl ) {
		synchronized (JFCMainClient.DLM) {
			JFCMainClient.DLM.addElement( youtubeUrl );
			debugOutput("notify() ");
			JFCMainClient.DLM.notify();
		}
	} // addYoutubeUrlToList()
	
	public static void removeYoutubeUrlFromList( YoutubeUrl youtubeUrl ) {
		synchronized (JFCMainClient.DLM) {
			try {
				int i = JFCMainClient.DLM.indexOf( youtubeUrl );
				JFCMainClient.DLM.remove( i );
			} catch (IndexOutOfBoundsException ioobe) {}
		}
	} 
	
//	public static YoutubeUrl getFirstUrlFromList( ) {
//		YoutubeUrl yurl = null;
//		synchronized (JFCMainClient.DLM) {
//			try {
//				int i;
//				// try to find the index of an URL entry in the list without sate downloading
//				for ( i = 0; i < JFCMainClient.DLM.getSize(); i++) {
//					//if (!(JFCMainClient.DLM.get(i).getUrl()).startsWith( JFCMainClient.DOWNLOAD_STATE )) break;
//					if (!(JFCMainClient.DLM.get(i).isDownloading())) break;
//				}
//				//yurl = (JFCMainClient.DLM.get(i).getUrl()).replaceFirst( JFCMainClient.DOWNLOAD_STATE, "" );
//				JFCMainClient.DLM.get(i).setDownloading();
//				yurl = JFCMainClient.DLM.get(i);
//						
//			} catch (IndexOutOfBoundsException ioobe) {}
//		}
//		return yurl;
//	} 
	
	public static YoutubeUrl getFirstYoutubeUrlFromList( ) {
		YoutubeUrl youtubeUrl = null;
		synchronized (JFCMainClient.DLM) {
			try {
				int i;
				// try to find the index of an URL entry in the list without "downloading " at the beginning
				for ( i = 0; i < JFCMainClient.DLM.getSize(); i++) {
					if (!(JFCMainClient.DLM.get(i).isDownloading())) break;
				}
				youtubeUrl = JFCMainClient.DLM.get(i);
			} catch (IndexOutOfBoundsException ioobe) {}
		}
		return youtubeUrl;
	} // getFirstYoutubeUrlFromList()

	public static void clearUrlList() {
		try {
			synchronized (JFCMainClient.DLM) {
				JFCMainClient.DLM.clear();
			}
		} catch (NullPointerException npe) {}
	} 
	
	public static boolean isGerman() {
		return Locale.getDefault().toString().startsWith("de_") || (JFCMainClient.getDebug() && System.getProperty("user.home").equals("/home/knoedel"));
	} 

	public void setFocusToTextField() {
		this.textInputField.requestFocusInWindow();
	} 
	
	static void saveConfigFile() {
		debugOutput("saveConfigFile()");
		
		if (JFCMainClient.FRAME == null)
			return;
		
		if (JFCMainClient.FRAME.saveConfigCheckBox.isSelected()) {

//			String sdirectorychoosed;
//			try {
//				synchronized (JFCMainClient.frame.directorytextfield) {
//					sdirectorychoosed = JFCMainClient.frame.directorytextfield.getText();
//				}
//			} catch (NullPointerException npe) {
//				// for CLI-only run
//				sdirectorychoosed = JFCMainClient.shomedir;
//			}

			// if config wasn't saved before create a new file
			if (CONFIG.getFile() == null)
				CONFIG.setFile(new File(JFCMainClient.CONFIG_FILE));

			CONFIG.setProperty("http_proxy", PROXY);
			CONFIG.setProperty("targetfolder", CONFIG.getProperty("savefolder"));
			CONFIG.setProperty("videobutton", getIdButtonState());
			CONFIG.setProperty("mpgbutton", getMpgButtonState());
			try {
				JFCMainClient.CONFIG.save();
				// debugoutput will not work here anymore because the GUI does not get updated - output is for CLI-run only
				debugOutput("config file written: ".concat(CONFIG.getBasePath()));
			} catch (ConfigurationException e) {
				debugOutput("error writing config file: ".concat(CONFIG.getBasePath()));
			}
		}

	} 
	
	public static void shutdownApp() {
		// try to save settings to an xml-file
		saveConfigFile();
		
		// running downloads are difficult to terminate (Thread.isInterrupted() does not work here)
		JFCMainClient.setQuitRequested(true);
		debugOutput("bQuitrequested = true");
		
		// TODO mayby we use a threadpool here?!
		try {JFCMainClient.THREAD_1.interrupt();} catch (NullPointerException npe) {}
		try {JFCMainClient.THREAD_2.interrupt();} catch (NullPointerException npe) {}
		try {JFCMainClient.THREAD_3.interrupt();} catch (NullPointerException npe) {}
		try {JFCMainClient.THREAD_4.interrupt();} catch (NullPointerException npe) {}
		try {JFCMainClient.THREAD_5.interrupt();} catch (NullPointerException npe) {}
		try {JFCMainClient.THREAD_6.interrupt();} catch (NullPointerException npe) {}
		try {
			try {JFCMainClient.THREAD_1.join();} catch (NullPointerException npe) {} 
			try {JFCMainClient.THREAD_2.join();} catch (NullPointerException npe) {} 
			try {JFCMainClient.THREAD_3.join();} catch (NullPointerException npe) {} 
			try {JFCMainClient.THREAD_4.join();} catch (NullPointerException npe) {}
			try {JFCMainClient.THREAD_5.join();} catch (NullPointerException npe) {}
			try {JFCMainClient.THREAD_6.join();} catch (NullPointerException npe) {}
		} catch (InterruptedException ie) {}
		
		// in cli-mode we do not exit as the shutdown hookhandler would run circular
		if (JFCMainClient.FRAME != null) {
			debugOutput( "quit." );
			output( "quit." );
			System.exit( 0 );
		}
	} 
	
	
	/**
	 * process events of ActionListener
	 * 
	 */
	public void actionPerformed( final ActionEvent e ) {
		if (e.getSource().equals( FRAME.textInputField )) {
			if (!e.getActionCommand().equals( "" )) { 
				if (e.getActionCommand().matches(YOUTUBE_REGEX))
					addYoutubeUrlToList(e.getActionCommand());
				else {
					addTextToConsole(e.getActionCommand());
					cli(e.getActionCommand().toLowerCase());
				}
			}
			synchronized (FRAME.textInputField) {
				FRAME.textInputField.setText("");				
			}
			return;
		}
		
		// let the user choose another dir
		if (e.getSource().equals( FRAME.directoryButton )) {
			debugOutput("frame.directorybutton");
			JFileChooser fc = new JFileChooser();
			fc.setMultiSelectionEnabled(false);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			synchronized (FRAME.directoryTextField) {
				// we have to set current directory here because it does get lost when fc is lost
				fc.setCurrentDirectory( new File( FRAME.directoryTextField.getText()) );
			}
			debugOutput("current dir: ".concat( fc.getCurrentDirectory().getAbsolutePath()) );
			if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			String snewdirectory = fc.getSelectedFile().getAbsolutePath();
			// append file.separator if last character is not file.separator (the user choosed a directory other than root)
			snewdirectory.concat(snewdirectory.endsWith(System.getProperty("file.separator"))?"":System.getProperty("file.separator"));
			File ftest = new File(snewdirectory);
			if (ftest.exists()) {
				if (ftest.isDirectory()) {
					synchronized (FRAME.directoryTextField) {
						FRAME.directoryTextField.setText( snewdirectory );
					}
					CONFIG.setProperty("savefolder", snewdirectory);
					debugOutput("new current dir: ".concat( fc.getCurrentDirectory().getAbsolutePath()) );
					output((isGerman()?"neues aktuelles Verzeichnis: ":"new current dir: ").concat( fc.getCurrentDirectory().getAbsolutePath()) );
				} else {
					output("not a directory: ".concat(snewdirectory));
				}
			} else {
				output("directory does not exist: ".concat(snewdirectory));
			}
			return;
		}
		
		// let the user choose another download resolution
		if (e.getActionCommand().equals(JFCMainClient.FRAME.fourkButton.getActionCommand()) ||
				e.getActionCommand().equals(JFCMainClient.FRAME.hdButton.getActionCommand()) || 
				e.getActionCommand().equals(JFCMainClient.FRAME.stdButton.getActionCommand()) ||
				e.getActionCommand().equals(JFCMainClient.FRAME.ldButton.getActionCommand()) ) {
			debugOutput("trying: ".concat(e.getActionCommand()));
			output("trying: ".concat( e.getActionCommand().toUpperCase() ));
			
			return;
		}
		
		// let the user choose another video format
		if (e.getActionCommand().equals(JFCMainClient.FRAME.mpgButton.getActionCommand()) ||
			e.getActionCommand().equals(JFCMainClient.FRAME.flvButton.getActionCommand()) || 
			e.getActionCommand().equals(JFCMainClient.FRAME.webmButton.getActionCommand()) ) {
			debugOutput("trying: ".concat(e.getActionCommand()));
			output("trying: ".concat( e.getActionCommand().toUpperCase().concat( "  ".concat(isGerman()?"(Dateien werden immer nach MIME-Typ benannt!)":"(files will always be named according to MIME type!)")) ));
			return;
		} 
		
		if (e.getActionCommand().equals( "quit" )) {
			addTextToConsole(isGerman()?"Programmende ausgewählt - beende DownloadThreads, Vorgang kann eine Weile dauern!":"quit requested - signaling donwload threads to terminate, this may take a while!");
			// seems to have to effect:
			//JFCMainClient.frame.repaint();
			JFCMainClient.shutdownApp();
			return;
		}
		
		if (e.getActionCommand().equals("paste")) {
			JFCMainClient.FRAME.textInputField.paste();
			return;
		}

		if (e.getActionCommand().equals("clear")) {
			if (JFCMainClient.FRAME.textInputField.getSelectedText() == null) JFCMainClient.FRAME.textInputField.selectAll();
			
			JFCMainClient.FRAME.textInputField.replaceSelection("");
			return;
		}
		
		if (e.getActionCommand().equals("debug")) {
			JFCMainClient.FRAME.textArea.setFocusable(true);
			JFCMainClient.FRAME.textArea.setCaretPosition(0);
			JFCMainClient.FRAME.textArea.selectAll();
			JFCMainClient.FRAME.textArea.copy();
			JFCMainClient.FRAME.textArea.select(0,0);
			JFCMainClient.FRAME.textArea.setFocusable(false);
			
			JFCMainClient.FRAME.textArea.copy();

			return;
		}

		
		debugOutput("unknown action. ".concat(e.getSource().toString())); 
		output("unbekannte Aktion");
		
	} // actionPerformed()
	
	/**
	 * process input events
	 * 
	 * @param scmd
	 */
	void cli(String scmd) {
		if (scmd.matches("^(hilfe|help|[h|\\?])")) {
			addTextToConsole("debug[ on| off]\t: ".concat(JFCMainClient.isGerman()?"mehr oder weniger (interne) Ausgaben":"more or less (internal) output"));
			addTextToConsole("help|h|?]\t\t: ".concat(JFCMainClient.isGerman()?"zeige diesen Text":"show this text"));
			addTextToConsole("id[ on| off]\t\t: ".concat(JFCMainClient.isGerman()?"Video ID im Dateinamen vermerken":"append video id to filename"));
			addTextToConsole("ndl[ on| off]\t\t: ".concat(JFCMainClient.isGerman()?"kein Herunterladen, nur Dateigröße ausgeben":"no download, just report file size"));
			addTextToConsole("proxy[ URL]\t\t: ".concat(JFCMainClient.isGerman()?"Proxy Variable anzeigen oder ändern":"get or set proxy variable").concat(" - [http[s]://]proxyhost:proxyport"));
			addTextToConsole("quit|exit\t\t: ".concat(JFCMainClient.isGerman()?"Anwendung beenden":"shutdown application"));
			addTextToConsole("sds[ on| off]\t\t: ".concat(JFCMainClient.isGerman()?"Speicherplatz sparen, geringere Aufl. zu erst herunterladen (z.B. 720p vor 1080p)":"save disk space, lower res download first (e.g. 720p before 1080p)"));
			addTextToConsole("audioonly[ on| off]\t: ".concat(JFCMainClient.isGerman()?"NUR Audio Version (falls vorhanden) herunterladen!":"ONLY save audio file, if available!"));
			addTextToConsole("version|v|\t\t: ".concat(JFCMainClient.isGerman()?"Version anzeigen":"show version"));
		} else if (scmd.matches("^(v(ersion)?)")) {
			addTextToConsole(VERSION);
		} else if (scmd.matches("^(debug)( on| off| true| false)?")) {
			if (scmd.matches(".*(on|true)$"))
				JFCMainClient.FRAME.isDebug = true;
			else if (scmd.matches(".*(off|false)$"))
				JFCMainClient.FRAME.isDebug = false;

			addTextToConsole("debug: ".concat(Boolean.toString(JFCMainClient.FRAME.isDebug)));
		} else if (scmd.matches("^(ndl)( on| off| true| false)?")) {
			if (scmd.matches(".*(on|true)$"))
				setNoDownload(true);
			else if (scmd.matches(".*(off|false)$"))
				setNoDownload(false);

			addTextToConsole("ndl: ".concat(Boolean.toString(JFCMainClient.getNoDownload())));
		} else if (scmd.matches("^(sds)( on| off| true| false)?")) {
			if (scmd.matches(".*(on|true)$"))
				JFCMainClient.SAVE_DISKSPACE = true;
			else if (scmd.matches(".*(off|false)$"))
				JFCMainClient.SAVE_DISKSPACE = false;

			addTextToConsole("sds: ".concat(Boolean.toString(JFCMainClient.SAVE_DISKSPACE)));
		} else if (scmd.matches("^(audio(only)?)( on| off| true| false)?")) {
			if (scmd.matches(".*(on|true)$"))
				setAudioOnly(true);
			else if (scmd.matches(".*(off|false)$"))
				setAudioOnly(false);

			addTextToConsole("audioonly: ".concat(Boolean.toString(JFCMainClient.getAudioOnly())));
		} else if (scmd.matches("^(quit|exit)")) {
			JFCMainClient.shutdownApp();
		} else if (scmd.matches("^(proxy)( .*)?")) {
			if (!scmd.matches("^(proxy)$")) {
				// we replace "" and '' with <nothing> otherwise it's interpreted as host
				// perhaps some users don't know how to input
				// "proxy[ URL]" with an empty URL ;-)
				String snewproxy = scmd.replaceAll("\"", "").replaceAll("'", "").replaceFirst("proxy ", "");
				debugOutput("snewproxy: ".concat(snewproxy));
				if (snewproxy.matches(JFCMainClient.PROXY_REGEX))
					JFCMainClient.PROXY = snewproxy;
				else
					addTextToConsole(isGerman() ? "Proxy-Zeichenkette entspricht nicht der Spezifikation für einen Rechner!": "proxy string does not match hostname specification!");
			}
			addTextToConsole("proxy: ".concat(JFCMainClient.PROXY));
		} else if (scmd.matches("^(config|c)$")) {
			addTextToConsole("config: ");
//			for ( Iterator<String> i = config.getKeys(); i.hasNext(); ) {
//				addTextToConsole(String.format("%s: %s", i, config.getProperty(i.toString())));
//			}
		} else if (scmd.matches("^(id)( .*)?")) {
			if (scmd.matches(".*(on|true)$"))
				JFCMainClient.setSaveIdInFilename(true);
			else if (scmd.matches(".*(off|false)$"))
				JFCMainClient.setSaveIdInFilename(false);

			addTextToConsole("id: ".concat(Boolean.toString(JFCMainClient.isSaveIdInFilename())));
		} else
			addTextToConsole(isGerman() ? "? (versuche hilfe|help|h|?)": "? (try help|h|?)");

	} // cli()
	

	static synchronized void setNoDownload(boolean noDownload ) {
		JFCMainClient.FRAME.noDownload = noDownload;
	} 
	
	static synchronized boolean getNoDownload() {
		// no download if we debug
		try {
			return(JFCMainClient.FRAME.noDownload);
		} catch (NullPointerException npe) {
			return(JFCMainClient.getDebug());
		}
	} 
	
	static synchronized void setAudioOnly(boolean audioOnly ) {
		JFCMainClient.FRAME.audioOnly = audioOnly;
	} 
	
	static synchronized boolean getAudioOnly() {
		try {
			return(JFCMainClient.FRAME.audioOnly);
		} catch (NullPointerException npe) {
			return(false);
		}
	} 
	
	static synchronized boolean isSaveIdInFilename() {
		return SAVE_ID_IN_FILENAME;
	} 

	private static synchronized void setSaveIdInFilename(boolean saveIdInFilename) {
		JFCMainClient.SAVE_ID_IN_FILENAME = saveIdInFilename;
	} 
	
	static synchronized boolean getDebug() {
		try {
			return(JFCMainClient.FRAME.isDebug);
		} catch (NullPointerException npe) {
			return JFCMainClient.VERSION.matches("V[0-9]+_[0-9]+d.*");
		}
	} 

	/**
	 * @param pane
	 */
	public void addComponentsToPane( final Container pane ) {
		this.panel = new JPanel();

		this.panel.setLayout( new GridBagLayout() );

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets( 5, 5, 5, 5 );
		gbc.anchor = GridBagConstraints.WEST;

		JFCMainClient.DLM = new DefaultListModel<YoutubeUrl>();
		this.urlList = new JList<YoutubeUrl>( JFCMainClient.DLM );
		// TODO maybe we add a button to remove added URLs from list?
//		this.userlist.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		this.urlList.setFocusable( false );
		this.textArea = new JTextArea( 2, 2 );
		this.textArea.setEditable( true );
		//this.textarea.setFocusable( false );
		this.textArea.setFocusable( true );

		JScrollPane leftscrollpane = new JScrollPane( this.urlList );
		JScrollPane rightscrollpane = new JScrollPane( this.textArea );
		this.middlePane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, leftscrollpane, rightscrollpane );
		this.middlePane.setOneTouchExpandable( true );
		this.middlePane.setDividerLocation( 150 );

		Dimension minimumSize = new Dimension( 25, 25 );
		leftscrollpane.setMinimumSize( minimumSize );
		rightscrollpane.setMinimumSize( minimumSize );
		
		this.directoryButton = new JButton("", createImageIcon("/resources/zsk/images/open.png",""));
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.directoryButton.addActionListener( this );
		this.panel.add( this.directoryButton, gbc );
		
		this.saveConfigCheckBox = new JCheckBox(isGerman()?"Konfig. speichern":"Save config");
		this.saveConfigCheckBox.setSelected(false);
		
		this.saveConfigCheckBox.addItemListener(this);
		this.panel.add(this.saveConfigCheckBox);
		
		this.saveConfigCheckBox.setEnabled(false);
		
		String sfilesep = System.getProperty("file.separator");

		// TODO check if initial download directory exists
		// assume that at least the users homedir exists
		String shomedir = System.getProperty("user.home").concat(sfilesep);
		if (System.getProperty("user.home").equals("/home/knoedel")) shomedir = "/home/knoedel/YouTube Downloads/ramdisk";
		debugOutput("user.home: ".concat(System.getProperty("user.home")).concat("  shomedir: ".concat(shomedir)));
		debugOutput("os.name: ".concat(System.getProperty("os.name")));
		debugOutput("os.arch: ".concat(System.getProperty("os.arch")));
		debugOutput("os.version: ".concat(System.getProperty("os.version")));
		debugOutput("Locale.getDefault: ".concat(Locale.getDefault().toString()));
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.directoryTextField = new JTextField( shomedir, 20+(JFCMainClient.getDebug()?48:0) );
		this.directoryTextField.setEnabled( false );
		this.directoryTextField.setFocusable( true );
		this.directoryTextField.addActionListener( this );
		this.panel.add( this.directoryTextField, gbc);
		
		JLabel dirhint = new JLabel( isGerman()?"Speichern im Ordner:":"Download to folder:");

		gbc.gridx = 0;
		gbc.gridy = 1;
		this.panel.add( dirhint, gbc);
		
		debugOutput(String.format("heigth x width: %d x %d",Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));
		
		this.middlePane.setPreferredSize( new Dimension( Toolkit.getDefaultToolkit().getScreenSize().width/3, Toolkit.getDefaultToolkit().getScreenSize().height/4+(JFCMainClient.getDebug()?200:0) ) );

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 2;
		gbc.weightx = 2;
		gbc.gridwidth = 2;
		this.panel.add( this.middlePane, gbc );

		// radio buttons for resolution to download
		JFCMainClient.FRAME.fourkButton = new JRadioButton("4K"); JFCMainClient.FRAME.fourkButton.setActionCommand("4k"); JFCMainClient.FRAME.fourkButton.addActionListener(this); JFCMainClient.FRAME.fourkButton.setToolTipText("2160p/4320p");
		JFCMainClient.FRAME.hdButton = new JRadioButton("HD"); JFCMainClient.FRAME.hdButton.setActionCommand("hd"); JFCMainClient.FRAME.hdButton.addActionListener(this); JFCMainClient.FRAME.hdButton.setToolTipText("1080p/720p");
		JFCMainClient.FRAME.stdButton = new JRadioButton("Std"); JFCMainClient.FRAME.stdButton.setActionCommand("std"); JFCMainClient.FRAME.stdButton.addActionListener(this); JFCMainClient.FRAME.stdButton.setToolTipText("480p/360p");
		JFCMainClient.FRAME.ldButton = new JRadioButton("LD"); JFCMainClient.FRAME.ldButton.setActionCommand("ld"); JFCMainClient.FRAME.ldButton.addActionListener(this); JFCMainClient.FRAME.ldButton.setToolTipText("< 360p");
		
		JFCMainClient.FRAME.fourkButton.setSelected(false);
		JFCMainClient.FRAME.stdButton.setSelected(true);
		JFCMainClient.FRAME.hdButton.setEnabled(true);
		JFCMainClient.FRAME.ldButton.setEnabled(true);
		
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(JFCMainClient.FRAME.fourkButton);
		bgroup.add(JFCMainClient.FRAME.hdButton);
		bgroup.add(JFCMainClient.FRAME.stdButton);
		bgroup.add(JFCMainClient.FRAME.ldButton);
		
		JPanel radiopanel = new JPanel(new GridLayout(1,0));
		radiopanel.add(JFCMainClient.FRAME.fourkButton);
		radiopanel.add(JFCMainClient.FRAME.hdButton);
		radiopanel.add(JFCMainClient.FRAME.stdButton);
		radiopanel.add(JFCMainClient.FRAME.ldButton);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 0;
		gbc.gridwidth = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		this.panel.add( radiopanel, gbc );

		// radio buttons for video format to download
		JFCMainClient.FRAME.mpgButton = new JRadioButton("MPEG"); JFCMainClient.FRAME.mpgButton.setActionCommand("mpg"); JFCMainClient.FRAME.mpgButton.addActionListener(this); JFCMainClient.FRAME.mpgButton.setToolTipText("Codec: H.264 MPEG-4");
		JFCMainClient.FRAME.webmButton = new JRadioButton("WEBM"); JFCMainClient.FRAME.webmButton.setActionCommand("webm"); JFCMainClient.FRAME.webmButton.addActionListener(this); JFCMainClient.FRAME.webmButton.setToolTipText("Codec: Google/On2's VP8 or Googles WebM");
		JFCMainClient.FRAME.flvButton = new JRadioButton("FLV"); JFCMainClient.FRAME.flvButton.setActionCommand("flv"); JFCMainClient.FRAME.flvButton.addActionListener(this); JFCMainClient.FRAME.flvButton.setToolTipText("Codec: Flash Video (FLV1)");

		bgroup = new ButtonGroup();
		bgroup.add(JFCMainClient.FRAME.mpgButton);
		bgroup.add(JFCMainClient.FRAME.webmButton);
		bgroup.add(JFCMainClient.FRAME.flvButton);

		JFCMainClient.FRAME.mpgButton.setSelected(true);
		JFCMainClient.FRAME.mpgButton.setEnabled(true);
		JFCMainClient.FRAME.webmButton.setEnabled(true);
		JFCMainClient.FRAME.flvButton.setEnabled(true);
		
		JFCMainClient.FRAME.save3dCheckBox = new JCheckBox("3D");
		JFCMainClient.FRAME.save3dCheckBox.setToolTipText("stereoscopic video");
		JFCMainClient.FRAME.save3dCheckBox.setSelected(false);
		JFCMainClient.FRAME.save3dCheckBox.setEnabled(true);
		JFCMainClient.FRAME.save3dCheckBox.addItemListener(this);
		
		radiopanel = new JPanel(new GridLayout(1,0));
		radiopanel.add(JFCMainClient.FRAME.save3dCheckBox);
		radiopanel.add(JFCMainClient.FRAME.mpgButton);
		radiopanel.add(JFCMainClient.FRAME.webmButton);
		radiopanel.add(JFCMainClient.FRAME.flvButton);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 0;
		gbc.gridwidth = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		this.panel.add( radiopanel, gbc );
		
		JLabel hint = new JLabel( isGerman()?"eingeben, reinkopieren, reinziehen von YT-Webadressen oder YT-Videobilder:":"Type, paste or drag'n drop a YouTube video address:");

		gbc.fill = 0;
		gbc.gridwidth = 0;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.WEST;
		this.panel.add( hint, gbc );
		
		this.textInputField = new JTextField( 20 );
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		this.textInputField.setEnabled( true );
		this.textInputField.setFocusable( true );
		this.textInputField.addActionListener( this );
		this.textInputField.getDocument().addDocumentListener(this);
		this.panel.add( this.textInputField, gbc );
		
		
		//...where the GUI is constructed:
	    //Create the popup menu.
	    this.popup = new JPopupMenu();
	    JMenuItem menuItem = new JMenuItem(isGerman()?"Zwischenablage in Textfeld einfügen":"Paste Clipboard into textfield");
	    menuItem.setActionCommand("paste");
	    menuItem.addActionListener(this);
	    this.popup.add(menuItem);
	    menuItem = new JMenuItem(isGerman()?"Textfeld Löschen!":"Clear textfield!");
	    menuItem.setActionCommand("clear");
	    menuItem.addActionListener(this);
	    this.popup.add(menuItem);
	    this.popup.addSeparator();
	    menuItem = new JMenuItem(isGerman()?"Meldungen Kopieren":"Copy Messages");
	    menuItem.setActionCommand("debug");
	    menuItem.addActionListener(this);
	    this.popup.add(menuItem);
	    
	    //Add listener to components that can bring up popup menus.
	    MouseListener popupListener = new PopupListener(this.popup);
	    this.textInputField.addMouseListener(popupListener);
	    this.textArea.addMouseListener(popupListener);
	    this.panel.addMouseListener(popupListener);
	    this.urlList.addMouseListener(popupListener);
		
		this.quitButton = new JButton( "" ,createImageIcon("/resources/zsk/images/exit.png",""));		
		gbc.gridx = 2;
		gbc.gridy = 5;
		gbc.gridwidth = 0;
		this.quitButton.addActionListener( this );
		this.quitButton.setActionCommand( "quit" );
		this.quitButton.setToolTipText( "Exit." );

		this.panel.add( this.quitButton, gbc );

		pane.add( this.panel );
		addWindowListener( this );
		
		JFCMainClient.FRAME.setDropTarget(new DropTarget(this, this));
		JFCMainClient.FRAME.textArea.setTransferHandler(null); // otherwise the dropped text would be inserted

	} 

	public JFCMainClient( String name ) {
		super( name );
	}

	public JFCMainClient() {
		
	}
	
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	static void initializeUI() {
		String sv = "YTD2 ".concat(VERSION).concat(" ").concat("http://sourceforge.net/projects/ytd2/"); // ytd2.sf.net is shorter
		sv = isGerman()?sv.replaceFirst("by", "von"):sv;

		if (! IS_CLI) {
			setDefaultLookAndFeelDecorated(false);
			FRAME = new JFCMainClient( sv );
			// why did I first use this? it prevents appl shutdown if an AWT thread runs (for popup menu - rmb)
			//frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
			FRAME.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
			FRAME.addComponentsToPane( FRAME.getContentPane() );
			FRAME.pack();
			FRAME.setVisible( true );
		} else {
			setSaveIdInFilename(true);
		}
		
		JFCMainClient.DOWNLOAD_STATE = isGerman()?"heruntergeladen ":JFCMainClient.DOWNLOAD_STATE;
		
		sv = "version: ".concat( VERSION ).concat(JFCMainClient.getDebug()?" DEBUG ":"");
		sv = isGerman()?sv.replaceFirst("by", "von"):sv;
		output(sv); debugOutput(sv);
		output(""); // \n

		JFCMainClient.PROXY = System.getenv("http_proxy");
		if (JFCMainClient.PROXY==null) PROXY="";
		sv = "http_proxy: ".concat(PROXY);
		output(sv); debugOutput(sv);
		
		sv = isGerman()?"Speicherverzeichnis: ":"initial download folder: ";
		if (IS_CLI) {
			if (CONFIG.getProperty("savefolder")==null) {
				// set initial download folder to users homedir
				String shomedir = System.getProperty("user.dir"); // for CLI-only run
				if (!shomedir.endsWith(System.getProperty("file.separator")))
					shomedir += System.getProperty("file.separator");
				CONFIG.setProperty("savefolder", shomedir);
			}
			sv = sv.concat((String)CONFIG.getProperty("savefolder"));
		} else {
			sv = sv.concat(JFCMainClient.FRAME.directoryTextField.getText());
			CONFIG.setProperty("savefolder", JFCMainClient.FRAME.directoryTextField.getText());
		}
		output(sv); debugOutput(sv);
		
		// lets respect the upload limit of google (youtube)
		// downloading is faster than viewing anyway so don't start more than six threads and don't play around with the URL-strings please!!!
		THREAD_1 = new YTDownloadThread();
		THREAD_1.start();
		if (!getDebug()) {
			THREAD_2 = new YTDownloadThread();
			THREAD_2.start();
			THREAD_3 = new YTDownloadThread();
			THREAD_3.start();
			THREAD_4 = new YTDownloadThread();
			THREAD_4.start();
			THREAD_5 = new YTDownloadThread();
			THREAD_5.start();
			THREAD_6 = new YTDownloadThread();
			THREAD_6.start();
		}
		
		output(""); // \n
		output(isGerman()?"besuche sf.net/projects/ytd2/forums für irgendwelche Tipps, Vorschläge, Neuerungen, Fragen!":"Visit sf.net/projects/ytd2/forums for tips, questions, updates and comments!");
		output(""); // \n
		if (!IS_CLI)
			JFCMainClient.FRAME.cli("help");
		
	} 
	
	
	public void windowActivated( WindowEvent e ) {
			setFocusToTextField();
	} 

	public void windowClosed( WindowEvent e ) {
	}

	/**
	 * quit==exit
	 * 
	 */
	public void windowClosing( WindowEvent e ) {
		JFCMainClient.shutdownApp();
	} 

	public void windowDeactivated( WindowEvent e ) {
	}

	public void windowDeiconified( WindowEvent e ) {
	}

	public void windowIconified( WindowEvent e ) {
	}

	public void windowOpened( WindowEvent e ) {
	}
	
	/*
	 * "Movement!...signals clean. Range 20 meters." (Hudson)
	 */
	public void processComponentEvent(ComponentEvent e) {
		switch (e.getID()) {
		case ComponentEvent.COMPONENT_MOVED:
			break;
		case ComponentEvent.COMPONENT_RESIZED:
			JFCMainClient.FRAME.middlePane.setDividerLocation(JFCMainClient.FRAME.middlePane.getWidth() / 3);
			break;
		case ComponentEvent.COMPONENT_HIDDEN:
			break;
		case ComponentEvent.COMPONENT_SHOWN:
			break;
		}
	} 
	
	/**
	 * main entry point
	 * 
	 * @param args
	 */
	public static void main( final String[] args ) {
		
		try { // load from file
			CONFIG = new XMLConfiguration(CONFIG_FILE);
			debugOutput("configuration loaded from file: ".concat(CONFIG.getBasePath()));
			// create empty config entries?
		} catch (ConfigurationException e1) {
			debugOutput("configuration could not be load from file: ".concat(CONFIG_FILE).concat(" creating new one."));
			CONFIG = new XMLConfiguration();
		}
		
		JFCMainClient.ARGS = args;
		if (args.length>0) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() { 
			    	debugOutput("shutdown hook handler. (cli only)");
			    	if (JFCMainClient.FRAME == null) {
			    		JFCMainClient.shutdownApp();
			    	}
			    	debugOutput("shutdown hook handler. end run()");
			    }
			});
			
			IS_CLI = true;
			runCLI();			
		} else {
			try {
				UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
			} catch (UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (java.lang.InternalError ie) {
				System.err.println(ie.getMessage());
				printHelp();
				System.exit(1);
			}

			try {
				javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							initializeUI();
						}  catch (java.awt.HeadlessException he) {
							System.err.println(he.getMessage());
							JFCMainClient.printHelp();
							System.exit(1);
						}
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	} 
	
	static int parseargs(String[] args) throws ArrayIndexOutOfBoundsException {
		int irc=0;
		
		// TODO 3D as parameter for CLI download
		// TODO Ultra HD as parameter for CLI download
		
		JFCMainClient.CLI_DOWNLOAD_QUALITIES = JFCMainClient.CLI_DownloadQuality.SD;
		JFCMainClient.CLI_DOWNLOAD_FORMATS = JFCMainClient.CLI_DownloadFormat.MPG;
		
		for (int i=0;i<args.length;i++) {
			if (args[irc].toLowerCase().matches("(--)?h(elp|ilfe)?")) {
				printHelp();
				return args.length; // dont load any videos if --help is submitted
			}
			
			if (args[irc].toLowerCase().equals("-l")) {
				JFCMainClient.CLI_DOWNLOAD_QUALITIES = JFCMainClient.CLI_DownloadQuality.LD;
				debugOutput("parameter -l");
				irc++;
			} 
			if (args[irc].toLowerCase().equals("-h")) {
				JFCMainClient.CLI_DOWNLOAD_QUALITIES = JFCMainClient.CLI_DownloadQuality.HD;
				debugOutput("parameter -h");
				irc++;
			}
			
			if (args[irc].toLowerCase().equals("-f")) {
				JFCMainClient.CLI_DOWNLOAD_FORMATS = JFCMainClient.CLI_DownloadFormat.FLV;
				debugOutput("parameter -f");
				irc++;
			}
			if (args[irc].toLowerCase().equals("-w")) {
				JFCMainClient.CLI_DOWNLOAD_FORMATS = JFCMainClient.CLI_DownloadFormat.WEBM;
				debugOutput("parameter -w");
				irc++;
			}

		} 
		
		return irc;
	} 
	
	// TODO long parameters must be tested too
	//
	static void printHelp() {
//		final HelpFormatter formatter = new HelpFormatter();
		System.out.println(VERSION);
		System.out.println("Usage: \n");
		System.out.println("java -jar runytd2.jar <Q> <F> ['youtube-url' ['youtube-url']]\n");
		System.out.println("<Q> = [-l|-h]   for video quality (low, high) - defaults to standard if <Q> is omitted");
		System.out.println("<F> = [-m]      for video format (mpeg) - defaults to mpeg if <F> is omitted");
		System.out.println("<F> = [-w]      for video format (webm) - defaults to mpeg if <F> is omitted");
		System.out.println("<F> = [-f]      for video format (flv)  - defaults to mpeg if <F> is omitted");
		// TODO we implement that later - some testing is still needed
		// formatter.printHelp("java -jar runytd2.jar", sCLIOptions, true);
		
		System.out.println("");
		System.out.println(JFCMainClient.isGerman()?"Nicht vergessen die URL mit ' oder \" einzuschließen, wenn die Adresse & oder <LEERZEICHEN> enthält!":"Don't forget to put ' or \" around an URL if it contains & or <space> !");
		System.out.println("");
		System.out.println(isGerman()?"besuche sf.net/projects/ytd2/forums für irgendwelche Tipps, Vorschläge, Neuerungen, Fragen!":"Visit sf.net/projects/ytd2/forums for tips, questions, updates and comments!");
		System.out.println("");
	} 
	
	public static void runCLI() {
		JFCMainClient.DLM = new DefaultListModel<YoutubeUrl>();
		
		Boolean bStartThreads=false;
		int istarturls = 0;
		try {
			istarturls = parseargs(JFCMainClient.ARGS);
		} catch (ArrayIndexOutOfBoundsException aioob) {
			output(JFCMainClient.isGerman()?"nicht genügend Parameter":"not enough parameters");
			debugOutput(JFCMainClient.isGerman()?"nicht genügend Parameter":"not enough parameters");
			istarturls = JFCMainClient.ARGS.length;
		}

		if (istarturls < JFCMainClient.ARGS.length) {
			for (int i = istarturls; i < JFCMainClient.ARGS.length; i++) {
				if (JFCMainClient.ARGS[i].matches(YOUTUBE_REGEX.concat(".*"))) {
					JFCMainClient.addYoutubeUrlToList(JFCMainClient.ARGS[i]);
					debugOutput("adding URL: ".concat(JFCMainClient.ARGS[i]));
					bStartThreads = true;
				} else {
					debugOutput("wrong URL: ".concat(JFCMainClient.ARGS[i]));
					output(String.format((JFCMainClient.isGerman()?"URL: %d sieht nicht aus wie eine YouTube-URL - %s":"URL: %d does not look like a youtube-URL - %s"),i, JFCMainClient.ARGS[i]));
				}
			}
		}
		if (bStartThreads)
			JFCMainClient.initializeUI();
		else {
			if (!JFCMainClient.ARGS[0].equals("--help")) {
				JFCMainClient.ARGS[0]="--help";
				parseargs(JFCMainClient.ARGS);
			}
		}
	} 
	
	static void debugOutput (String debugMessage) {
		if (!JFCMainClient.getDebug())
			return;

		JFCMainClient.addTextToConsole("#DEBUG ".concat(debugMessage));
	} 
	
	static void output (String message) {
		try {
			if (JFCMainClient.getDebug())
				return;
		} catch (NullPointerException npe) {}

		JFCMainClient.addTextToConsole("#info - ".concat(message));
	} 


	public void changedUpdate(DocumentEvent e) {
		checkInputFieldForYoutubeUrls();
	}


	public void insertUpdate(DocumentEvent e) {
		checkInputFieldForYoutubeUrls();
	} 

	public void removeUpdate(DocumentEvent e) {
		checkInputFieldForYoutubeUrls();
	}
	
//	private String getHost(String sURL) {
//		String shost = sURL.replaceFirst(JFCMainClient.szHOSTREGEX, "");
//		shost = sURL.substring(0, sURL.length()-shost.length());
//		shost = shost.toLowerCase().replaceFirst("http://", "").replaceAll("/", "");
//		return(shost);
//	} // gethost
	
	/**
	 * check if a youtube-URL was pasted or typed in
	 * if yes cut it out and send it to the URLList to get processed by one of the threads
	 * 
	 * the user can paste a long string containing many youtube-URLs .. but here is work to do because we have to erase the string(s) that remain(s)
	 */
	void checkInputFieldForYoutubeUrls() {
		String input = FRAME.textInputField.getText(); // don't call .toLowerCase() !

		input = input.replaceAll("/watch?.*&v=", "/watch?v=");
		input = input.replaceAll(" ", "");
		input = input.replaceAll(PLAYLIST_REGEX, "/watch?v=");

		String url = input.replaceFirst(YOUTUBE_REGEX, "");
		
		// if nothing could be replaced we have to yt-URL found
		if (input.equals(url)) return;

		debugOutput("sinput: ".concat(input).concat(" surl: ".concat(url)));
		
		// starting at index 0 because szYTREGEX should start with ^ // if szYTREGEX does not start with ^ then you have to find the index where the match is before you can cut out the URL 
		url = input.substring(0, input.length()-url.length());
		addYoutubeUrlToList(url);
		input = input.substring(url.length());
		debugOutput(String.format("sinput: %s surl: %s",input,url));
		
		// if remaining text is shorter than shortest possible yt-url we delete it
		if (input.length()<"youtube.com/watch?v=0123456789a".length()) input = "";
		
		//frame.textinputfield.setText(sinput); // generates a java.lang.IllegalStateException: Attempt to mutate in notification
		
		final String fs = input;

		// let a thread update the textfield in the UI
		Thread worker = new Thread() {
            public void run() {
            	synchronized (JFCMainClient.FRAME.textInputField) {
            		JFCMainClient.FRAME.textInputField.setText(fs);
				}
            }
        };
        SwingUtilities.invokeLater (worker);
	} 
	
	ImageIcon createImageIcon(String path, String description) {
	    URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	} 

	public void stateChanged(ChangeEvent e) {
	}


	public void dragEnter(DropTargetDragEvent dtde) {
	}


	public void dragOver(DropTargetDragEvent dtde) {
	}


	public void dropActionChanged(DropTargetDragEvent dtde) {
	}


	public void dragExit(DropTargetEvent dte) {
	}


	/**
	 * processing event of dropping a HTTP URL, YT-Video Image or plain text (URL) onto the frame
	 * 
	 * seems not to work with M$-IE (8,9) - what a pity! (at least the video image drop does not work, url seems to work though)
	 */
	public void drop(DropTargetDropEvent dropTargetDropEvent) {
			Transferable tr = dropTargetDropEvent.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			DataFlavor fl = null;
			String str = "";
			
			debugOutput("DataFlavors found: ".concat(Integer.toString( flavors.length )));
		for (int i = 0; i < flavors.length; i++) {
			fl = flavors[i];
			if (fl.isFlavorTextType() /* || fl.isMimeTypeEqual("text/html") || fl.isMimeTypeEqual("application/x-java-url") || fl.isMimeTypeEqual("text/uri-list")*/) {
				try {
					dropTargetDropEvent.acceptDrop(dropTargetDropEvent.getDropAction());
				} catch (Throwable t) {
				}
				try {
					if (tr.getTransferData(fl) instanceof InputStreamReader) {
						debugOutput("Text-InputStream");
						BufferedReader textreader = new BufferedReader(
								(Reader) tr.getTransferData(fl));
						String sline = "";
						try {
							while (sline != null) {
								sline = textreader.readLine();
								if (sline != null)
									str += sline;
							}
						} catch (Exception e) {
						} finally {
							textreader.close();
						}
						str = str.replaceAll("<[^>]*>", ""); // remove HTML tags, especially a hrefs - ignore HTML characters like &szlig; (which are no tags)
					} else if (tr.getTransferData(fl) instanceof InputStream) {
						debugOutput("Byte-InputStream");
						InputStream input = new BufferedInputStream(
								(InputStream) tr.getTransferData(fl));
						int idata = input.read();
						String sresult = "";
						while (idata != -1) {
							if (idata != 0)
								sresult += new Character((char) idata)
										.toString();
							idata = input.read();
						} // while
						debugOutput("sresult: ".concat(sresult));
					} else {
						str = tr.getTransferData(fl).toString();
					}
				} catch (IOException ioe) {
				} catch (UnsupportedFlavorException ufe) {
				}

				debugOutput("drop event text: ".concat(str).concat(" (").concat(fl.getMimeType()).concat(") "));
				// insert text into textfield - almost the same as user drops text/url into this field
				// except special characaters -> from http://de.wikipedia.org/wiki/GNU-Projekt („GNU is not Unix“)(&bdquo;GNU is not Unix&ldquo;)
				// two drops from same source .. one time in textfield and elsewhere - maybe we change that later?!
				if (str.matches(YOUTUBE_REGEX.concat("(.*)"))) {
					synchronized (JFCMainClient.FRAME.textInputField) {
						JFCMainClient.FRAME.textInputField.setText(str.concat(JFCMainClient.FRAME.textInputField.getText()));
					}
					debugOutput("breaking for-loop with str: ".concat(str));
					break;
				}
			} else {
				String sv = "drop event unknown type: ".concat(fl.getHumanPresentableName());
				//output(sv);
				debugOutput(sv);
			}
		} 

		dropTargetDropEvent.dropComplete(true);
	} 

	public void itemStateChanged(ItemEvent itemEvent) {
		if ( itemEvent.getSource()==this.saveConfigCheckBox )
			if ( itemEvent.getStateChange()==java.awt.event.ItemEvent.SELECTED)
				debugOutput("saving config on exit.");
			else
				debugOutput("don't saving config on exit.");
		
		if ( itemEvent.getSource()==this.save3dCheckBox )
			if ( itemEvent.getStateChange()==java.awt.event.ItemEvent.SELECTED) {
				debugOutput("trying: 3D");
				output("trying: 3D");
			} else {
				debugOutput("trying: normal 2D");
				output("trying: normal 2D");
			}

	} 
	
} 