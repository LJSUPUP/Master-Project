package mq.Node;
import java.io.*;
import java.net.*;
import java.util.*;

public class NodeThread extends Thread {
    private ServerSocket  serverSocket;
    private DatagramSocket datagramSocket;
    TcpThread tcpThread;
    UdpThread udpThread;
    public Set<Integer> targets;
    private int roundId = 0;
    private List<String> record;
    private int proposalId;
    private int nodeId;
    private int portNum = 0;
    private int otherProposal = -1;
    private String otherVal = null;
    byte[] data = null;
    boolean notWorking = true;
    boolean isPreparing = false;
    boolean isAccepting = false;
    boolean needPrepare = true;
    boolean beenRejected = false;
    Set<Integer> resForPre = new HashSet<>();
    Set<Integer> resForAcc = new HashSet<>();
    Queue<String> buffer;
    String newVal =null;


    public int getPortNum(){
        return this.portNum;
    }

    public NodeThread(int portNum, int nodeId){
        this.portNum = portNum;
        this.nodeId = nodeId;
        buffer = new LinkedList<>();
        proposalId = nodeId;
        record = new ArrayList<>();
        targets = new HashSet<>();
        try {
            datagramSocket = new DatagramSocket(portNum);
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int prepare() throws IOException {
        if(beenRejected) proposalId+=10;
        notWorking = false;
        isPreparing = true;
        needPrepare = true;
        beenRejected = false;
        roundId = record.size();
        resForAcc.clear();
        resForPre.clear();
        Message pre = new Message("prepareRequest",nodeId);
        pre.setRoundId(roundId);
        pre.setProposalId(proposalId);
        pre.setPortNum(getPortNum());
        this.sendByTargetsUdp(pre);
        return 0;
    }
    int waitingForPre() throws IOException, InterruptedException {
        long begin = System.currentTimeMillis();
        int max = targets.size();
        int ok = 0;
        int rej = 0;
        while(isPreparing){
            Message respond = null;
            while(respond==null){
                respond = udpThread.getMessage();
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
                    if(rej>=max/2||System.currentTimeMillis()-begin>2000){
                        sleep(200);
                        prepare();
                    }
                }
                else if(respond.getType().equals("preOK")){
                    ok++;

                    if(ok>max/2){
                        isPreparing = false;
                    }

                }
            }
        }
        return 0;
    }

    int accept() throws IOException {
        notWorking = false;
        isPreparing = false;
        isAccepting = true;
        beenRejected = false;
        Message acc = new Message("acceptRequest",nodeId);
        System.out.println(nodeId+":send accept out");
        acc.setRoundId(roundId);
        acc.setProposalId(proposalId);
        acc.setPortNum(getPortNum());
        if(otherVal!=null) acc.setContent(otherVal);
        else acc.setContent(newVal);
        //System.out.println("This is Proposer"+nodeId+":"+acc.getContent()+"234567890-sdfghjkl;cfg");
        sendByTargetsUdp(acc);
        return 0;
    }

    int waitingForAcc() throws IOException, InterruptedException {
        long begin = System.currentTimeMillis();
        int max = targets.size();
        int ok = 0;
        int rej = 0;
        while(isAccepting){
            Message respond = null;
            while(respond==null){
                respond = udpThread.getMessage();
            }
            if(respond.getRoundId()==roundId&&respond.getProposalId()==proposalId&&!resForAcc.contains(respond.nodeId)) {
                resForAcc.add(respond.nodeId);
                if(respond.getType().equals("refuseAcc")){
                    rej++;
                    if(rej>=max/2||System.currentTimeMillis()-begin>2000){
                        //isPreparing = false;
                        sleep(10);
                        prepare();
                    }
                }
                else if(respond.getType().equals("accOK")){
                    ok++;
                    if(ok>max/2){
                        System.out.println(nodeId+"finished");
                        isAccepting = false;
                        notWorking = false;
                    }
                }
            }
        }
        return 0;
    }

    private void learn() {
    }

    public void run() {

        tcpThread = new TcpThread(serverSocket,portNum);
        udpThread = new UdpThread(datagramSocket,portNum);
        tcpThread.start();
        udpThread.start();
        newVal=null;

        while (notWorking) {
            //System.out.println(String.valueOf(nodeId)+String.valueOf(notWorking));
            if(buffer.isEmpty()){
                while(newVal==null){
                    newVal = tcpThread.getValue();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //System.out.println(nodeId+newVal);
                }
            }
            else
                newVal = buffer.poll();
            //System.out.println(newVal);
            if(newVal!=null){
                try {
                    prepare();
                    waitingForPre();
                    accept();
                    waitingForAcc();
                    learn();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                waitingForPre();
            }
        }
       // System.out.println(String.valueOf(nodeId)+String.valueOf(notWorking));
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
    public void sendByTargetsTcp(Message m){
        for(int i:targets){
            sendByTcp(i,m);
        }
    }

    public void sendByTargetsUdp(Message m) throws IOException {
        for(int i:targets){
            sendByUdp(i,m);
            //System.out.println(i+m.getType());
        }
    }

    public void sendByTcp(int i, Message m) {
        TcpSenThread tcpSenThread = new TcpSenThread("127.0.0.1", i,m);
        tcpSenThread.start();
    }
    public void sendByUdp(int i, Message m) throws IOException {
        UdpSenThread udpSenThread = new UdpSenThread("127.0.0.1", i,m);
        udpSenThread.start();
    }
}
