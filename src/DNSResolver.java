import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;


/**
 * @author Douglas Money
 */
class DNSResolver{

    public static void main(String args[]) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.print("Enter port number: ");
        int port = in.nextInt(); //user sets Port
        DatagramSocket serverSocket = new DatagramSocket(port); //listens for incoming packets not connections
        while(true){
            byte[] receiveData = new byte[512];//byte array to store bytes into
            DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
            serverSocket.receive(receivePacket);
            serverSocket.send(receivePacket);

            byte[] recvData = new byte[512];
            DatagramPacket recvPacket = new DatagramPacket(recvData,recvData.length);
            serverSocket.receive(recvPacket);
            DNSPacket responsepacket = new DNSPacket(recvData);

            if(responsepacket.getRCode()!=0){
                System.out.println("Got an error, rcode is "+responsepacket.getRCode());
                System.exit(0);
            }
            if(responsepacket.getAnswerCount()==0){
                System.out.println("We got no answers, must be some problem");
                System.exit(0);
            }

            DNSQuestion responsequestion = responsepacket.getQuestion();
            System.out.println("Query: "+responsequestion.getQName() + "\t"
                    + responsequestion.getDnsType()
                    + "\t" + responsequestion.getDnsClass());

            //String message = new String(receiveData); //byteArray to string
            //System.out.println("Got message: \"" +message+ "\" from client: "+ receivePacket.getAddress());
            //sendToClient(serverSocket,message,receivePacket.getAddress(),receivePacket.getPort());
            //System.out.println("Sent Packet");

        }
    }

    public static void sendToClient(DatagramSocket serverSocket,String message,InetAddress IP, int port) throws Exception{
        byte[] sendData = message.getBytes(); //convert string into byte array
        DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,IP,port);
        serverSocket.send(sendPacket);
    }
}
