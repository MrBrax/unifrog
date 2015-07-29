import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//import java.util.zip.CRC32;
//import java.util.zip.Checksum;

public class unifrog {

	/**
	 * @param args
	 */

	static boolean export = true;
	
	// Test account
	static String username = "";
	static String password = "";
	
	static String appTitle = "Unifrog";
	
	public static String[][] ItemList = new String[49][3];
	public static String ItemHash = "-1";
	
	static int version = 15;
	static Insets insets = new Insets(0, 0, 0, 0);
	static Insets hp = new Insets(3, 3, 3, 3);
	static boolean radioEnabled = false;
	static boolean alwaysTopItem = false;
	
	public static String webpage = "http://unifrog.braxnet.org";
	
	
	
	public static void main(String[] args) {
		if (export) {
			username = "Username";
			password = "Password";
		}
		System.out.println("Starting Unifrog...");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				unifrog.login();
			}
		});

	}

	public static void login() {
		final JFrame f = new JFrame(appTitle + " - Login");
		
		JEditorPane news = null;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
		
		f.setIconImage(new ImageIcon(unifrog.class.getResource("icon.png")).getImage()); // NOI18N
		f.setLayout(null);
		

		// f.setDefaultLookAndFeelDecorated(true);
		f.setResizable(false);
		f.setSize(300, 400);

		// Add login elements
		f.add(new JLabel(appTitle));

		ImageIcon titleimg = new ImageIcon(unifrog.class.getResource("title.png"));
		JLabel title = new JLabel(titleimg);
		title.setBounds(0, -10, 300, 80);
		f.add(title);
		
		Color bgColor = new Color(34,92,41);
		f.getContentPane().setBackground(bgColor);

		// Input border
		Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

		// Username
		final JTextField usrinput = new JTextField(username);
		usrinput.setBounds(5, 310, 160, 25);
		usrinput.setFont(new Font("Arial", Font.PLAIN, 12));
		usrinput.setMargin(new Insets(3, 3, 3, 3));
		usrinput.setBorder(border);
		usrinput.setForeground(new Color(120, 120, 120));

		// Password
		final JPasswordField pwdinput = new JPasswordField(password);
		pwdinput.setBounds(5, 340, 160, 25);
		pwdinput.setFont(new Font("Arial", Font.PLAIN, 12));
		pwdinput.setMargin(new Insets(3, 3, 3, 3));
		pwdinput.setBorder(border);
		pwdinput.setForeground(new Color(120, 120, 120));

		// Login button
		JButton logbtn = new JButton("Login");
		logbtn.setBounds(170, 310, 119, 25);
		//logbtn.setBackground(new Color(255, 255, 255));
		logbtn.setFont(new Font("Arial", Font.PLAIN, 11));
		//logbtn.setBorder(border);

		// Reg button
		JButton regbtn = new JButton("Create account");
		regbtn.setBounds(170, 340, 119, 25);
		//regbtn.setBackground(new Color(255, 255, 255));
		regbtn.setFont(new Font("Arial", Font.PLAIN, 11));
		//regbtn.setBorder(border);

		usrinput.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				tryLogin(usrinput.getText(), pwdinput.getText(), f);
			}
		});

		pwdinput.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				tryLogin(usrinput.getText(), pwdinput.getText(), f);
			}
		});

		usrinput.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (usrinput.getText().equals(username)) {
					usrinput.setText("");
				}
				usrinput.setBorder(BorderFactory.createLineBorder(Color.gray));
				usrinput.setForeground(new Color(20, 20, 20));
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (usrinput.getText().equals("")) {
					usrinput.setText(username);
					usrinput.setForeground(new Color(120, 120, 120));
				}
				usrinput.setBorder(BorderFactory
						.createLineBorder(Color.lightGray));
			}
		});

		pwdinput.addFocusListener(new FocusListener() {
			@SuppressWarnings("deprecation")
			public void focusGained(FocusEvent e) {
				if (pwdinput.getText().equals(password)) {
					pwdinput.setText("");
				}
				pwdinput.setBorder(BorderFactory.createLineBorder(Color.gray));
				pwdinput.setForeground(new Color(20, 20, 20));
			}

			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (pwdinput.getText().equals("")) {
					pwdinput.setText(password);
					pwdinput.setForeground(new Color(120, 120, 120));
				}
				pwdinput.setBorder(BorderFactory
						.createLineBorder(Color.lightGray));
			}
		});

		regbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(
							java.net.URI
									.create( webpage + "/register.php"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		logbtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				tryLogin(usrinput.getText(), pwdinput.getText().toString(), f);
			}
		});
		
		news = new JEditorPane();
		news.setEditable(false);
		news.addHyperlinkListener(null);
		news.setMargin(insets);
		news.setBounds(5, 63, 284, 240);
		news.setBorder(BorderFactory.createLineBorder(Color.gray));
		f.add(news);

		try {
			news.setPage(webpage + "/news.php?v=" + version);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(news != null) news.setText("<center>Could not fetch news</center>");
		}

		f.add(usrinput);
		f.add(pwdinput);
		f.add(logbtn);
		f.add(regbtn);

		// Center window
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = f.getSize().width;
		int h = f.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		f.setLocation(x, y);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		usrinput.requestFocus();
	}

	public static void tryLogin(String usr, String pwd, JFrame f) {

		try {
			pwd = AeSimpleSHA1.SHA1(pwd);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		System.out.println("User: " + usr + ", Pwd: " + pwd);

		String ret = uninet.sendGetRequest(webpage + "/handle.php",
				"username=" + usr + "&password=" + pwd + "&version=" + version
						+ "&login=true");
		
		if( ret == null || ret.equals("") ) {
			JOptionPane.showMessageDialog(f, "Server error!");
			return;
		}

		if (ret.substring(0, 1).equals("<")) {
			JOptionPane.showMessageDialog(f,
					"Server error! Wait a moment and try again.");
		}

		Object user_obj = JSONValue.parse(ret.toString());
		JSONObject obj = (JSONObject) user_obj;
		if (obj.get("error").equals("0")) {
			// Redirect to game
			username = usr;
			password = pwd;

			JSONArray iobj = (JSONArray) obj.get("items");
			int len = iobj.size();

			for (int i = 0; i < len; i++) {
				JSONObject tmpobj = (JSONObject) iobj.get(i);
				String[] tmp = new String[] { (String) tmpobj.get("file"), (String) tmpobj.get("name"), (String) tmpobj.get("type") };
				ItemList[i] = tmp;
			}
			
			ItemHash = (String) obj.get("hash");
			
			System.out.println( iobj );
			
			f.dispose();
			MainGame mg = new MainGame();
			mg.begin();

		} else {
			JOptionPane.showMessageDialog(f, obj.get("error"));
		}
	}

	public void actionPerformed(ActionEvent event) {

		String actioncommand = event.getActionCommand();
		System.out.println(actioncommand);

	}
}
