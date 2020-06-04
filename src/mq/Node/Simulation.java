package mq.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    public static void main(String[] args) throws IOException, InterruptedException {
        int[] ports = new int[]{8000,8100,8200,8300,8400,8500,8600,8700,8800,8900,9000};
        List<Node> nodes = new ArrayList<>();
        for(int i = 0;i < 10; i++){
            nodes.add(new Node(i,ports[i]));
        }
        for(Node node:nodes){
            for(int i = 0;i < 10; i++){
                if(ports[i]!=node.getIo().portNum){
                    node.getIo().addTarget(ports[i]);
                }
            }
        }

        for(Node node:nodes){
            node.start();
        }
        nodes.get(0).getProposer().newRound("cat");
        nodes.get(1).getProposer().newRound("smile");
    }



}
