package mq.Node;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Node extends Thread{

    private NetworkLayer io;


    private Acceptor acceptor;
    private Proposer proposer;
    private Learner learner;
    public Set<Integer> targets;
    public List<String> proposalList;
    private int roundId = 0;
    private int nodeId;
    private int portNum = 0;
    private boolean needLearning;


    public NetworkLayer getIo() {
        return io;
    }

    public Proposer getProposer() {
        return proposer;
    }

    public Node(int nodeId, int portNum){

        this.nodeId = nodeId;
        this.portNum = portNum;
        this.io = new NetworkLayer(this.portNum);
        this.proposalList = new LinkedList<>();
        this.init();
        targets = new HashSet<>();
    }

    private void init(){
        this.acceptor = new Acceptor(nodeId,io);
        this.proposer = new Proposer(nodeId,io);
        this.learner = new Learner(nodeId,io);
        proposer.setAcceptor(acceptor);
        needLearning = false;
    }

    public void run(){
        acceptor.start();
        while(true){

            Message learnMsg = null;

            Message tmp = null;
            int max = roundId;
            while(io.udpThread.getLearnBuf().size()!=0){
                    learnMsg = io.udpThread.getLearnMessage();
                    if(max<learnMsg.getRoundId()){
                        max = learnMsg.getRoundId();
                        tmp = learnMsg;
                    }
            }
            acceptor.learn(tmp);

            Message newMsg = io.tcpThread.getMsg();

            if(newMsg!=null){
                proposalList.add(newMsg.getContent());
            }

            if(proposalList.size()==0)
                continue;

            String val = proposalList.remove(0);

            proposer.reset(roundId);

            int res = -1;

            try {
                res = proposer.newRound(val);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(res==-1||!acceptor.getChosenVal().get(roundId).equals(val)){
                proposalList.add(val);
            }


            roundId = acceptor.getChosenVal().size();
            //System.out.println();

            //learner.bcLearn(roundId);

            //存进去

        }
    }

}
