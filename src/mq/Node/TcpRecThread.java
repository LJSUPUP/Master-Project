package mq.Node;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2017/5/28.
 */
/*
 * 服务器线程处理类
 */
public class TcpRecThread implements Callable<Message> {
    // 和本线程相关的Socket
    Socket socket = null;
    int portNum;
    Message m;

    public TcpRecThread(Socket socket, int portNum) {
        this.socket = socket;
        this.portNum = portNum;
    }

    //线程执行的操作，响应客户端的请求
    public Message getMessage(){
        return m;
    }
    public Message call() {
        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            inputStream = socket.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            objectInputStream = new ObjectInputStream(bufferedInputStream);
            Object object = objectInputStream.readObject();
            m = (Message)object;
            return m;
            //this.m=m;
            //System.out.println("client"+m.nodeId+"say"+m.getType());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            try {
                objectInputStream.close();
                bufferedInputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

