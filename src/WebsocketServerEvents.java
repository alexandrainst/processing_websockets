package websockets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebsocketServerEvents {
 
    public Session session;
    public WebsocketServerController ctrl;
    
    public WebsocketServerEvents(WebsocketServerController ctrl) {
		this.ctrl=ctrl;
	}
 
    @OnWebSocketConnect
    public void handleConnect(Session session) {
        this.session = session;
        ctrl.join(this);
    }
 
    @OnWebSocketClose
    public void handleClose(int statusCode, String reason) {
    	ctrl.remove(this);
    }
 
    @OnWebSocketMessage
    public void handleMessage(String message) {
    	ctrl.sendToOnMessageListener(message);
    }
 
    @OnWebSocketError
    public void handleError(Throwable error) {
        error.printStackTrace();    
    }
}