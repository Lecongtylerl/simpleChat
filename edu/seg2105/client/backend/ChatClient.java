// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Laganière
 * @author François Bélanger
 * @author Paul Holden
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  // keep login id (Ex.3)
  private String loginId;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  //add loginId and auto-login
  public ChatClient(String host, int port, ChatIF clientUI, String loginId) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    // store login id
    this.loginId = loginId;
    openConnection();
    // send #login automatically
    sendToServer("#login " + loginId);
  }

  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  //exact test output on server shutdown
  @Override
  protected void connectionClosed() {
    System.out.println("The server has shut down.");
    System.exit(0);
  }

  //exact test output on server exception
  @Override
  protected void connectionException(Exception exception) {
    System.out.println("The server has shut down.");
    System.exit(0);
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  // disconnect without exiting
  public void logoff() {
    try {
      if (isConnected()) closeConnection();
      // exact test wording
      clientUI.display("Connection closed.");
    } catch (IOException e) {
      clientUI.display("Error while logging off");
    }
  }

  //set host when disconnect
  public void safeSetHost(String host) {
    if (isConnected()) {
      clientUI.display("Cannot set host while connected");
      return;
    }
    setHost(host);
    clientUI.display("Host set to " + host);
  }

  //set port when disconnect
  public void safeSetPort(int port) {
    if (isConnected()) {
      clientUI.display("Cannot set port while connected");
      return;
    }
    setPort(port);
    clientUI.display("Port set to " + port);
  }

  // connect and auto-login
  public void login() {
    try {
      if (isConnected()) {
        clientUI.display("Already connected");
        return;
      }
      openConnection();
      // re-send #login after reconnect
      sendToServer("#login " + loginId);
      clientUI.display("Login successful");
    } catch (IOException e) {
      clientUI.display("Login failed");
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try { closeConnection(); } catch(IOException e) {}
    System.exit(0);
  }

  // expose host/port to UI without overriding OCSF finals
  public String currentHost() { return super.getHost(); }
  public int currentPort() { return super.getPort(); }
}
//End of ChatClient class
