package mq.Node;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Set;

public class AcceptorThread extends Thread{

    private DatagramSocket datagramSocket;
    UdpThread udpThread;
    public Set<Integer> targets = null;
    private int roundId = 0;
    private int nodeId;
    private int portNum = 0;
    private int accProposal = -1;
    private String accVal = null;
    byte[] data = null;
    //boolean notWorking = true;


    public int getPortNum(){
        return this.portNum;
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
    public AcceptorThread(int portNum, int nodeId){
        this.portNum = portNum;
        this.nodeId = nodeId;
        try {
            datagramSocket = new DatagramSocket(portNum);
            //System.out.println(nodeId+"yesok"+portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendByUdp(int i, Message m) throws IOException {
        UdpSenThread udpThread = new UdpSenThread("127.0.0.1", i,m);
        udpThread.start();
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
        sendByUdp(target,res);
        //System.out.println("now2");
    }

    void replyAcc(Message m) throws IOException {
        String type = null;
        int portNum = m.getPortNum();
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
        sendByUdp(portNum,res);
    }

    public void getChosenVal(){
        System.out.println(accVal);
    }

    public void run() {

        udpThread = new UdpThread(datagramSocket,portNum);
        udpThread.start();

        while (true) {

            try {
                Message m = null;
                while(m==null){
                    m = udpThread.getMessage();
                    sleep(10);
                    //System.out.println(nodeId+"bp1");
                }

                //System.out.println(nodeId+"bp1");
                //if(m==null) continue;
                //System.out.println(nodeId+"bp2");
                //System.out.println(m.getType());
                if(m.getType().equals("prepareRequest")){
                    //System.out.println(m.getType());
                    replyPre(m);
                }
                else if(m.getType().equals("acceptRequest")){
                    replyAcc(m);
                }

                else if(m.getType().equals("learnRequest")){
                    //learn();
                }
                if(accVal!=null)
                    System.out.println(accVal);
                sleep(10);

            } catch (IOException e) {
                e.printStackTrace();
//                waitingForPre();
        } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }





}
}
