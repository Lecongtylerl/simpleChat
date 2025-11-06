package edu.seg2105.edu.server.backend;

import java.io.*;  // BufferedReader, InputStreamReader, IOException

public class ServerConsole {

  // server instance
  private final EchoServer server;
  // read stdin
  private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

  // construct server on given port
  public ServerConsole(int port) {
    server = new EchoServer(port);
  }

  //read admin input and handle commands
  public void accept() {
    try {
      String line;
      while ((line = in.readLine()) != null) {

        // broadcast plain text with exact prefix for tests
        if (!line.startsWith("#")) {
          server.sendToAllClients("SERVER MESSAGE> " + line);
          System.out.println("SERVER MESSAGE> " + line);
          continue;
        }

        // parse command
        String[] parts = line.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
          case "#quit": //exit program
            try { server.close(); } catch (IOException ignore) {}
            System.out.println("Server quitting");
            System.exit(0);
            break;

          case "#stop": //stop listening
            try { server.stopListening(); System.out.println("Server stopped listening"); }
            catch (Exception e) { System.out.println("Stop failed: " + e.getMessage()); }
            break;

          case "#close": //close and drop clients
            try { server.close(); }
            catch (Exception e) { System.out.println("Close failed: " + e.getMessage()); }
            break;

          case "#setport": //change port only when closed
            if (parts.length < 2) { System.out.println("Usage: #setport <port>"); break; }
            if (server.isListening()) {
              System.out.println("Cannot set port while server is open. Use #stop or #close first.");
              break;
            }
            try {
              int newPort = Integer.parseInt(parts[1]);
              server.setPort(newPort);
              System.out.println("Port set to " + newPort);
            } catch (NumberFormatException e) {
              System.out.println("Port must be a number");
            }
            break;

          //start listening
          case "#start": 
            try { server.listen(); } 
            catch (IOException e) { System.out.println("Start failed: " + e.getMessage()); }
            break;

         // show port
          case "#getport":
            System.out.println("Port: " + server.getPort());
            break;

          default:
            System.out.println("Unknown command");
        }
      }
    } catch (IOException e) {
      System.out.println("Console I/O error: " + e.getMessage());
    }
  }

  // entry point
  public static void main(String[] args) {
    int port = EchoServer.DEFAULT_PORT; //default
    try { if (args.length > 0) port = Integer.parseInt(args[0]); } catch (NumberFormatException ignore) {}

    ServerConsole console = new ServerConsole(port);
    try { console.server.listen(); } 
    catch (IOException e) { System.out.println("ERROR - Could not listen for clients!"); }

    console.accept();
  }
}
