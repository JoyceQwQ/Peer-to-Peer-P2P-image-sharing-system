import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

import javax.swing.JPanel;

/**
 * The image panel class,
 * extends JPanel
 * Implements the interface MouseListener 
 * to contain the JPanel of the image 
 * @author hp1
 */
public class ImagePanel extends JPanel implements MouseListener {
    	private BufferedImage image;
    	private Image resized;
    	private int ChosenIndex = -1;
    	private String ServerPeer;
    	
    	/**
    	 * The list of active peers
    	 */
    	ArrayList<ActivePeer> ActivePeers;
    	/**
    	 * The boolean to indicate if the image is successfully 
    	 * resized and scaled to fit into a canvas of 700*700 pixels and 
    	 * separated into blocks of 70*70 pixels in size
    	 * true if it fails
    	 * false if not
    	 */
    	boolean fail = false;
    	/**
    	 * The arraylist of separated image blocks with the corresponding number
    	 */
    	ArrayList<NumberedBlock> NumberedBlocks = new ArrayList<NumberedBlock>();
    	
    	
    	/**
    	 * The constructor of ImagePanel for the ImageServer
    	 * @param newImage the new image of the JPanel
    	 * @param ServerPeer "Server" in String to point out
    	 * 		  this imagePanel belongs to the server
    	 */
    	public ImagePanel(BufferedImage newImage, String ServerPeer) {
    	    super(true);
    	    try {
    	    	image = newImage;
    	    	this.ServerPeer = ServerPeer;
    	        this.setPreferredSize(new Dimension(700, 700));
    	        super.addMouseListener(this);
    	        resized = image.getScaledInstance(700, 700, Image.SCALE_SMOOTH);
    	    	BufferedImage buffer = new BufferedImage(700, 700,
    					BufferedImage.TYPE_INT_RGB);
    			Graphics2D g2d = buffer.createGraphics();
    			g2d.drawImage(resized, 0, 0, null);
    			g2d.dispose();
    			image = buffer;
    			for (int i = 0; i < 10; i++) {
    				for (int j = 0; j < 10; j++) {
    					try {
    						BufferedImage subImage = buffer.getSubimage(i*70, j*70, 70, 70);
    						NumberedBlock block = new NumberedBlock (i*10+j, subImage);
    						NumberedBlocks.add(block);
    						
    					} catch (Exception e) {
    						System.out.println("subImages cannot be added");
    					}
    				}
    			}
    	    }   catch (Exception e) {
    	    	System.out.println("The new image fails to load");
    	    	fail = true;
    	    }
    	}
    	
    	/**
    	 * The constructor of imagePanel for the Peer
    	 * @param NumberedBlocks the list of the blocks with number
    	 * @param ServerPeer "Peer" in String to point out
    	 * 		  this imagePanel belongs to the peer
    	 */
    	public ImagePanel (ArrayList<NumberedBlock> NumberedBlocks, String ServerPeer) {
    		super(true);
    		super.addMouseListener(this);
    		this.ServerPeer = ServerPeer;
    		this.setPreferredSize(new Dimension(700, 700));
    		this.NumberedBlocks = NumberedBlocks;
    	}
    	
    	/**
    	 * Paint the separated blocks on the imagePanel
    	 */
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		for (int i = 0; i < 10; i++) {
    			for (int j = 0; j < 10; j++) {
    				g.drawImage(NumberedBlocks.get(i*10+j).subImage, i*70, j*70, null);
    			}
    		}
    	}
    	
    	/**
    	 * Update the list of Active Peers
    	 * when a peer is added or removed
    	 * @param ActivePeers the new list of Active Peers
    	 */
    	public void UpdateActivePeers(ArrayList<ActivePeer> ActivePeers) {
    		this.ActivePeers = ActivePeers;
    	}
    	
    	/**
    	 * When the image block is pressed, the coordinate will be recorded
    	 * For the user to drag the image block using the server program GUI
    	 */
    	@Override
    	public void mousePressed(MouseEvent e) {
    		// TODO Auto-generated method stub
    		if (ServerPeer.equals("Server")) {
    			int x = e.getX()/70;
    			int y = e.getY()/70;
    			ChosenIndex = x*10 + y;
    		}
    	}
    	
    	/**
    	 * When the user drags the image block using the server program GUI
    	 * the corresponding image blocks are swapped
    	 * The imagePanel will be repainted.
    	 */
    	@Override
    	public void mouseReleased(MouseEvent e) {
    		// TODO Auto-generated method stub
    		if (ServerPeer.equals("Server")) {
    			System.out.println(ActivePeers.size());
    			int x = e.getX()/70;
    			int y = e.getY()/70;
    			Collections.swap(NumberedBlocks, ChosenIndex, x*10+y);
    			repaint();
    			String message = "Swapped Blocks";
    			try {
    				for (ActivePeer activePeer : ActivePeers) {
    					if (activePeer.PortNumber != 9000) {
    						Socket s = new Socket(activePeer.IPAddress,activePeer.PortNumber);
    						new PeerThread(s, message, this).swapBlocks(ChosenIndex, x*10+y);
    					}
    				}
    			} catch (IOException ex) {
			    	System.out.println("Another image file fails to load");
			    }
    		}
    	}
    	
    	/**
    	 * The mouseClicked method
    	 * As it is not used in this program, no code is inside
    	 * To regulate the implemented interface MouseListener
    	 */
    	@Override
    	public void mouseClicked(MouseEvent e) {
    		// TODO Auto-generated method stub
    	}
    	
    	/**
    	 * The mouseEntered method
    	 * As it is not used in this program, no code is inside
    	 * To regulate the implemented interface MouseListener
    	 */
    	@Override
    	public void mouseEntered(MouseEvent e) {
    		// TODO Auto-generated method stub
    	}

    	/**
    	 * The mouseExited method
    	 * As it is not used in this program, no code is inside
    	 * To regulate the implemented interface MouseListener
    	 */
    	@Override
    	public void mouseExited(MouseEvent e) {
    		// TODO Auto-generated method stub
    	}
    	
	}
	