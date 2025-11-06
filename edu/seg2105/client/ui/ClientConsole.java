package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Laganière
 * @author François Bélanger
 * @author Paul Holden
 * @version July 2000
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  public static final int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  // keep login id (Ex.3)
  private final String loginId;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  // take loginId and pass to ChatClient
  public ClientConsole(String loginId, String host, int port) 
  {
    this.loginId = loginId;
    try 
    {
      client = new ChatClient(host, port, this, loginId);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection! Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {
      String message;

      while (true) 
      {
        message = fromConsole.nextLine();

        // command handler
        if (message.startsWith("#")) {
          handleCommand(message);
          continue;
        }

        client.handleMessageFromClientUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  // parse and execute client #commands
  private void handleCommand(String cmdLine) {
    String[] parts = cmdLine.trim().split("\\s+");
    String cmd = parts[0].toLowerCase();

    switch (cmd) {
      //quit program
      case "#quit":
        client.quit();
        break;

      // disconnect only
      case "#logoff":
        client.logoff();
        break;

      //set host when disconnect
      case "#sethost":
        if (parts.length < 2) { display("Usage: #sethost <host>"); break; }
        client.safeSetHost(parts[1]);
        break;

      //set port when disconnected
      case "#setport":
        if (parts.length < 2) { display("Usage: #setport <port>"); break; }
        try { client.safeSetPort(Integer.parseInt(parts[1])); }
        catch (NumberFormatException e) { display("Port must be a number"); }
        break;

      //connect
      case "#login":
        client.login();
        break;

      //show host
      case "#gethost":
        display("Current host: " + client.currentHost());
        break;

      // show port
      case "#getport":
        display("Current port: " + client.currentPort());
        break;

      default:
        display("Unknown command");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  // args: loginId (mandatory), host (optional), port (optional)
  public static void main(String[] args) 
  {
    String loginId;

    // exact test-case error on missing login id
    try {
      loginId = args[0];
      if (loginId == null || loginId.isEmpty()) throw new ArrayIndexOutOfBoundsException();
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("ERROR - No login ID specified.  Connection aborted.");
      System.exit(1);
      return;
    }

    String host;
    try { host = args[1]; }
    catch (ArrayIndexOutOfBoundsException e) { host = "localhost"; }

    int port = DEFAULT_PORT;
    try {
      if (args.length >= 3) port = Integer.parseInt(args[2]);
    } catch (NumberFormatException ex) {
      System.out.println("Invalid port. Using default " + DEFAULT_PORT + ".");
    }

    ClientConsole chat = new ClientConsole(loginId, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
