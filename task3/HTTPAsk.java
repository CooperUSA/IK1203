import java.net.*;
import java.io.*;

public class HTTPAsk {
    public static void main( String[] args) throws IOException{

        // Create a server socket to get HTTP requests
        int argindex = 0;
        int HTTPPort = Integer.parseInt(args[argindex]);
        ServerSocket serverSocket = new ServerSocket(HTTPPort);       

        // Socket to connect
        Socket socket = null;

        //HTTP status codes
        StringBuilder ok = new StringBuilder("HTTP/1.1 200 OK\r\n\r\n");
        byte[] notFound = "HTTP/1.1 404 Not Found\r\n\r\n".getBytes();
        byte[] badRequest = "HTTP/1.1 400 Bad Request\r\n\r\n".getBytes();

        while(true){
            // variables for get request
            String hostname = null;
            Integer port = null;
            String string = null;
            boolean shutdown = false;
            Integer limit = null;
            int timeout = 0;

            // Extra variables for get request
            byte[] fromClient = new byte[0];
            boolean version = false;

            try{
                System.out.println("Server waiting for request");

                // Create a connection socket
                socket = serverSocket.accept();
                System.out.println("Connected");

                // Input and output stream for the socket
                InputStream in = socket.getInputStream();
                OutputStream out = new DataOutputStream(socket.getOutputStream());

                // Dynamic Byte array to recieve HTTP request, then creates string from it.
                ByteArrayOutputStream clientData = new ByteArrayOutputStream();
                int temp = in.read();
                while(true){
                    clientData.write(temp);
                    if(clientData.toString().endsWith("\r\n\r\n")){
                        break;
                    }
                    temp = in.read();
                }
                String url = clientData.toString();
                System.out.println(url);

                // Split the url into parts and print out the array
                String[] params = url.split("[?\\&\\=\\\r\n\\ ]");
                for (String a : params) {
                    System.out.println("'" + a + "'");
                }
                // Split the HTTP request and assign parameters
                if(params[0].equals("GET") && params[1].equals("/ask")){
                    for(int i = 0; i < params.length; i++){
                        if(params[i].equals("hostname"))
                        hostname = params[++i];

                        else if(params[i].equals("port"))
                            port = Integer.parseInt(params[++i]);
                        
                        else if(params[i].equals("string"))
                            string = params[++i];
                            
                        else if(params[i].equals("shutdown"))
                            shutdown = Boolean.parseBoolean(params[++i]);
    
                        else if(params[i].equals("limit"))
                            limit = Integer.parseInt(params[++i]);

                        else if(params[i].equals("timeout"))
                            timeout = Integer.parseInt(params[++i]);
                        
                        else if(params[i].equals("HTTP/1.1"))
                            version = true;
                    }
                }
                // If the client sends any data
                if(string != null){
                    fromClient = string.getBytes();
                }

                // Call the TCPClient and then forward the data to the Socket Client
                if(params[1].equals("/ask") && hostname != null && port != null && version){
                    try{
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        byte[] toClientBytes = tcpClient.askServer(hostname, port, fromClient);
                        String data = new String(toClientBytes);
                        ok.append(data);
                        out.write(ok.toString().getBytes());
                        System.out.println("ok");
                        //ok
                    }
                    catch(Exception e){
                        out.write(notFound);
                        System.out.println("Not Found");
                    }
                } 
                else{
                    out.write(badRequest);
                    System.out.println("Bad Request");
                }
                System.out.println("--------REQUEST DONE---------");
                socket.close();
            }
            catch (Exception ex) {
                System.err.println("SHIT SUCKS");
                System.exit(1);
            }
        }
    }
}

