package mq.Node;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class NetworkLayer {
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    TcpThread tcpThread;
    UdpThread udpThread;
    int portNum;

    public Set<Integer> targets;

    public NetworkLayer(int portNum){
        this.portNum = portNum;
        targets = new HashSet<>();
        try {
            datagramSocket = new DatagramSocket(portNum);
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }
    public void init(){
        tcpThread = new TcpThread(serverSocket,portNum);
        udpThread = new UdpThread(datagramSocket,portNum);
        tcpThread.start();
        udpThread.start();
    }

    public void setTargets(Set<Integer> targets) {
        this.targets = targets;
    }

    public Set<Integer> getTarget() {
        return targets;
    }

    public void addTarget(int i){
        targets.add(i);
    }
    public void cancelTarget(int i){
        if(targets.contains(i))
            targets.remove(i);
    }
    public void sendByTargetsTcp(Message m){
        for(int i:targets){
            sendByTcp(i,m);
        }
    }

    public void sendByTargetsUdp(Message m) throws IOException {
        for(int i:targets){
            sendByUdp(i,m);
            //System.out.println(i+m.getType());
        }
    }

    public void sendByTcp(int i, Message m) {
        TcpSenThread tcpSenThread = new TcpSenThread("127.0.0.1", i,m);
        tcpSenThread.start();
    }
    public void sendByUdp(int i, Message m) throws IOException {
        UdpSenThread udpSenThread = new UdpSenThread("127.0.0.1", i,m);
        udpSenThread.start();
    }
}
