package mq.Node;

import java.io.Serializable;

public class Message implements Serializable {
    String type = null;
    String content = null;
    int nodeId = 0;
    int roundId = 0;
    int proposalId = 0;
    int portNum;
    int stage;

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getPortNum() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public Message(String type, int id){
        this.type = type;
        this.nodeId = id;
    }
    public Message(String type, int id, String content){
        this.type = type;
        this.nodeId = id;
        this.content = content;
    }
    public Message(Message ms){
        this.type = ms.type;
        this.nodeId = ms.nodeId;
        this.content = ms.content;
        this.roundId = ms.roundId;
        this.proposalId = ms.proposalId;
    }

    public String getContent() {
        if(content==null) return null;
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int i){
        this.roundId = i;
    }

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }
    public String toString(){
        return this.type+" "+this.content+" "+this.nodeId+" "+this.roundId+this.proposalId;
    }
}
