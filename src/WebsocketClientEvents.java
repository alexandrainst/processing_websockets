package websockets;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import processing.core.PApplet;

@WebSocket
public class WebsocketClientEvents {
	private Session session;
	CountDownLatch latch = new CountDownLatch(1);
	private PApplet parent;
	private Method onMessageEvent;

	public WebsocketClientEvents(PApplet p, Method event) {
		parent = p;
		onMessageEvent = event;
	}

	@OnWebSocketMessage
	public void onText(Session session, String message) throws IOException {
		if (onMessageEvent != null) {
			try {
				onMessageEvent.invoke(parent, message);
			} catch (Exception e) {
				System.err
						.println("Disabling webSocketEvent() because of an error.");
				e.printStackTrace();
				onMessageEvent = null;
			}
		}
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		latch.countDown();
	}

	public void sendMessage(String str) {
		try {
			session.getRemote().sendString(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnWebSocketError
	public void onError(Throwable cause) {
		System.out.printf("onError(%s: %s)%n",cause.getClass().getSimpleName(), cause.getMessage());
		cause.printStackTrace(System.out);
	}

	public CountDownLatch getLatch() {
		return latch;
	}
}
