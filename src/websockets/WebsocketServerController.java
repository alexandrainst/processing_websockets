package websockets;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Map;

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

	private final Map<WebsocketServerEvents, String> memberUIDs =
			new HashMap<>();
	private final Map<String, WebsocketServerEvents> memberSockets =
			new HashMap<>();

	private final Object listenerObject;
	private Method eventListener = null;
	private Method eventBinaryListener = null;

	private static SecureRandom random = new SecureRandom();

	/**
	 * 
	 * Initiates the communication management between websocket events and
	 * the Processing sketch
	 *
	 * @param listenerObject The Processing sketch's PApplet object
	 */
	public WebsocketServerController(Object listenerObject) {
		this.listenerObject = listenerObject;

		try {
			eventListener = listenerObject.getClass().getMethod(
					"webSocketServerEvent", String.class);
		} catch (NoSuchMethodException e) {
			// ok to ignore
		}

		try {
			eventBinaryListener = listenerObject.getClass().getMethod(
					"webSocketServerEvent", byte[].class, int.class, int.class);
		} catch (NoSuchMethodException e) {
			// ok to ignore
		}
	}

	/**
	 * 
	 * Add new client to the list of all active clients
	 * 
	 * @param socket The specific connection between client and server
	 */
	public void join(WebsocketServerEvents socket) {
		String IP = getIP(socket);
		String UID = getUID();

		memberUIDs.put(socket, UID);
		memberSockets.put(UID, socket);

		try {
			Method m = listenerObject.getClass().getMethod(
					"webSocketConnectEvent", String.class, String.class);
			m.invoke(listenerObject, UID, IP);
		} catch (NoSuchMethodException e) {
			// ok to ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Removes client to the list of all active clients
	 * 
	 * @param socket The specific connection between client and server
	 */
	public void remove(WebsocketServerEvents socket) {
		String IP = getIP(socket);
		String UID = memberUIDs.get(socket);

		memberUIDs.remove(socket);
		memberSockets.remove(UID);

		try {
			Method m = listenerObject.getClass().getMethod(
					"webSocketDisconnectEvent", String.class, String.class);
			m.invoke(listenerObject, UID, IP);
		} catch (NoSuchMethodException e) {
			// ok to ignore
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Writes a message to all active clients
	 * 
	 * @param message String to send to all clients
	 */
	public void writeAllMembers(String message) {
		for (WebsocketServerEvents member : memberUIDs.keySet()) {
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
		for (WebsocketServerEvents member : memberUIDs.keySet()) {
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
	public void writeSpecificMember(String message, String uid) {
		WebsocketServerEvents member = memberSockets.get(uid);
		member.session.getRemote().sendStringByFuture(message);
	}

	/**
	 *
	 * Send message to one member
	 *
	 * @param data String to send to client
	 * @param uid id of the specific client
	 */
	public void writeSpecificMember(byte[] data, String uid) {
		WebsocketServerEvents member = memberSockets.get(uid);
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			member.session.getRemote().sendBytes(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Sends incoming message directly to the Processing sketch's websocket event function
	 * 
	 * @param message The message that has been received
	 */
	public void sendToOnMessageListener(String message){
		if (eventListener != null) {
		    try {
		    	eventListener.invoke(listenerObject, message);
		    } catch (Exception e) {
		    	System.err.println("Disabling webSocketEvent()");
		    	e.printStackTrace();
		    	eventListener = null;
		    }
		}
	}

	public void sendToOnBinaryListener(byte[] buf, int offset, int length){
		if (eventBinaryListener != null) {
		    try {
					eventBinaryListener
							.invoke(listenerObject, buf, offset, length);
		    } catch (Exception e) {
					System.err.println("Disabling webSocketEvent()");
					e.printStackTrace();
					eventBinaryListener = null;
		    }
		}
	}

	/**
	 * Generate
	 * @return
	 */
	private static String getUID() {
		return new BigInteger(130, random).toString(24);
	}

	private static String getIP(WebsocketServerEvents socket) {
		return socket.session.getRemoteAddress().getAddress().toString();
	}
}
