import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.Player;

public class MainGame implements ActionListener {

	// Main variables
	static long money; // current money
	static long meters; // current meters
	static long gotmoney;
	static long currentMeters;
	static long combo;
	static int meterDisplay = 0; // meter display
	static long speed;
	static long publictotal;
	static boolean started = false;
	static boolean connected = false;
	
	static String error;
	static int ci;
	static boolean showMoney = true;
	static boolean alwaysTop = true;
	static boolean pMusic;
	static int doSceneTest = 0;

	// Public stuff
	public static TrayIcon trayIcon;
	public static JLabel stats;
	public static JFrame f;
	public static JLabel meterDisplayTick;
	public static JLabel errorText;
	public static Timer meterTick;
	public static Timer uniSend;
	public static JLabel publicMetersLabel;
	public static Boolean pausedFrog = true;
	public static JLabel title;
	public static JLabel unifrog_base_scene_label;
	
	// dynamic
	public static JMenu mItems;
	public static JMenu mSkins;
	public static JMenu mAccessories;
	
	public static String webpage = "http://unifrog.braxnet.org";
	
	AudioClip sound_bike;
	AudioClip sound_techno;
	
	unisound MusicPlayer = new unisound();

	JLabel unifrog_base_img_label = null;
	JLabel unifrog_base_scene = null;
	
	public static Color textColor = null;
	
	NumberFormat formatter = NumberFormat.getInstance();
	
