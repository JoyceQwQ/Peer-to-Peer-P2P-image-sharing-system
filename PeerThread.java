import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;

import javax.swing.ImageIcon;

/**
	 * The peer thread class, to make the server work as a peer
	 * Extends Thread
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
		private ImagePanel image;

		/**
		 * The constructor of the peer thread
		 * @param s the socket which the server wants to be connected
		 * @param message the message to tell the peer server what actions to be taken
		 * @param image the imagepanel of this peer program
		 * @throws IOException when oos and ois are initialised
		 */
		public PeerThread(Socket s, String message, ImagePanel image) throws IOException {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			this.message = message;
			this.image = image;
		}
		
		/**
		 * Update the blocks when another image is loaded
		 * Write a message to the peer,
		 * write the updated blocks of the new image in image icon
		 * and finally close oos and ois
		 * @throws IOException IOException for the file handling
		 */
		public void updateBlocks() throws IOException {
			oos.writeObject(message);
			for (int i = 0; i<100; i++) {
				BufferedImage subImage = image.NumberedBlocks.get(i).subImage;
		    	ImageIcon imageIcon = new ImageIcon(subImage);
			    oos.writeObject(imageIcon); 
		    }
			oos.close();
			ois.close();
		}
		
		/**
		 * The image will be updated
		 * when the user drags and drops the image block 
		 * using the server program GUI. 
		 * For such case, 
		 * the client only required to download the swapped image blocks.
		 * 1. Write a message to the peer server to notice it
		 * 2. Write the two swapped blocks
		 * @param ChosenIndex the Index of the first block in the list of numbered blocks 
		 * @param SwappedIndex the Index of the second block in the list of numbered blocks
		 * @throws IOException when oos encounters some problems when writing objects
		 * 					   to the peer server
		 */
		public void swapBlocks(int ChosenIndex, int SwappedIndex) throws IOException {
			oos.writeObject(message);
			BufferedImage subImage = image.NumberedBlocks.get(ChosenIndex).subImage;
	    	ImageIcon imageIcon = new ImageIcon(subImage);
	    	oos.writeObject(ChosenIndex);
		    oos.writeObject(imageIcon); 
		    subImage = image.NumberedBlocks.get(SwappedIndex).subImage;
	    	imageIcon = new ImageIcon(subImage);
	    	oos.writeObject(SwappedIndex);
		    oos.writeObject(imageIcon); 
			oos.close();
			ois.close();
		}
		
		/**
		 * The run method of the peer thread for the server
		 * to send message and the list of active peers
		 * to the peer server
		 * When a peer program is started,
		 * the server should then update the list of active peers, 
		 * and each peer should collect the list of current active peers 
		 * from the server. 
		 * This connection can be closed afterwards.
		 */
		public void run() {
			boolean flag = true;
			while (flag) {
				try {
					oos.writeObject(message);
					oos.writeObject(image.ActivePeers);
					oos.close();
					ois.close();
				} catch (Exception e) {
					flag = false;
					interrupt();
				}
			}
		}
	}