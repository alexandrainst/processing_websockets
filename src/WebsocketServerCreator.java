package websockets;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

/**
 * 
 * @author Lasse Steenbock Vestergaard
 *
 * Class specific to the underlying websocket implementation (Jetty) 
 *
 */
public class WebsocketServerCreator implements WebSocketCreator{
    private WebsocketServerController ctrl;

    public WebsocketServerCreator(WebsocketServerController ctrl){
        this.ctrl = ctrl;
    }

    public Object createWebSocket(ServletUpgradeRequest request, ServletUpgradeResponse response){
        return new WebsocketServerEvents(ctrl);
    }
}