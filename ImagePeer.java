import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.swing.*;

/**
 * The image peer class
 * implements the interface Hash
 * @author hp1
 */
public class ImagePeer implements Hash{
	
	private JFrame jf;
	private JPanel imagePanel;
	private ImagePanel image;
	private ArrayList<ActivePeer> ActivePeers = new ArrayList <ActivePeer> ();
	private int numPeers, PeerNumber, PortNum;
	private ArrayList<NumberedBlock> NumberedBlocks;
	private ServerThread serverThread;
	
	/**
	 * The main method
	 * to build an imagepeer object
	 * @param args the arguments of the main method
	 */
	public static void main(String[] args) {
		new ImagePeer();
	}
	
	/**
	 * The constructor of Image Peer
	 * To log in
	 * Connect with the server program
	 * And try to login with username input, password input
	 * The client should use the hash interface to hash the password 
	 * before sending to the server and
	 * the server needs to verify the user information 
	 * before starting the main GUI
	 * Build the server thread of the peer program
	 * Besides, when the JFrame of the peer program is closed,
	 * i.e. terminating the program,
	 * the peer program will notice the server it's no longer active before closing
	 */
	public ImagePeer () {
		String IPAddress = JOptionPane.showInputDialog(null, "Connect to server:", 
				"localhost");
		String Username = JOptionPane.showInputDialog(null, "Username");
		String Password = JOptionPane.showInputDialog(null, "Password");
		String HashedPassword =  Hashing(Password);
		try {
			Socket s=new Socket(IPAddress,9000);
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject("log in");
			oos.writeObject(IPAddress); 
			oos.writeObject(Username); 
			oos.writeObject(HashedPassword);
			boolean Successful = (boolean) ois.readObject();
			String message = (String) ois.readObject();
			System.out.println(message);
			if (!Successful) {
    			JOptionPane.showMessageDialog(null, "Login Fail!", 
        				"Message" , JOptionPane.INFORMATION_MESSAGE);
    			oos.close();
    			ois.close();
    			s.close();
    			System.exit(0);
    		}
    		else {
    			PeerNumber = (int) ois.readObject();
    			jf = new JFrame("Image Peer *" + PeerNumber);
				imagePanel = new JPanel();
				jf.addWindowListener(/**
				 * @author hp1
				 * the new Window Adapter
				 * To listen to the window events,
				 * i.e. if the JFrame of the peer program is closed in this case
				 */
				new WindowAdapter() {
		            /**
		             * The windowClosing method
		             * When the JFrame of the peer program is closed,
		             * a socket to the server program will be created
		             * The closing peer program will notice the server program that it will be closed soon
		             * and send the corresponding index in the list of active peers to the server
		             * in order to remove the inactive peer
		             */
		            @Override
		            public void windowClosing(WindowEvent we) {
		            	try {
		            		Socket s=new Socket(IPAddress,9000);
		            		ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
		        			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		        			oos.writeObject("exit");
		        			int index = -1;
		        			for (int i = 0; i < ActivePeers.size(); i++) {
		        				if (ActivePeers.get(i).PortNumber == PortNum) {
		        					index = i;
		        				}
		        			}
		        			System.out.println("Closed Port number "+index+" "+PortNum);
		        			oos.writeObject(index);
		        			oos.close();
		        			ois.close();
		        			s.close();
		            		System.exit(0);
		            	} catch (Exception e) 
		                { 
		        	    	e.printStackTrace();
		        	    	System.exit(0);
		            	}
		            }
				});
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setSize(750,750);
				jf.getContentPane().add(BorderLayout.CENTER,imagePanel);
			    jf.setVisible(true);
			    ActivePeers = (ArrayList<ActivePeer>) ois.readObject();
			    numPeers = (ActivePeers.size() - 1);
			    NumberedBlocks = new ArrayList<NumberedBlock>();
				oos.close();
				ois.close();
				s.close();
    		}
			PortNum = PeerNumber + 9000;
			System.out.println("Port Number: "+PortNum);
			for (int i = 0; i<ActivePeers.size()-1; i++) {
				ActivePeer activePeer = ActivePeers.get(i);
				Socket socket = new Socket(activePeer.IPAddress,activePeer.PortNumber);
				new PeerThread(socket, "Requiring Blocks", i).start();
			}
			if (NumberedBlocks.size()== 100) {
				image = new ImagePanel (NumberedBlocks, "Peer");
	            imagePanel.removeAll();
				imagePanel.add(image);
				imagePanel.revalidate();
			}
			serverThread = new ServerThread(PortNum);
			serverThread.start();
		} catch (Exception e) 
        { 
	    	e.printStackTrace();
	    	System.exit(0);
        }
	}
	
