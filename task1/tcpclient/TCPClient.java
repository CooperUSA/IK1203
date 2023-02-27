package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient{

    public TCPClient(){ 
    }

    //For fixed byte array
        //private static int BUFFERSIZE=1024;

    public byte[] askServer(String hostname, int port, byte[] bytesToServer) throws IOException{
        if(bytesToServer == null){
            return askServer(hostname, port); 
        } 
        else{
            //Creates fixed byte array for server response
                //byte[] fromServer = new byte[BUFFERSIZE];

            //Creates better dynamic byte array for server response
            ByteArrayOutputStream fromServerBuffer = new ByteArrayOutputStream(); 

            //Create socket
            Socket clientSocket = new Socket(hostname, port);

            //Creates an output stream to the server()
            clientSocket.getOutputStream().write(bytesToServer);

            //Creates an input stream for the correct channel, then writes down the input from the server to the dynamic byte array.
            InputStream fromServer = clientSocket.getInputStream();
            int temp = fromServer.read();
            while(temp != -1){
                fromServerBuffer.write(temp);
                temp = fromServer.read();
            }

            //Closes the connection and returns the server response
            clientSocket.close();
            return fromServerBuffer.toByteArray();
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
