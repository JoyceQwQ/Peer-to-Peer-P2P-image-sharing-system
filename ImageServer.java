import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * The image Server class
 * the server program
 * @author hp1
 */
public class ImageServer extends JFrame {

	private JPanel buttonPanel = new JPanel();
	private JButton LoadAnotherImage = new JButton("Load another image");
	private JPanel imagePanel = new JPanel();
	private ArrayList<User> Users = new ArrayList <User> ();
	private int PeerNumber = 0;
	private ArrayList<ActivePeer> ActivePeers = new ArrayList <ActivePeer> ();
	private String message;
	private ImagePanel image;
	
	/**
	 * The User class,
	 * to store the user name, hashed password, failed login count
	 * and a boolean of whether the user account is locked
	 * for a user record
	 * @author hp1
	 */
	public class User {
		
		private String userName;
		private String HashedPassword;
		private int FailedLoginCount;
		private boolean AccountLocked;
		
		/**
		 * Create a User object
		 * FailedLoginCount: the failed login count of the user, 
		 * 					 i.e. how many times the user fails to login
		 * AccountLocked: the account locked, i.e. whether the account is locked
		 * @param userName the user name of the user
		 * @param HashedPassword the hashed password of the user input password
		 */
		public User(String userName, String HashedPassword) {
			this.userName = userName;
			this.HashedPassword = HashedPassword;
			this.FailedLoginCount = 0;
			this.AccountLocked = false;
		}
		
		/**
		 * Increase the failed login count by 1,
		 * i.e. when the user failed to login once,
		 * the failed count will increase by 1
		 */
		public void IncreaseFailedCount() {
			this.FailedLoginCount++;
		}
		
		/**
		 * Lock this account
		 * If the failed count is greater than or equal to 3,
		 * the user account is locked,
		 * and the user will not be allowed to login again.
		 * @return the AccountLocked of this User object as Boolean
		 * 		   true if the account is locked;
		 * 		   false if not, as the initial AccountLocked is false
		 * 		   and it is not locked
		 */
		public boolean LockAccount() {
			if (this.FailedLoginCount >= 3) {
			    this.AccountLocked = true;
			}
			return this.AccountLocked;
		}
		
