package websockets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class WebsocketServerController {

	private List<WebsocketServerEvents> members = new ArrayList<>();
	private PApplet parent;
	private Method serverEvent;
	
	public WebsocketServerController(PApplet p, Method serverEvent){
		parent=p;
		this.serverEvent=serverEvent;
	}

	public void join(WebsocketServerEvents socket) {
		members.add(socket);
	}

	public void remove(WebsocketServerEvents socket) {
		members.remove(socket);
	}

	public void writeAllMembers(String message) {
		for (WebsocketServerEvents member : members) {
			member.session.getRemote().sendStringByFuture(message);
		}
	}

	public void writeSpecificMember(String memberName, String message) {
		WebsocketServerEvents member = findMemberByName(memberName);
		member.session.getRemote().sendStringByFuture(message);
	}

	public WebsocketServerEvents findMemberByName(String memberName) {
		return null;
	}
	
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
