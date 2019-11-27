# Websockets for Processing

**Create websocket servers and clients, which makes it possible to communicate with the
outside world including web sites. With this library it is possible to have true two-way
real-time connections with other Processing sketches, web sites, Internet of Things
devices, etc.**

## Updates
This is the initial version of the library, and it is therefore an update in it self :-)

## Download
The library can be downloaded here:
https://github.com/alexandrainst/processing_websockets/blob/master/webSockets.zip?raw=true

## Installation
Unzip and put the extracted webSockets folder into the libraries folder of your Processing
sketches. Reference and examples are included in the webSockets folder.

## Examples explained
I have provided two simple examples on using both the client and server part. These can be
found in the examples folder. Below I will go through each example, and elaborate their usage.

### Websocket client

In the following I provide the full example code of creating a websocket client in Processing.
In the below code I draw a new ellipse at a random location (without removing the previous)
each time I get a message from the websocket server, and I send a message to the server every
5 seconds ("Client message").

```
import websockets.*;

WebsocketClient wsc;
int now;
boolean newEllipse;

void setup(){
  size(200,200);
  
  newEllipse=true;
  
  //Here I initiate the websocket connection by connecting to "ws://localhost:8025/john", which is the uri of the server.
  //this refers to the Processing sketch it self (you should always write "this").
  wsc= new WebsocketClient(this, "ws://localhost:8025/john");
  now=millis();
}

void draw(){
    //Here I draw a new ellipse if newEllipse is true
  if(newEllipse){
    ellipse(random(width),random(height),10,10);
    newEllipse=false;
  }
    
    //Every 5 seconds I send a message to the server through the sendMessage method
  if(millis()>now+5000){
    wsc.sendMessage("Client message");
    now=millis();
  }
}

//This is an event like onMouseClicked. If you chose to use it, it will be executed whenever the server sends a message 
void webSocketEvent(String msg){
 println(msg);
 newEllipse=true;
}
```

### Websocket server

In the following I provide the full example code of creating a websocket server in Processing.
In the below code I move an ellipse to a random location when I get a message from a client,
and I send a message to ll clients every 5 seconds ("Server message").

```
import websockets.*;

WebsocketServer ws;
int now;
float x,y;

void setup(){
  size(200,200);
  
  //Initiates the websocket server, and listens for incoming connections on ws://localhost:8025/john
  ws= new WebsocketServer(this,8025,"/john");
  now=millis();
  x=0;
  y=0;
}

void draw(){
  background(0);
  ellipse(x,y,10,10);
  
  //Send message to all clients very 5 seconds
  if(millis()>now+5000){
    ws.sendMessage("Server message");
    now=millis();
  }
}

//This is an event like onMouseClicked. If you chose to use it, it will be executed whenever a client sends a message
void webSocketServerEvent(String msg){
 println(msg);
 x=random(width);
 y=random(height);
}
```

### Set message max size

Call this method before calling the constructor to specify the message max size in bytes

    WebsocketServer.setMaxMessageSize(200000);

### Enable logging

By default logging has been disabled, as it looks like errors in the
Processing IDE's console. Call this method before calling the constructor

    WebsocketServer.enableDebug();

### How to be notified of user connections / disconnections

Implement the following two methods, which will receive the user id hash and
the user IP address.

```
public void webSocketConnectEvent(String uid, String ip) {
  println("Someone connected", uid, ip);
}
  
public void webSocketDisconnectEvent(String uid, String ip) {
  println("Someone disconnected", uid, ip);
}
```

### How to send a message to a specific user

You need the user ID to send a message to that user only. 
You receive that user ID when the user first connects.

    ws.sendMessageTo("message", "userID");

or

    ws.sendMessageTo(byteArray, "userID");

### How to instantiate the WebsocketServer in your own class instead of the PApplet

Note the extra second argument in the constructor. That's the Object that will implement all the webSocketXXX() methods. Without that second argument it is assumed that the PApplet will implement such methods.

```
public class CaptainSocket {
  WebsocketServer ws;
  CaptainSocket(PApplet p5) {
    ws = new WebsocketServer(p5, this, 8025, "/miau");
  }
  public void webSocketServerEvent(String msg) {
    println(msg);
  }
}  

... in your PApplet ...

CaptainSocket  socket;
void setup() {
  socket = new CaptainSocket(this);
}

```


## Technical development details

The library has been developed on a Mac with El Capitan, I have used the Eclipse Luna IDE,
and I have only tested on Processing version 3.0.1.

The library is build with the Jetty websocket implementation, and different Jetty libraries
are therefore needed for running this library. All dependencies are included in the downloadable
zip file. The source code is available through this Github project (open source under MIT
license) as well as included in the zip file below.
