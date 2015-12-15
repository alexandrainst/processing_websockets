package websockets;

import java.lang.reflect.Method;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import processing.core.PApplet;

/**
 * 
 * @author Lasse Steenbock Vestergaard
 *
 */
public class WebsocketServer {
	private PApplet parent;
	private Method websocketServerEvent;
	private WebsocketServerController serverController;
	
	/**
	 * 
	 * The websocket server object that is initiated directly in the Processing sketch
	 * 
	 * @param parent Processings PApplet object
	 * @param port The port number you want the websocket server to initiate its connection on
	 * @param uri The uri you want your server to respond to. Ex. /john (if the port is set to ex. 8025, then the full URI would be ws://localhost:8025/john).
	 */
	public WebsocketServer(PApplet parent, int port, String uri){
		this.parent=parent;
		this.parent.registerMethod("dispose", this);
		
		try {
        	websocketServerEvent = parent.getClass().getMethod("webSocketServerEvent", String.class);
        } catch (Exception e) {
        	// no such method, or an error.. which is fine, just ignore
        }
		
		Server server = new Server(port);
		serverController = new WebsocketServerController(parent, websocketServerEvent);
		 
		WebSocketHandler wsHandler = new WebSocketHandler(){
			
			 	@Override
			    public void configure(WebSocketServletFactory factory){
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
	 * This method is used for sending messages to all connected clients. This method will be updated with the possibility for sending messages to specific clients!
	 * 
	 * @param message The message you want to send to all clients
	 */
	//TODO: Add possibility for sending back to specific client
	public void sendMessage(String message){
		serverController.writeAllMembers(message);
		
	}
	
	public void dispose(){
		// Anything in here will be called automatically when 
	    // the parent sketch shuts down. For instance, this might
	    // shut down a thread used by this library.
	}
}