	/**
	 * Hashing method for the input password
	 * before sending to the server for verification
	 * @param the input password
	 */
	public String Hashing(String Password) {
		try { 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
            md.update(Password.getBytes());
    		byte[] hash = md.digest();
    		BigInteger no = new BigInteger(1, hash);
    		String HashedPassword = no.toString(16);
    		while (HashedPassword.length() < 32) { 
                HashedPassword = "0" + HashedPassword; 
            }
            return HashedPassword; 
        } 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        }
	}
	
	/**
	 * The serverthread class
	 * extends thread
	 * to make the peer work as a server
	 * @author hp1
	 *
	 */
	public class ServerThread extends Thread {
		/**
		 * The Server Socket
		 */
		ServerSocket ss;
		private Set<ServerThreadThread> serverThreadThreads = new HashSet <ServerThreadThread>();
		
		/**
		 * The constructor of the server thread
		 * with the port number of this peer program,
		 * i.e. port number = 9000 + the current peer index
		 * @param PortNum the port number for the server
		 * @throws IOException
		 */
		public ServerThread(int PortNum) throws IOException {
			ss = new ServerSocket(PortNum);
		}
		
		/**
		 * The run method for the server thread
		 * to build the connection with ServerThread Thread
		 * i.e. manage the peer(s) connected to the peer server
		 */
		public void run() {
			try {
				while (true) {
					ServerThreadThread serverThreadThread = new ServerThreadThread (ss.accept(), this);
					serverThreadThreads.add(serverThreadThread);
					serverThreadThread.start();
				}
			} catch (Exception e) {e.printStackTrace(); }
		}
		
		/**
		 * Get the Set of the connected serverThread Thread 
		 * @return this.serverThreadThreads in Set
		 */
		public Set<ServerThreadThread> getServerThreadThreads() {
			return serverThreadThreads;
		}
	}
	
	/**
	 * The serverThreadThread class
	 * extends Thread
	 * manage the actions for the serverthread
	 * whenever a socket is connected to the server
	 * @author hp1
	 *
	 */
	public class ServerThreadThread extends Thread {
		private ServerThread serverThread;
		private Socket s;
		/**
		 * The Output Stream of the Peer as a Server
		 */
		OutputStream os;
		/**
		 * The Object Output Stream of the Peer as a Server
		 */
		ObjectOutputStream oos;
		/**
		 * The Input Stream of the Peer as a Server
		 */
		InputStream is;
		/**
		 * The Object Input Stream of the Peer as a Server
		 */
		ObjectInputStream ois;
		
		/**
		 * The constructor of the Server Thread Thread
		 * @param s the socket connected
		 * @param serverThread the Server Thread
		 */
		public ServerThreadThread (Socket s, ServerThread serverThread) {
			this.serverThread = serverThread;
			this.s = s;
		}
		
		/**
		 * The run method of this Server Thread Thread
		 * to get the updated list of active peers, 
		 * updated and separated image blocks when the user switches the image,
		 * send the required separated blocks other peers needed
		 * and the swapped blocks when the user drags and drops the image block
		 * using the server program GUI
		 */
		public void run() {
			try {
				this.os = s.getOutputStream();
				this.oos = new ObjectOutputStream(os);
				this.is = s.getInputStream();
				this.ois = new ObjectInputStream(is);
				String message = (String) ois.readObject();
				if (message.contentEquals("List Update")) {
					ActivePeers = (ArrayList <ActivePeer>) ois.readObject();
				}
				else if (message.contentEquals("Image Update")) {
					NumberedBlocks = new ArrayList<NumberedBlock>();
		            for (int i = 0; i < 100; i++) {
		            	ImageIcon sub = (ImageIcon)ois.readObject();
		            	Image img = sub.getImage();
		            	BufferedImage subimage = new BufferedImage(70, 70,
		    					BufferedImage.TYPE_INT_RGB);
		    			Graphics2D g2d = subimage.createGraphics();
		    			g2d.drawImage(img, 0, 0, null);
		    			g2d.dispose();
		    			NumberedBlocks.add(new NumberedBlock(i, subimage));
		    		}
		            image = new ImagePanel (NumberedBlocks, "Peer");
		            imagePanel.removeAll();
					imagePanel.add(image);
					imagePanel.revalidate();
				}
				else if (message.contentEquals("Requiring Blocks")) {
					int start = (int) ois.readObject();
					int numPeers = (int) ois.readObject();
					int end = (start+1)*100/numPeers;
					if (start == numPeers - 1) {
						end = 100;
					}
					for (int i = start*100/numPeers; i< end; i++) {
	    				BufferedImage subImage = NumberedBlocks.get(i).subImage;
	    				ImageIcon imageIcon = new ImageIcon(subImage);
	    				oos.writeObject(i);
	    				oos.writeObject(imageIcon);
	    			}
				}
				else if (message.contentEquals("Swapped Blocks")) {
					for (int i = 0; i < 2; i++) {
						int ChosenIndex = (int) ois.readObject();
						ImageIcon sub = (ImageIcon)ois.readObject();
						Image img = sub.getImage();
						BufferedImage subimage = new BufferedImage(70, 70,
								BufferedImage.TYPE_INT_RGB);
						Graphics2D g2d = subimage.createGraphics();
						g2d.drawImage(img, 0, 0, null);
						g2d.dispose();
						NumberedBlocks.set(ChosenIndex, new NumberedBlock(ChosenIndex, subimage));
					}
					image = new ImagePanel (NumberedBlocks, "Peer");
		            imagePanel.removeAll();
					imagePanel.add(image);
					imagePanel.revalidate();
				}
				os.close();
				oos.close();
				is.close();
				ois.close();
				s.close();
			} catch (Exception e) {
				serverThread.getServerThreadThreads().remove(this);
			}
		}
	}
	
	/**
	 * The peer thread
	 * for the peer to send requests to other peer servers
	 * @author hp1
	 */
	public class PeerThread extends Thread {
		/**
		 * The Object Input Stream of the Server as a Peer
		 */
		ObjectInputStream ois;
		/**
		 * The Object Output Stream of the Server as a Peer
		 */
		ObjectOutputStream oos;
		private String message;
		private int start;
		
		/**
		 * The constructor of peer thread
		 * @param s the socket for connecting to the targeted peer server
		 * @param message the message for telling the peer server
		 * 				  what this peer program needs, i.e. requiring blocks
		 * @param start the start of the arraylist of numbered blocks
		 * 				that this peer program wants from the targeted peer server
		 * @throws IOException when ois and oos are initialized
		 */
		public PeerThread(Socket s, String message, int start)  throws IOException {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			this.message = message;
			this.start = start;
		}
		
		/**
		 * The run method of the peer thread
		 * to write a message and numPeers to the peer servers
		 * and get the required blocks
		 */
		public void run() {
			boolean flag = true;
			while (flag) {
				try {
					oos.writeObject(message);
					oos.writeObject(start);
					oos.writeObject(numPeers);
					int end = (start+1)*100/numPeers;
					if (start == numPeers - 1) {
						end = 100;
					}
					for (int i = start*100/numPeers; i < end; i++) {
						int number = (int)ois.readObject();
					   	ImageIcon sub = (ImageIcon)ois.readObject();
					   	Image img = sub.getImage();
					   	BufferedImage subimage = new BufferedImage(70, 70,
					   			BufferedImage.TYPE_INT_RGB);
					   	Graphics2D g2d = subimage.createGraphics();
					   	g2d.drawImage(img, 0, 0, null);
					   	g2d.dispose();
					   	NumberedBlocks.add(new NumberedBlock(number, subimage));
					}
					if (NumberedBlocks.size()== 100) {
						System.out.println("The image JPanel is ready!");
						Collections.sort(NumberedBlocks, new Sortbynumber());
						image = new ImagePanel (NumberedBlocks, "Peer");
			            imagePanel.removeAll();
						imagePanel.add(image);
						imagePanel.revalidate();
						NumberedBlocks = image.NumberedBlocks;
					}
				} catch (Exception e) {
					flag = false;
					interrupt();
				}
			}
		}
	}
	
}
