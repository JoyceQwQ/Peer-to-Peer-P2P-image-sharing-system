import java.io.Serializable;

/**
 * The class ActivePeer
 * Implements the interface Serializable
 * Contains the peer IP address and port number
 * @author hp1
 */
public class ActivePeer implements Serializable {
	/**
	 * The IPAddress of the Peer
	 */
	String IPAddress;
	/**
	 * The PortNumber of the Peer
	 */
	int PortNumber;
		
	/**
	 * Create an Active Peer object with the peer IP address
	 * and the port number of the peer
	 * @param IPAddress the peer IP address
	 * @param PortNumber the peer port number
	 */
	public ActivePeer(String IPAddress, int PortNumber) {
		this.IPAddress = IPAddress;
		this.PortNumber = PortNumber;
	}
}