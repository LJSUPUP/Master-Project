package mq.Node;

public class paxosSimulation {


    public static void main(String[] args) {
        NodeThread pro1 = new NodeThread(8888,1);
        NodeThread pro2 = new NodeThread(9999,2);
        NodeThread pro8 = new NodeThread(7777,8);

        pro1.start();
        pro2.start();
        pro8.start();

        AcceptorThread pro3 = new AcceptorThread(10100,3);
        AcceptorThread pro4 = new AcceptorThread(10200,4);
        AcceptorThread pro5 = new AcceptorThread(10300,5);
        AcceptorThread pro6 = new AcceptorThread(10400,6);
        AcceptorThread pro7 = new AcceptorThread(10500,7);

        pro3.start();
        pro4.start();
        pro5.start();
        pro6.start();
        pro7.start();

        pro1.addTarget(10100);
        pro1.addTarget(10200);
        pro1.addTarget(10300);
        pro1.addTarget(10400);
        pro1.addTarget(10500);
        pro2.addTarget(10100);
        pro2.addTarget(10200);
        pro2.addTarget(10300);
        pro2.addTarget(10400);
        pro8.addTarget(10500);
        pro8.addTarget(10100);
        pro8.addTarget(10200);
        pro8.addTarget(10300);
        pro8.addTarget(10400);
        pro8.addTarget(10500);

        Message m1 = new Message("message",1,"I love Cat" );
        Message m2 = new Message("message",2,"Cat loves me");
        Message m3 = new Message("message",3,"Cat is Catty");

        TcpSenThread tcpSenThread1 = new TcpSenThread("127.0.0.1", 8888,m1);
        TcpSenThread tcpSenThread2 = new TcpSenThread("127.0.0.1", 9999,m2);
        TcpSenThread tcpSenThread3 = new TcpSenThread("127.0.0.1", 7777,m3);
        tcpSenThread1.start();
        tcpSenThread2.start();
        tcpSenThread3.start();





    }
}