		/**
		 * Reset the failed count to 0,
		 * If the failed login count is less than 3,
		 * and the user can login successfully.
		 */
		public void ResetFailedCount() {
			if (this.FailedLoginCount < 3) {
				this.FailedLoginCount = 0;
			}
		}
		
	}
	
	
	/**
	 * the main method of the image server
	 * @param args the arguments of main method
	 */
	public static void main(String[] args) {
		JFileChooser jfc = new JFileChooser();
		File file = null;
		int returnVal = jfc.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	file = jfc.getSelectedFile();
	    }
	    try {
	    	BufferedImage newImage = ImageIO.read(file);
	    	new ImageServer(newImage);
	    }
	    catch(IOException e) 
        { 
	    	e.printStackTrace();
        }
	}
	
	/**
	 * The constructor of image server
	 * When ImageServer.class is executed, 
	 * a file chooser should be presented to ask for an image file.
	 * The image is then loaded and displayed.
	 * If the image file fails to load,
	 * the program terminates. 
	 * Otherwise, the server should listen to the port 9000, 
	 * and load the User.txt which contains the user information into the program 
	 * and waiting for user to login.
	 * The user information in User.txt could be used to login 
	 * and add into the P2P images sharing system.
	 * Waiting for a new peer program to connect to the server socket continuously 
	 * @param newImage the new image in buffered image
	 */
	public ImageServer(BufferedImage newImage) {
	    super("Image Server");
	    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    buttonPanel.add(LoadAnotherImage);
	    LoadAnotherImage.addActionListener(new Load());
	    buttonPanel.setLayout(new GridLayout(1, 1));
	    super.getContentPane().add(BorderLayout.SOUTH,buttonPanel);
	    super.getContentPane().add(BorderLayout.NORTH,imagePanel);
	    ImagePanel imageP = new ImagePanel(newImage, "Server");
		if (! imageP.fail) {
			image = imageP;
			imagePanel.removeAll();
			imagePanel.add(image);
			imagePanel.revalidate();
		}
		else {
			System.exit(0);
		}
	    super.setSize(730,780);
	    super.setVisible(true);
	    ServerSocket ss = null;
	    try {
	    	File file = new File("User.txt");
	    	BufferedReader br = new BufferedReader(new FileReader(file));
	    	String line = br.readLine();
	    	int lengthUser = 9, lengthPassowrd = 14;
	    	while (line != null) {
	    		int indexHashedPassword = line.indexOf(";hashPassword:");
	    		String userName = line.substring(lengthUser, indexHashedPassword);
	    		String HashedPassword = line.substring(indexHashedPassword+lengthPassowrd);
	    		User user = new User (userName, HashedPassword);
	    		Users.add(user);
				line = br.readLine();
	    	}
	    	br.close();
			ss = new ServerSocket(9000);
	    	ActivePeers.add(new ActivePeer("127.0.0.1", 9000));
	    	image.UpdateActivePeers(ActivePeers);
	    	while (true) {
	    		Socket s = ss.accept();
	    		ClientHandler ch = new ClientHandler(s);
	    		Thread t = new Thread(ch);
				t.start();
	    	} 
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * The class ClientHander
	 * Implements the interface Runnable
	 * Contains the streams for the Server Socket
	 * Make the server become a multithreaded server
	 * @author hp1
	 */
	class ClientHandler implements Runnable {
		/**
		 * The Output Stream of the server
		 */
		OutputStream os;
		/**
		 * The Object Output Stream of the server
		 */
		ObjectOutputStream oos;
		/**
		 * The Input Stream of the server
		 */
		InputStream is;
		/**
		 * The Object Input Stream of the server
		 */
		ObjectInputStream ois;
		
		/**
		 * /**
		 * The constructor of the ClientHandler
		 * Initialize the streams of ClientHandler
		 * for the accepted socket
		 * @param s the client socket to be connected
		 */
		public ClientHandler(Socket s) {		
			try {
				os = s.getOutputStream();
				oos = new ObjectOutputStream(os);
				is = s.getInputStream();
				ois = new ObjectInputStream(is);
			} catch (IOException e) {
				System.out.println("ClientHandler error");
				e.printStackTrace();
			}
		}
		
		/**
		 * The required method for the interface Runnable
		 * Creates new threads with the same functions
		 * for each client
		 * i.e. multithreaded
		 */
		public void run() {
			this.ch_go();
		}
		
		/**
		 * Update the list of active peers whenever there's an update
		 * and send to them
		 * send the image blocks to the peer
		 * Besides, update the list of active peers when a peer program stops
		 */
		public void ch_go() {
				try {
					String action = (String) ois.readObject();
					if (action.contentEquals("log in")) {
						String IPAddress = (String)ois.readObject();
						String Username=(String)ois.readObject();
						String HashedPassword=(String)ois.readObject();
						boolean Successful = Authentication(Username, HashedPassword);
						oos.writeObject(Successful);
						oos.writeObject(message);
						if (Successful) {
							PeerNumber++;
							int PortNumber = PeerNumber + 9000;
							ActivePeer newPeer = new ActivePeer(IPAddress, PortNumber);
							ActivePeers.add(newPeer);
							image.UpdateActivePeers(ActivePeers);
							oos.writeObject(PeerNumber);
							oos.writeObject(ActivePeers);
							message = "List Update";
							for (ActivePeer activePeer : ActivePeers) {
								if (activePeer.PortNumber != 9000) {
									Socket s = new Socket(activePeer.IPAddress,activePeer.PortNumber);
									new PeerThread(s, message, image).start();
								}
							}
						}
		    		}
					else if (action.contentEquals("exit")) {
						int i = (int) ois.readObject();
						if (i >= 0) {
							ActivePeers.remove(i);
							image.UpdateActivePeers(ActivePeers);
							System.out.println(i == ActivePeers.size());
							if (i == ActivePeers.size()) {
								PeerNumber--;
							}
							else if (ActivePeers.size() == 1) {
								PeerNumber = 0;
							}
							message = "List Update";
							for (ActivePeer activePeer : ActivePeers) {
								if (activePeer.PortNumber != 9000) {
									Socket s = new Socket(activePeer.IPAddress,activePeer.PortNumber);
									new PeerThread(s, message, image).start();
								}
							}
						}
					}
					else if (action.contentEquals("Requiring Blocks")) {
						int start = (int) ois.readObject();
						int numPeers = (int) ois.readObject();
						int end = (start+1)*100/numPeers;
						if (start == numPeers - 1) {
							end = 100;
						}
						for (int i = start*100/numPeers; i< end; i++) {
		    				BufferedImage subImage = image.NumberedBlocks.get(i).subImage;
		    				ImageIcon imageIcon = new ImageIcon(subImage);
		    				oos.writeObject(i);
		    				oos.writeObject(imageIcon);
		    			}
					}
		    		oos.close();
					ois.close();
					return;
				} catch ( Exception e ) {
					System.out.println("Exception in command processing");
					e.printStackTrace();
					return;
				}
		}
	}
	
	/**
	 * Get the index of the user the system is looking for
	 * in the ArrayList Users
	 * @param Username the user name of the user that
	 *        this system needs to look for in the ArrayList Users
	 * @return the index of the Usernmae (User) in the ArrayList Users;
	 * 		   or -1 if the user does not exist in the ArrayList Users
	 */
	public int getUserIndex(String Username) {
		int Index = -1;
		for (int i=0; i < Users.size(); i++) {
			User u = Users.get(i);
			if (Username.equals(u.userName)) {
				Index = i;
			}
		}
	    return Index;
	}
	
	/**
	 * Authentication with error handlings and error count,
	 * i.e. Login process for the user
	 * The account will be validated
	 * if the user can provide a correct username and password.
	 * If the user tries to login with a username that does not exist, 
	 * “User not found!” will be printed out.
	 * If the user enters a wrong password, 
	 * “Login failed!” will be printed out.
	 * If the failed count is less than 3 and the user can login successfully,
	 * the failed count will reset to 0.
	 * If the failed count is greater than or equal to 3, 
	 * the user account is locked, 
	 * and the user will not be allowed to login again. 
	 * In this case, no matter the user has input a correct password not, 
	 * “Login failed! Your account has been locked!” will be printed out.
	 * i.e. the user cannot login anymore
	 * @param Username the user input username got from the peer
	 * @param HashedPassword the hashed password from the peer's input password
	 * @return true if the login succeeded
	 * 		   false if not
	 */
	public boolean Authentication(String Username, String HashedPassword) {
		boolean SuccessfulLogin = false;
		if (getUserIndex(Username) != -1) {
			User u = Users.get(getUserIndex(Username));
			if (u.LockAccount()) {
			    message = "Login failed! Your account has been locked!";
			}
			else {
			    if (HashedPassword.equals(u.HashedPassword)) {
				    u.ResetFailedCount();
					message = "Login success! Hello " + Username + "!";
					SuccessfulLogin = true;
				}
				else {
					u.IncreaseFailedCount();
					message = "Login failed!";
				}
			}
		}
		else {
			message = "User not found!";
		}
		return SuccessfulLogin;
	}
	
	/**
	 * The Load ActionListener
	 * Implements the interface ActionListener
	 * for the button "Load another image"
	 * @author hp1
	 */
	class Load implements ActionListener {
    	
		/**
		 * Change the current image
		 * When the JButtton "Load another image" is pressed,
		 * a file chooser should be presented to ask for an image file.
		 * If the new image fails to load, the old image is retained.
		 */
		public void actionPerformed(ActionEvent ae) {
			if(ae.getSource() == LoadAnotherImage) {
				JFileChooser jfc = new JFileChooser();
				File file = null;
				int returnVal = jfc.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	file = jfc.getSelectedFile();
			    }
			    try {
			    	BufferedImage newImage = ImageIO.read(file);
			    	ImagePanel imageP = new ImagePanel(newImage, "Server");
			    	if (! imageP.fail) {
			    		image = imageP;
			    		image.UpdateActivePeers(ActivePeers);
			    		imagePanel.removeAll();
			    		imagePanel.add(image);
			    		imagePanel.revalidate();
			    		message = "Image Update";
			    		for (ActivePeer activePeer : ActivePeers) {
		    				if (activePeer.PortNumber != 9000) {
		    					Socket s = new Socket(activePeer.IPAddress,activePeer.PortNumber);
		    					new PeerThread(s, message, image).updateBlocks();
		    				}
						}
			    	}
			    } catch (IOException e) {
			    	System.out.println("Another image file fails to load");
			    }
			}
		}
	}
	
}
