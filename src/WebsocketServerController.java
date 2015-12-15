package websockets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * 
 * @author Lasse Steenbock Vestergaard
 *
 * Intermediate class responsible for keeping track of client connections, and propagate method calls between the websocket events and the Processing sketch 
 *
 */
public class WebsocketServerController {

	private List<WebsocketServerEvents> members = new ArrayList<>();
	private PApplet parent;
	private Method serverEvent;
	
	/**
	 * 
	 * Initiates the communication management between websocket events and the Processing sketch
	 * 
	 * @param p The Processing sketch's PApplet object
	 * @param serverEvent The Processing sketch's websocket event function
	 */
	public WebsocketServerController(PApplet p, Method serverEvent){
		parent=p;
		this.serverEvent=serverEvent;
	}

	/**
	 * 
	 * Add new client to the list of all active clients
	 * 
	 * @param socket The specific connection between client and server
	 */
	public void join(WebsocketServerEvents socket) {
		members.add(socket);
	}

	/**
	 * 
	 * Removes client to the list of all active clients
	 * 
	 * @param socket The specific connection between client and server
	 */
	public void remove(WebsocketServerEvents socket) {
		members.remove(socket);
	}

	/**
	 * 
	 * Writes a message to all active clients
	 * 
	 * @param message Message to send to all clients
	 */
	public void writeAllMembers(String message) {
		for (WebsocketServerEvents member : members) {
			member.session.getRemote().sendStringByFuture(message);
		}
	}

	/**
	 * 
	 * This method is not yet fully implemented, and therefore not working!
	 * 
	 * @param memberName Name of the specific client
	 * @param message Message to send to client
	 */
	public void writeSpecificMember(String memberName, String message) {
		WebsocketServerEvents member = findMemberByName(memberName);
		member.session.getRemote().sendStringByFuture(message);
	}

	/**
	 * 
	 * Find specific client connection from a name. This functionality is not yet implemented!
	 * 
	 * @param memberName Name of the client connection
	 * @return The connection object
	 */
	public WebsocketServerEvents findMemberByName(String memberName) {
		return null;
	}
	
	/**
	 * 
	 * Sends incoming message directly to the Processing sketch's websocket event function
	 * 
	 * @param message The message that has been received
	 */
	public void sendToOnMessageListener(String message){
		if (serverEvent != null) {
		    try {
		    	serverEvent.invoke(parent, message);
		    } catch (Exception e) {
		    	System.err.println("Disabling webSocketEvent() because of an error.");
		    	e.printStackTrace();
		    	serverEvent = null;
		    }
		}
	}
}
