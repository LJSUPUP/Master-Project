package mq.Node;

import java.io.IOException;

public class Learner extends Thread{
    private int nodeId;
    private int portNum;
    private NetworkLayer io;


    public Learner(int nodeId, NetworkLayer io){
        this.io = io;
        this.portNum = io.portNum;
        this.nodeId = nodeId;
    }

    public void bcLearn(int roundId){
        Message m = new Message("needLearning",nodeId);
        m.setPortNum(portNum);
        m.setRoundId(roundId);
        try {
            io.sendByTargetsUdp(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
