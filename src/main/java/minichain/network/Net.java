package minichain.network;

import com.alibaba.fastjson.JSONObject;
import minichain.consensus.PeerNode;
import minichain.data.Block;
import minichain.data.Transaction;

import java.util.*;

public class Net {

    public static final Set<PeerNode> peerNodes = new HashSet<>();
    public static final Queue<Transaction> transactionPool = new LinkedList<>();
    public static int difficulty = 5;
    public static int sendProbability = 1000000;

    public static void registerPeerNode(PeerNode peerNode) {
        synchronized (peerNodes) {
            System.out.println("注册节点 [" + peerNode.getName() + "]");
            peerNodes.add(peerNode);
        }
    }

    public static void addTransaction(Transaction transaction) {

        synchronized (transactionPool) {
            transactionPool.add(transaction);
        }
    }

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        while (transactions.size() < Block.MAX_TX_SIZE) {
            synchronized (transactionPool) {
                if (transactionPool.isEmpty()) {
                    break;
                }
                transactions.add(transactionPool.remove());
            }
        }
        return transactions;
    }

    public static int getDifficulty() {
        return difficulty;
    }

    public static int getSendProbability() {
        synchronized (peerNodes) {
            return sendProbability * peerNodes.size();
        }
    }

    public static String hashPrefixTarget() {
        StringBuilder stringBuilder = new StringBuilder("0x");
        for (int i = 0; i < difficulty; ++i) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    public static void boardcast(PeerNode fromPeerNode) {
         synchronized (peerNodes) {
             for (PeerNode toPeerNode: peerNodes) {
                 if (toPeerNode != fromPeerNode) {
                     toPeerNode.setOtherLongChainPeerNode(fromPeerNode);
                 }
             }
         }
    }

    public static JSONObject toJson() {
        JSONObject json = new JSONObject();
        for (PeerNode peerNode: peerNodes) {
            json.put(peerNode.getName(), peerNode.toJson());
        }
        return json;
    }

    public static void simulate(int peerNodeNum) {
        for (int i = 0; i < peerNodeNum; ++i) {
            PeerNode peerNode = new PeerNode("Node" + i);
            peerNode.start();
        }
        System.out.println(JSONObject.toJSONString(Net.toJson(), true));
        while (true) ;
    }

    public static void simulate(int peerNodeNum, int difficulty) {
        Net.difficulty = difficulty;
        simulate(peerNodeNum);
    }
}
