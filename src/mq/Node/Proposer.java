package mq.Node;

import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

public class Proposer {

    public Set<Integer> targets;
    private int roundId = 0;
    private int proposalId;
    private int nodeId;
    private int portNum;
    private int otherProposal;
    private String otherVal;
    private boolean notWorking;
    private boolean isPreparing;
    private boolean isAccepting;
    private boolean needPrepare;
    private boolean beenRejected;
    private Set<Integer> resForPre;
    private  Set<Integer> resForAcc;
    private  NetworkLayer io;
    private String newVal =null;
    private int retryTimeLimit = 3;
    private int retryTimes;
    Acceptor acceptor;

    public Proposer(int nodeId,NetworkLayer io){
        this.io = io;
        this.portNum = io.portNum;
        this.nodeId = nodeId;
        resForPre = new HashSet<>();
        resForAcc = new HashSet<>();

    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    public void reset(int roundId){
        resForPre.clear();
        resForAcc.clear();
        proposalId = nodeId;
        this.roundId = roundId;
        otherProposal = -1;
        otherVal = null;
        notWorking = true;
        isPreparing = false;
        isAccepting = false;
        needPrepare = true;
        beenRejected = false;
        retryTimes = 0;
    }

    public int newRound(String newVal) throws IOException, InterruptedException {
        this.newVal = newVal;
         prepare();

        waitingForPre();
         if(retryTimes>retryTimeLimit)
             return -1;
         accept();

        waitingForAcc();
        if(retryTimes>retryTimeLimit)
            return -1;
        return 0;

    }


    int prepare() throws IOException, InterruptedException {
        if(beenRejected) proposalId+=10;
        notWorking = false;
        isPreparing = true;
        needPrepare = true;
        beenRejected = false;
        resForAcc.clear();
        resForPre.clear();
        Message pre = new Message("prepareRequest",nodeId);
        pre.setRoundId(roundId);
        pre.setProposalId(proposalId);
        pre.setPortNum(portNum);
        System.out.println(io.portNum);
        io.udpThread.addBuffer(pre);
        io.sendByTargetsUdp(pre);

        return 0;
    }

    int waitingForPre() throws IOException, InterruptedException {
        long begin = System.currentTimeMillis();
        int max = io.getTarget().size();
        int ok = 0;
        int rej = 0;
        while(isPreparing){
            Message respond = null;
            while(respond==null){
                respond = io.udpThread.getMessage();
            }
            if(respond.getRoundId()==roundId&&!resForPre.contains(respond.getPortNum())) {
                resForPre.add(respond.getPortNum());
                if(respond.getType().equals("refusePre")){
                    beenRejected=true;
                    rej++;
                    if(otherProposal<respond.getProposalId()){

                        otherProposal = respond.getProposalId();
                        otherVal = respond.getContent();
                    }
                    if(rej>=max/2||System.currentTimeMillis()-begin>200){
                        sleep(20);
                        if(++retryTimes>=retryTimeLimit)
                            return -1;
                        prepare();
                    }
                }
                else if(respond.getType().equals("preOK")){
                    ok++;

                    if(ok>max/2){
                        isPreparing = false;
                        return 0;
                    }

                }
            }
        }
        return 0;
    }

    int accept() throws IOException, InterruptedException {
        notWorking = false;
        isPreparing = false;
        isAccepting = true;
        beenRejected = false;
        Message acc = new Message("acceptRequest",nodeId);
        System.out.println(nodeId+":send accept out");
        acc.setRoundId(roundId);
        acc.setProposalId(proposalId);
        acc.setPortNum(portNum);
        if(otherVal!=null) acc.setContent(otherVal);
        else acc.setContent(newVal);
        newVal = acc.getContent();
        //System.out.println("This is Proposer"+nodeId+":"+acc.getContent()+"234567890-sdfghjkl;cfg");
        io.udpThread.addBuffer(acc);
        io.sendByTargetsUdp(acc);

        return 0;
    }

    int waitingForAcc() throws IOException, InterruptedException {
        long begin = System.currentTimeMillis();
        int max = io.getTarget().size();
        int ok = 0;
        int rej = 0;
        while(isAccepting){
            Message respond = null;
            while(respond==null){
                respond = io.udpThread.getMessage();
            }
            if(respond.getRoundId()==roundId&&respond.getProposalId()==proposalId&&!resForAcc.contains(respond.nodeId)) {
                resForAcc.add(respond.nodeId);
                if(respond.getType().equals("refuseAcc")){
                    rej++;
                    if(rej>=max/2||System.currentTimeMillis()-begin>2000){
                        //isPreparing = false;
                        sleep(10);
                        if(++retryTimes>=retryTimeLimit)
                            return -1;
                        prepare();
                    }
                }
                else if(respond.getType().equals("accOK")){
                    ok++;
                    if(ok>max/2){
                        //System.out.println(nodeId+"finished");
                        Message chosen = new Message("chosen",nodeId);
                        chosen.setRoundId(roundId);
                        chosen.setContent(newVal);
                        chosen.setPortNum(portNum);
                        chosen.setContent(newVal);
                        io.udpThread.addBuffer(chosen);
                        io.sendByTargetsUdp(chosen);
                        isAccepting = false;
                        notWorking = false;
                        //acceptor.getChosenVal().add(newVal);
                    }
                }
            }
        }
        return 0;
    }


}
