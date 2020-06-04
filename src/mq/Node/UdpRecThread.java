package mq.Node;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class UdpRecThread implements Callable<Message> {

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    int portNum = 0;
    Message m=null;


    public UdpRecThread(DatagramSocket socket, DatagramPacket packet, int portNum) {
        this.socket = socket;
        this.packet = packet;
        this.portNum = portNum;
    }

    @Override
    public Message call() {
        String info = null;
        InetAddress address = null;
        byte[] data = packet.getData();
//        byte[] data2 = null;
        DatagramPacket packet2 = null;
        try {

            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
            Object object = ois.readObject();
            m = (Message)object;
            //System.out.println(m.getType());
            ois.close();
            return new Message(m);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //socket.close();//不能关闭
        return null;
    }


}
