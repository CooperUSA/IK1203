import java.net.*;
import java.io.*;

public class TCPClient {
    boolean shutdown;
    Integer timeout;
    Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        if(toServerBytes == null){
            return askServer(hostname, port); 
        } 
        else{
            //Creates dynamic byte array for server response
            ByteArrayOutputStream fromServerBuffer = new ByteArrayOutputStream(); 

            //Create socket
            Socket clientSocket = new Socket(hostname, port);
            

            //Creates an output stream to the server()
            clientSocket.getOutputStream().write(toServerBytes);

            ///Check for shutdown, to close the output stream (It's already sent out the data)
            if(shutdown){
               clientSocket.shutdownOutput(); 
            }

            ///Enable a timer for timeout, such that a exception is raised if the timeout expires
            if(timeout != null){
                clientSocket.setSoTimeout(timeout);
            }

            ///Try and catch for timeout, will catch the exception too be able to return whatever we got from the input stream
            try{
                //Creates an input stream for the correct channel, then writes down the input from the server to the dynamic byte array.
                InputStream fromServer = clientSocket.getInputStream();
                int temp = fromServer.read();   
                int maxlimit = 0;                                               
                while(temp != -1){
                    ///If there's limit for how much data to recieve, it will break when we have received it
                    if(limit != null && maxlimit >= limit){
                        break;
                    }
                    maxlimit++;
                    fromServerBuffer.write(temp);               //Uses our ByteArrayOutputStream to write down the input we get, so that 
                    temp = fromServer.read();                       //we can later return the input as a byte array (we don't actually use the
                }                                                   //ByteArrayOutputStream now to send anything out, we did that prior to this) 


                //Closes the connection and returns the server response
                clientSocket.close();
                return fromServerBuffer.toByteArray();
            }
            catch(SocketTimeoutException exc){
                System.out.println("Timeout: " + exc);
                clientSocket.close();
                return fromServerBuffer.toByteArray();
            }
        }
    }

    //Does the same thing but without an output stream
    public byte[] askServer(String hostname, int port) throws IOException{
            ByteArrayOutputStream fromServerBuffer = new ByteArrayOutputStream(); 

            Socket clientSocket = new Socket(hostname, port);

            InputStream fromServer = clientSocket.getInputStream();
            int temp = fromServer.read();
            while(temp != -1){
                fromServerBuffer.write(temp);
                temp = fromServer.read();
            }

            clientSocket.close();
            return fromServerBuffer.toByteArray();
    } 
}
