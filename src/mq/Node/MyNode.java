package mq.Node;
import java.io.*;
import java.net.*;
import java.util.*;

public class MyNode {
    private ServerSocket serverSocket = null;
    private DatagramSocket datagramSocket = null;
    public Set<Integer> targets;
    TcpThread tcpThread;
    UdpThread udpThread;
    private int portNum = 0;


    public int getPortNum(){
        return this.portNum;
    }
    public MyNode(int portNum){
        this.portNum = portNum;
        targets = new HashSet<>();
        try {
            datagramSocket = new DatagramSocket(portNum);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void rec() {
        recByUdp();
        recByTcp();

    }
    public void recByTcp() {
        tcpThread = new TcpThread(serverSocket, portNum);
        ///System.out.println(portNum);
        tcpThread.start();

    }
    public void recByUdp() {
        udpThread = new UdpThread(datagramSocket, portNum);
        //System.out.println(portNum);
        udpThread.start();

    }
    public void sendByTargetsTcp(Message m){
        for(int i:targets){
            sendByTcp(i,m);
        }
    }

    public void sendByTargetsUdp(Message m) throws IOException {
        for(int i:targets){
            sendByUdp(i,m);
        }
    }

    public void sendByTcp(int i, Message m) {
        TcpSenThread tcpSenThread = new TcpSenThread("127.0.0.1", i,m);
        tcpSenThread.start();
    }
    public void sendByUdp(int i, Message m) throws IOException {
        UdpSenThread udpThread = new UdpSenThread("127.0.0.1", i,m);
        udpThread.start();

    }
    public void test(){
        while(true){
            String mmm = tcpThread.getValue();
            Message sss = udpThread.getMessage();
            if(mmm!=null)System.out.println(mmm);
            if(sss!=null)System.out.println(sss.getType());
        }
    }
}
