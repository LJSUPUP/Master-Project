package mq.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/*
 * 基于TCP协议的Socket通信，实现用户登陆
 * 服务器端
 */
public class TcpThread extends Thread{
    ExecutorService executorService= Executors.newCachedThreadPool();



    ServerSocket serverSocket = null;
    private int portNum;
    List<Message> list = new ArrayList<>();


    public TcpThread(ServerSocket serverSocket, int portNum) {
        this.serverSocket = serverSocket;
        this.portNum = portNum;
    }

    public String getValue(){
        //System.out.println(1213);
        if(list.isEmpty()) return null;
        Message m = list.get(0);
        list.remove(0);
       // System.out.println(123);
        return m.getContent();
    }

    public Message getMsg(){
        //System.out.println(1213);
        if(list.isEmpty()) return null;
        Message m = list.get(0);
        list.remove(0);
        // System.out.println(123);
        return m;
    }

    public void run() {
        try {
            int count=0;
            System.out.println("***Tcp服务器即将启动，等待客户端的连接***");
            Socket socket=null;

            //记录客户端的数量

            //循环监听等待客户端的连接
            while(true){
                //2、调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();

                //创建一个新的线程
                Callable<Message> callable=new TcpRecThread(socket,portNum);
                Future future=executorService.submit(callable);
//                TcpRecThread MyThread2 =new TcpRecThread();
//                //启动线程
//                tcpRecThread.start();
                Message mx = null;
                while(mx==null){
                    if(future.isDone())
                    mx = (Message) future.get();
                    //System.out.println(989);
                }
                list.add(mx);
                //System.out.println(mx.getType());
                count++;//统计客户端的数量
                System.out.println("客户端的数量："+count);
                InetAddress address=socket.getInetAddress();
                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}