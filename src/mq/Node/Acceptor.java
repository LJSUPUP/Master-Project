package mq.Node;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Acceptor extends Thread{

    private NetworkLayer io;
    private List<String> chosenVal;
    private int roundId = 0;
    private int nodeId;
    private int portNum;
    private int accProposal = -1;
    private String accVal = null;



    public Acceptor(int nodeId, NetworkLayer io){
        this.io = io;
        this.portNum = io.portNum;
        this.nodeId = nodeId;
        chosenVal = new LinkedList<>();

    }
    public  void reset(){
        accProposal = -1;
        accVal = null;
    }

    public void setRoundId(int roundId){
        this.roundId = roundId;
    }


    void replyPre(Message m) throws IOException {
        String type = null;
        int target = m.getPortNum();
        int nodeId = this.nodeId;
        Message res = new Message(type, nodeId);
        res.setRoundId(roundId);
        res.setProposalId(accProposal);
        res.setPortNum(this.portNum);
        if(m.getProposalId()>accProposal){
            res.setType("preOK");

            if(accProposal>=0){
                res.setContent(accVal);
            }
            this.accProposal = m.getProposalId();
            System.out.println("ACC:"+nodeId+":"+"preOK to"+"proposer" + m.getPortNum());
            //this.accVal = m.getContent();
        }
        else{
            res.setType("refusePre");
            System.out.println("ACC:"+nodeId+":"+"refusePre to"+"proposer" + m.getPortNum());
        }
        //System.out.println("now");
        io.sendByUdp(target,res);
        //System.out.println("now2");
    }

    void replyAcc(Message m) throws IOException {
        String type = null;
        int aimPort = m.getPortNum();
        int nodeId = this.nodeId;

        Message res = new Message(type, nodeId);
        res.setRoundId(roundId);
        res.setProposalId(accProposal);
        res.setPortNum(portNum);
        if(m.getProposalId()>=accProposal){
            res.setType("accOK");
            System.out.println("ACC:"+nodeId+":"+"accOK to"+"proposer" + m.getPortNum());
            this.accProposal = m.getProposalId();
            this.accVal = m.getContent();
        }
        else{
            res.setType("refuseAcc");
            System.out.println("ACC:"+nodeId+":"+"refuseAcc to"+"proposer" + m.getPortNum());
        }
        if(m.getPortNum()==this.portNum){
            io.udpThread.addBuffer(res);
            //io.udpThread.getBuffer().offer(res);
            //System.out.println(io.udpThread.getMessage());
        }

        else
            io.sendByUdp(aimPort,res);
    }

    public List getChosenVal(){
        return chosenVal;
    }

    public void learn(Message m){
        // 同步
    }


    public void run() {


        while (true) {

            try {
                Message m = null;
                while(m==null){
                    m = io.udpThread.getMessage();
                    sleep(10);
                }
//                if(m.getRoundId()!=roundId){
//                    sleep(10);
//                    continue;
//                }

                if(m.getType().equals("prepareRequest")){
                    System.out.println(m.getType());
                    replyPre(m);
                }
                else if(m.getType().equals("acceptRequest")){
                    System.out.println(m.getContent());
                    replyAcc(m);
                }
                else if(m.getType().equals("chosen")){
                    if(m.getContent().equals(accVal)&&roundId==m.getRoundId()){
                        roundId++;
                        chosenVal.add(accVal);
                        System.out.println("nodeId"+nodeId+"roundId:"+roundId+"chose"+accVal);
                        accVal = null;
                        accProposal = -1;
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
//                waitingForPre();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }





    }
}
