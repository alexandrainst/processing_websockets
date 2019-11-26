package websockets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * 
 * @author Lasse Steenbock Vestergaard
 * @author Abe Pazos (changes)
 *
 * Intermediate class responsible for keeping track of client connections,
 * and propagate method calls between the websocket events and the Processing sketch
 *
 */
public class WebsocketServerController {

	private List<WebsocketServerEvents> members = new ArrayList<>();
	private Object parent;
	private Method serverEvent;
	private Method serverEventBinary;
	
	/**
	 * 
	 * Initiates the communication management between websocket events and
	 * the Processing sketch
	 *
	 * @param listenerObject The Processing sketch's PApplet object
	 */
	public WebsocketServerController(Object p, Method serverEvent, Method serverEventBinary){
		parent = p;
		this.serverEvent=serverEvent;
		this.serverEventBinary=serverEventBinary;
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
	 * @param message String to send to all clients
	 */
	public void writeAllMembers(String message) {
		for (WebsocketServerEvents member : members) {
			member.session.getRemote().sendStringByFuture(message);
		}
	}

	/**
	 *
	 * Writes a message to all active clients
	 *
	 * @param data byte[] to send to all clients
	 */
	public void writeAllMembers(byte[] data) {
		for (WebsocketServerEvents member : members) {
			try {
				ByteBuffer buf = ByteBuffer.wrap(data);
				member.session.getRemote().sendBytes(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Send message to one member
	 *
	 * @param message String to send to client
	 * @param uid id of the specific client
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
		    	System.err.println("Disabling webSocketEvent()");
		    	e.printStackTrace();
		    	serverEvent = null;
		    }
		}
	}

	public void sendToOnBinaryListener(byte[] buf, int offset, int length){
		if (serverEventBinary != null) {
		    try {
					serverEventBinary.invoke(parent, buf, offset, length);
		    } catch (Exception e) {
					System.err.println("Disabling webSocketEvent()");
					e.printStackTrace();
					serverEventBinary = null;
		    }
		}
	}
}