	void ResetItemMenu(){
		
		// reset
		mItems.removeAll();
		
		// Skins
		mSkins.removeAll();
		for (int i = 0; i < unifrog.ItemList.length; i++) {
			if (unifrog.ItemList[i] != null && unifrog.ItemList[i][2] != null) {
				
				if( unifrog.ItemList[i][2].equals("skin") ){
				
					JMenuItem tmp = new JMenuItem(unifrog.ItemList[i][1]);
					tmp.setToolTipText(unifrog.ItemList[i][0]);
					final int bi = i;
					tmp.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae2) {
							setFrog(bi);
						}
					});
	
					mSkins.add(tmp);
					
				}
			
			}
		}
		mItems.add(mSkins);
		
		// Other items
		
		
		// Music
		mAccessories.removeAll();
		for (int i = 0; i < unifrog.ItemList.length; i++) {
			if (unifrog.ItemList[i] != null && unifrog.ItemList[i][2] != null) {
				
				if( unifrog.ItemList[i][2].equals("radio") ){
				
					JMenuItem tmp = new JMenuItem(unifrog.ItemList[i][1]);
					tmp.setToolTipText(unifrog.ItemList[i][0]);
					final String song = unifrog.ItemList[i][0];
					tmp.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae2) {
							MusicPlayer.stop();
							MusicPlayer.play( song , true );
						}
					});
	
					mAccessories.add(tmp);
					
				}
			
			}
		}
		
		/*
			JCheckBoxMenuItem mMusic = new JCheckBoxMenuItem("Radio on");
			mMusic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae2) {
					pMusic = !pMusic;
					System.out.println("Music: " + showMoney);
					if (pMusic == true) {
						//sound_techno.loop();
						MusicPlayer.play( "lights.mp3", true );
					} else {
						MusicPlayer.stop();
					}
				}
			});
			mAccessories.add(mMusic);
		}
		*/
		mItems.add( mAccessories );
	
	}

	void begin() {
		
		// Send every 10 seconds thing
		uniSend = new Timer(10000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendFrog();
			}
		});
		uniSend.setRepeats(false);
		uniSend.setInitialDelay(10000);
		
		// Do this every second
		meterTick = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(connected){
					meterDisplay++;
					meterDisplayTick.setText(formatter.format(meterDisplay) + "m"); // current meters text
					f.setTitle(unifrog.appTitle + " - " + formatter.format(meterDisplay) + "m");
				}else{
					meterDisplayTick.setText(formatter.format(meterDisplay) + "m, not connected"); // current meters text
					f.setTitle(unifrog.appTitle + " - " + formatter.format(meterDisplay) + "m, not connected");
				}
				
			}
		});
		

		sound_techno = Applet.newAudioClip(unifrog.class.getResource("unifrog.wav"));
		sound_bike = Applet.newAudioClip(unifrog.class.getResource("bike.wav"));
		
		System.out.println("MainGame");
		f = new JFrame(unifrog.appTitle + " - v" + unifrog.version);
		f.setLayout(null);
		
		//f.setUndecorated(true);

		if (SystemTray.isSupported()) {
			
			// Tray icon
			SystemTray tray = SystemTray.getSystemTray();
			Image image = new ImageIcon(unifrog.class.getResource("icon.gif")).getImage();
			trayIcon = new TrayIcon(image, unifrog.appTitle, null);
			
			// popup menu
			PopupMenu tray_menu = new PopupMenu();
			MenuItem tray_quit = new MenuItem("Quit");
			tray_quit.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
				   System.exit(0);
			   }
			});
			tray_menu.add(tray_quit);
			trayIcon.setPopupMenu(tray_menu);
			
			trayIcon.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					f.requestFocus();
					f.toFront();
					f.repaint();
				}
			});
			
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("Tray is not supported!");
		}
		
		


		// Window settings
		f.setIconImage(new ImageIcon(unifrog.class.getResource("icon.png")).getImage());
		f.setResizable(false);
		f.setSize(300, 400);
		

		// Title
		title = new JLabel("Logged in as " + unifrog.username);
		title.setBounds(0, 13, 300, 20);
		title.setFont(new Font("Arial", Font.PLAIN, 14));
		title.setHorizontalAlignment(JLabel.CENTER);
		f.add(title);
		

		// Menu
		JMenuBar menuBar = new JMenuBar();

		// File menu
		JMenu mFile = new JMenu("File");
		JMenuItem quitButton = new JMenuItem("Quit");
		JMenuItem retryButton = new JMenuItem("Retry connection");
		mFile.add(retryButton);
		mFile.add(quitButton);
		menuBar.add(mFile);
		
		
		// Meters display
		meterDisplayTick = new JLabel("");
		meterDisplayTick.setBounds(0, 270, 300, 20);
		meterDisplayTick.setHorizontalAlignment(JLabel.CENTER);
		meterDisplayTick.setFont(new Font("Sans-Serif", Font.PLAIN, 18));
		f.add(meterDisplayTick);
		
		
		int bottomText = 300;

		// Error display and info
		errorText = new JLabel("Requesting update");
		errorText.setBounds(0, bottomText, 300, 20);
		errorText.setHorizontalAlignment(JLabel.CENTER);
		errorText.setFont(new Font("MS Sans Serif", Font.PLAIN, 10));
		f.add(errorText);
		
		
		// Stats
		stats = new JLabel("Waiting for stats");
		stats.setBounds(0, bottomText+14, 300, 20);
		stats.setHorizontalAlignment(JLabel.CENTER);
		stats.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
		f.add(stats);
		
		
		// Total public meters
		publicMetersLabel = new JLabel("");
		publicMetersLabel.setBounds(0, bottomText+30, 300, 20);
		publicMetersLabel.setHorizontalAlignment(JLabel.CENTER);
		publicMetersLabel.setFont(new Font("Sans-Serif", Font.BOLD, 9));
		f.add(publicMetersLabel);
		

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae2) {
				f.dispose();
				System.exit(0);
			}
		});

		retryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae2) {
				sendFrog();
				uniSend.stop();
				uniSend.start();
			}
		});

		// Main frog image - Frog!!!
		ImageIcon unifrog_base_img_icon = new ImageIcon(unifrog.class.getResource("unifrog_static.gif"));
		unifrog_base_img_label = new JLabel(unifrog_base_img_icon);
		unifrog_base_img_label.setBounds(70, 50, 120, 190);
		f.add(unifrog_base_img_label);
		
		//  background		
		ImageIcon unifrog_base_scene_img = new ImageIcon(unifrog.class.getResource("scene-day.png"));
		unifrog_base_scene_label = new JLabel(unifrog_base_scene_img);
		unifrog_base_scene_label.setBounds(0, 0, 300, 400);
		f.add(unifrog_base_scene_label);
		
		

		// Options
		JMenu mOptions = new JMenu("Options");
		JCheckBoxMenuItem mMoneyPopups = new JCheckBoxMenuItem("Show money popups", true);
		mMoneyPopups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae2) {
				showMoney = !showMoney;
			}
		});
		
		mOptions.add(mMoneyPopups);
		menuBar.add(mOptions);
		
		mItems = new JMenu("Items");
		mSkins = new JMenu("Skins");
		mAccessories = new JMenu("Accessories");
		ResetItemMenu();
		
		menuBar.add( mItems );
		
		
		// About menu
		JMenu aboutMenu = new JMenu("About");
		JMenuItem aboutMenuHomepage = new JMenuItem("Homepage");
		aboutMenuHomepage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae2){
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(unifrog.webpage));
				} catch (IOException e) {
					System.out.println("Error opening web page");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		aboutMenu.add(aboutMenuHomepage);
		JMenuItem aboutMenuVersion = new JMenuItem("Version " + unifrog.version);
		aboutMenuVersion.setEnabled(false);
		aboutMenu.add(aboutMenuVersion);
		menuBar.add(aboutMenu);
		
		
		f.setJMenuBar(menuBar);

		// Center window
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = f.getSize().width;
		int h = f.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		f.setLocation(x, y);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		// Set main app
		//uniSend.setInitialDelay(0);
		//uniSend.start();
		
		if(!started) uniSend.start();
		
		//sendFrog();
		
		meterTick.setInitialDelay(0);
		
		trayIcon.displayMessage(unifrog.appTitle, "Hello!", TrayIcon.MessageType.INFO);
		traySpeak();
		
		doScene();

	}

	void sendFrog() {
		//System.out.println("UN:" + unifrog.username + " PW: " + unifrog.password);
		
		//CRC32 crc = new CRC32();
		//crc.update( JSONValue.toJSONString( unifrog.ItemList ).getBytes() );
		//long sItems = crc.getValue();
		
		//System.out.println( JSONValue.toJSONString( unifrog.ItemList ) );
		
		String ret = uninet.sendGetRequest(webpage + "/handle.php","username=" + unifrog.username + "&password="	+ unifrog.password + "&version=" + unifrog.version + "&i=" + unifrog.ItemHash );
		
		System.out.println(ret);
		
		if (ret.equals("") ) {
			trayIcon.displayMessage(unifrog.appTitle, "Error: Could not contact server!", TrayIcon.MessageType.ERROR);
			connected = false;
			uniSend.stop();
			return;
		}

		if (ret.substring(0, 1).equals("<")) {
			MainGame.trayIcon.displayMessage(unifrog.appTitle,"Website error, urgent!!! I'm trying to fix it, but you NEED to relog immediately!",TrayIcon.MessageType.ERROR);
			connected = false;
			uniSend.stop();
			return;
		}

		Object user_obj = null;
		user_obj = JSONValue.parse(ret.toString());
		JSONObject obj = (JSONObject) user_obj;
		
		if (!obj.get("error").equals("0")) {
			
			if (obj.get("error").equals("Outdated client")) {
				JOptionPane.showMessageDialog(MainGame.f, "Your client is outdated, you should update it.","Warning!", JOptionPane.WARNING_MESSAGE);
			}else{
			
				if(obj.get("error").equals("1")) error = "Your client is updating too fast!";	
				
				stats.setText("");
				trayIcon.displayMessage(unifrog.appTitle, "Error: " + error, TrayIcon.MessageType.ERROR);
				traySpeak();
				
				if(!obj.get("error").equals("1")){
					System.out.println("Paused!");
					ImageIcon tmp = new ImageIcon(unifrog.class.getResource("unifrog_static.gif"));
					unifrog_base_img_label.setIcon(tmp);
					pausedFrog = true;
					sound_bike.stop();
					
				}
				
				connected = false;
				
				uniSend.start();
				
				return;
				
			}
		} else {
			error = "No problems encountered";
		}
		
		// Get data from JSON
		if (obj.get("money") != null)
			money = (Long) obj.get("money");
		if (obj.get("meters") != null)
			meters = (Long) obj.get("meters");
		if (obj.get("gotmoney") != null)
			gotmoney = (Long) obj.get("gotmoney");
		if (obj.get("combo") != null)
			combo = (Long) obj.get("combo");
		if(obj.get("speed") != null)
			speed = (Long) obj.get("speed");
		if(obj.get("publictotal") != null)
			publictotal = (Long) obj.get("publictotal");
		
		if (gotmoney > 0 && showMoney) {
			trayIcon.displayMessage(unifrog.appTitle, "You found $" + formatter.format(gotmoney) + "!", TrayIcon.MessageType.INFO);
			traySpeak();
		}
		
		// item update
		if( obj.get("hash") != null ){
			
			unifrog.ItemHash = (String) obj.get("hash");
			unifrog.ItemList = new String[49][3];
			
			JSONArray iobj = (JSONArray) obj.get("items");
			int len = iobj.size();
	
			for (int i = 0; i < len; i++) {
				JSONObject tmpobj = (JSONObject) iobj.get(i);
				//if (tmpobj.get("type").equals("skin")) {
				String[] tmp = new String[] { (String) tmpobj.get("file"), (String) tmpobj.get("name"), (String) tmpobj.get("type") };
				unifrog.ItemList[i] = tmp;
				//}
				//long id = (Long) tmpobj.get("id");
				//if (id == 60) unifrog.radioEnabled = true;
			}
			
			ResetItemMenu();
			
		}
		
		meterDisplay = (int) combo;
		
		if(!meterTick.isRunning()) meterTick.start();
		int finalMeterTick = 1000;
		finalMeterTick = (int) (1000 * 1000 / speed);
		meterTick.setDelay(finalMeterTick);
		
		errorText.setText(error);
		stats.setText(formatter.format(meters) + "M - $" + formatter.format(money));
		
		publicMetersLabel.setText(formatter.format(publictotal) + " METERS IN TOTAL");
		
		if(pausedFrog){
			ImageIcon tmp = new ImageIcon(unifrog.class.getResource("unifrog.gif"));
			unifrog_base_img_label.setIcon(tmp);
			pausedFrog = false;
			sound_bike.loop();
		}
		
		doSceneTest++;
		if(doSceneTest > 60){
			doScene();
			doSceneTest = 0;
		}
		
		connected = true;
		
		uniSend.start();

	}
	
	void traySpeak(){
		Image img = new ImageIcon(unifrog.class.getResource("icon-speak.gif")).getImage();
		trayIcon.setImage(img);
		Timer tempTray = new Timer(5000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Image img = new ImageIcon(unifrog.class.getResource("icon.gif")).getImage();
				trayIcon.setImage(img);
			}
		});
		tempTray.setRepeats(false);
		tempTray.start();
		
	}
	
	void doScene(){
		String tod = "";
		Calendar todd = Calendar.getInstance();
		if(todd.get(Calendar.HOUR_OF_DAY) > 20 || todd.get(Calendar.HOUR_OF_DAY) < 6){
			tod = "night";
			textColor = new Color(255,255,255);
		}else{
			tod = "day";
			textColor = new Color(0,0,0);
		}
		
		ImageIcon tmp = new ImageIcon(unifrog.class.getResource("scene-"+tod+".png"));
		unifrog_base_scene_label.setIcon(tmp);
		
		title.setForeground(textColor);
		meterDisplayTick.setForeground(textColor);
		errorText.setForeground(textColor);
		stats.setForeground(textColor);
		publicMetersLabel.setForeground(textColor);
		
		System.out.println("Update scene");
		
	}

	void setFrog(int id) {
		
		if(unifrog.ItemList == null){ System.out.println( "Invalid frog (items): " + id); return; }
		if(unifrog.ItemList[id] == null){ System.out.println( "Invalid frog (item): " + id); return; }
		if(unifrog.ItemList[id][0] == null){ System.out.println( "Invalid frog (path): " + id); return; }
		
		String sk = unifrog.ItemList[id][0];
		
		System.out.println( "Set new frog: " + sk);
		
		URL img = unifrog.class.getResource( sk );
		ImageIcon tmp = new ImageIcon( img );
		unifrog_base_img_label.setIcon( tmp );
		
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println(e);
	}

}
