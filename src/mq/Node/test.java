package mq.Node;

import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        MyNode node1 = new MyNode(9999);
        MyNode node2 = new MyNode(8888);
        Message m1 = new Message("prepare",1,"The first Message");
        Message m2 = new Message("prepare",2,"The Second Message");
        Message m3 = new Message("t1t1t",1,"The first Message");
        Message m4 = new Message("udpto1",2,"The Second Message");
//        node1.rec();
//        node2.rec();
//        node1.sendByUdp(8888,m1);
//        node2.sendByUdp(9999,m2);
        node1.rec();
        node2.rec();
    //    node1.sendByTcp(8888,m1);
        node2.sendByTcp(9999,m2);
//        node1.sendByUdp(8888,m3);
        node2.sendByUdp(9999,m4);
        node1.test();
      //  node2.test();
    }
}
