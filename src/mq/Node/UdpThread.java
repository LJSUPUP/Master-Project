package mq.Node;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpThread extends Thread {
    ExecutorService executorService= Executors.newCachedThreadPool();

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    LinkedList<Message> buffer = new LinkedList<Message>();
    LinkedList<Message> learnBuf = new LinkedList<>();
    private int portNum;
    byte[] data = null;
    int count = 0;


    public LinkedList<Message> getBuffer() {
        return buffer;
    }
    public void addBuffer(Message m) {
        buffer.offer(m);
    }

    public LinkedList<Message> getLearnBuf() {
        return learnBuf;
    }

    public UdpThread(DatagramSocket socket, int portNum) {
        this.socket = socket;
        this.portNum = portNum;
    }
    public Message getMessage(){
        if(buffer.isEmpty()) return null;
        return buffer.poll();
    }

    public Message getLearnMessage(){
        if(learnBuf.isEmpty()) return null;
        return learnBuf.poll();
    }
    public void run() {
        while (true) {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message m = null;
            byte[] data = packet.getData();
            try {

                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
                Object object = ois.readObject();
                m = (Message)object;
                ois.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

//            Message mx = null;
//            while(mx==null){
//                if(future.isDone()) {
//                    try {
//                        mx = (Message) future.get();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            if(m.getType().equals("learn")||m.getType().equals("learnOK")||m.getType().equals("learnRej")){
                learnBuf.offer(m);

            }
            else
                buffer.offer(m);

            //System.out.println(mx.getType());
           // count++;
//            System.out.println("服务器端被连接过的次数：" + count);
//            InetAddress address = packet.getAddress();
//            System.out.println("当前客户端的IP为：" + address.getHostAddress());

        }
    }
}

// 状态位置
// 主题函数
// 开启监听
// 如果是message存入list
//
