package mq.Node;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TcpSenThread extends Thread{
    Socket socket = null;
    Message m;
    OutputStream outputStream = null;
    BufferedOutputStream bufferedOutputStream = null;
    ObjectOutputStream objectOutputStream = null;
    public TcpSenThread(String adr, int portNum, Message m) {
        this.m = m;
        try {
            this.socket = new Socket(adr, portNum );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {

            if(socket==null) return;
            outputStream = socket.getOutputStream();
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(m);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                bufferedOutputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
