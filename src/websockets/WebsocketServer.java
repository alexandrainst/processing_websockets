package websockets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import processing.core.PApplet;

/**
 *
 * @author Lasse Steenbock Vestergaard
 * @author Abe Pazos (changes)
 *
 */
public class WebsocketServer {
	private WebsocketServerController serverController;
	private Server server;

	private static int MAX_MSG_SIZE = 65536;
	private static boolean DEBUG = false;

	/**
	 *
	 * The websocket server object that is initiated directly in the Processing sketch
	 *
	 * @param parent Processing's PApplet object
	 * @param port The port number you want the websocket server to initiate its connection on
	 * @param uri The uri you want your server to respond to. Ex. /john (if the port is set to ex. 8025, then the full URI would be ws://localhost:8025/john).
	 */
	public WebsocketServer(PApplet parent, int port, String uri){
		this(parent, parent, port, uri);
	}

	/**
	 *
	 * @param parent Processing's PApplet object
	 * @param listenerObject The object implementing .webSocketServerEvent()
	 * @param port The port number you want the websocket server to initiate its connection on
	 * @param uri The uri you want your server to respond to. Ex. /john (if the port is set to ex. 8025, then the full URI would be ws://localhost:8025/john).
	 */
	public WebsocketServer(PApplet parent, Object listenerObject, int port,
						   String uri) {

		parent.registerMethod("dispose", this);

		// Logging is disabled by default because it looks like errors
		// in the Processing IDE (five red INFO messages on start)
		if(!DEBUG) {
			org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
		}

		server = new Server(port);
		serverController = new WebsocketServerController(listenerObject);

		WebSocketHandler wsHandler = new WebSocketHandler() {

			@Override
			public void configure(WebSocketServletFactory factory){
				// Set max message sizes. Set by calling the static
				// method setMaxMessageSize(bytes) before the constructor.
				factory.getPolicy().setMaxTextMessageSize(MAX_MSG_SIZE);
				factory.getPolicy().setMaxBinaryMessageSize(MAX_MSG_SIZE);

				factory.setCreator(new WebsocketServerCreator(serverController));
			}
		};

		ContextHandler contextHandler = new ContextHandler(uri);
		contextHandler.setAllowNullPathInfo(true); // disable redirect from /ws to /ws/
		contextHandler.setHandler(wsHandler);

		server.setHandler(contextHandler);

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Send String message to all connected clients
	 *
	 * @param message The message content as a String
	 */
	public void sendMessage(String message) {
		serverController.writeAllMembers(message);
	}

	/**
	 *
	 * Send byte[] message to all connected clients
	 *
	 * @param data The message content as a byte[]
	 */
	public void sendMessage(byte[] data) {
		serverController.writeAllMembers(data);
	}

	/**
	 *
	 * Send String message to one receiver
	 *
	 * @param message The message content as a String
	 * @param to Receiver userId of this message
	 */
	public void sendMessageTo(String message, String to) {
		serverController.writeSpecificMember(message, to);
	}

	/**
	 *
	 * Send byte[] message to one receiver
	 *
	 * @param data The message content as a byte[]
	 * @param to Receiver userId of this message
	 */
	public void sendMessageTo(byte[] data, String to) {
		serverController.writeSpecificMember(data, to);
	}

	/**
	 * Set the max message size in bytes, 64Kb by default
	 * @param bytes
	 */
	public static void setMaxMessageSize(int bytes) {
		MAX_MSG_SIZE = bytes;
	}

	/**
	 * Enable logging, disabled by default because it looks like errors
	 * in the Processing IDE
	 */
	public static void enableDebug() {
		DEBUG = true;
	}

	/**
	 *
	 */
	public void dispose(){
		// Anything in here will be called automatically when
	    // the parent sketch shuts down. For instance, this might
	    // shut down a thread used by this library.
		try {
			System.out.println("Closing websockets...");
			server.stop();
			System.out.println("...websockets closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
