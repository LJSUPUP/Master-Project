package mq.Node;

import java.io.*;
import java.net.*;

/**
 * Created by Administrator on 2017/5/28.
 */
public class UdpSenThread extends Thread {
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    Message m;
    InetAddress address = null;
    int portNum = 0;




    public UdpSenThread(String adr, int portNum, Message m) throws IOException {
        this.m = m;
        this.portNum = portNum;

        try {
            this.address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(m);
            byte[] data = baos.toByteArray();
            baos.close();
            oos.close();

            packet = new DatagramPacket(data, data.length, address, portNum);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void run() {
        try {
            if(socket!=null) socket.send(packet);
            //System.out.println(portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //接收服务器响应数据
//        byte[] data2 = new byte[1024];
//
//        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
//        try {
//            socket.receive(packet2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        data2 = packet2.getData();
//        ObjectInputStream ois = null;
//        try {
//            ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(data2)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Object object = null;
//        try {
//            object = ois.readObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        Message m2 = (Message)object;
//
//        System.out.println("我是客户端，服务器说：" +m2.getType());
          socket.close();
    }

}

