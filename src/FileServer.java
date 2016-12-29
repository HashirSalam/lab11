

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class FileServer {

    private DatagramSocket socket = null;
    private FileHandler fileHandle = null;

    public FileServer() {

    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(9876);
            byte[] incomingData = new byte[1024 * 1000 * 50];
            while (true) {
                //receive the file
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                byte[] data = incomingPacket.getData();
                System.out.println("RECEIVED");

                //Write data to a local file
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                fileHandle = (FileHandler) is.readObject();

                if (fileHandle.getStatus().equalsIgnoreCase("Error")) {
                    System.out.println("Some issue happened while packing the data @ client side");
                    System.exit(0);
                }

                createAndWriteFile(); // writing the file to hard disk
                
                //send confirmation
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                String reply = "Got the file";
                byte[] replyBytea = reply.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
                socket.send(replyPacket);
                

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createAndWriteFile() {
        String outputFile = fileHandle.getDestinationDirectory() + fileHandle.getFilename();
        if (!new File(fileHandle.getDestinationDirectory()).exists()) {
            new File(fileHandle.getDestinationDirectory()).mkdirs();
        }
        File dstFile = new File(outputFile);
        FileOutputStream fileOutputStream = null;
        /////////////////////////////////////////////////////////////
        //save the file
        try {
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileHandle.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
///////////////////////////////////////////////////////////////
            
            
            

        

    }

    public static void main(String[] args) {
        FileServer server = new FileServer();
        server.createAndListenSocket();
    }
}
