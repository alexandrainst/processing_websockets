package websockets;

import java.lang.reflect.Method;
import java.net.URI;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import processing.core.PApplet;

public class WebsocketClient {
	private Method webSocketEvent;
	private WebsocketClientEvents socket;
	
	public WebsocketClient(PApplet parent, String endpointURI) {
		parent.registerMethod("dispose", this);
		
		try {
        	webSocketEvent = parent.getClass().getMethod("webSocketEvent", String.class);
        } catch (Exception e) {
        	// no such method, or an error.. which is fine, just ignore
        }
		
		WebSocketClient client = new WebSocketClient();
		try {
			socket = new WebsocketClientEvents(parent, webSocketEvent);
			client.start();
			URI echoUri = new URI(endpointURI);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			socket.getLatch().await();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void sendMessage(String message){
		socket.sendMessage(message);
	}
	
	public void dispose(){
		// Anything in here will be called automatically when 
	    // the parent sketch shuts down. For instance, this might
	    // shut down a thread used by this library.
	}
}
