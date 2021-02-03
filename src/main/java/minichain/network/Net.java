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
    public static int sendProbability = 100000;

    public static void registerPeerNode(PeerNode peerNode) {
        synchronized (peerNodes) {
            System.out.println("注册节点 [" + peerNode.getName() + "]");
            peerNodes.add(peerNode);
        }
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

    public static void addTransaction(Transaction transaction) {

        synchronized (transactionPool) {
            transactionPool.add(transaction);
        }
    }

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        while (transactions.size() < Block.MAX_TX_SIZE) {
            Transaction transaction = null;
            synchronized (transactionPool) {
                if (transactionPool.isEmpty()) {
                    break;
                }
                transaction = transactionPool.remove();
            }
            transactions.add(transaction);
        }
        return transactions;
    }

    public static int getDifficulty() {
        return difficulty;
    }

    public static int getSendProbability() {
        int peerNodeNum = 0;
        synchronized (peerNodes) {
            peerNodeNum = peerNodes.size();
        }
        return sendProbability * peerNodeNum;
    }

    public static String hashPrefixTarget() {
        StringBuilder stringBuilder = new StringBuilder("0x");
        for (int i = 0; i < difficulty; ++i) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    public static void boardcast(PeerNode fromPeerNode, Block newBlock) {
         synchronized (peerNodes) {
             for (PeerNode toPeerNode: peerNodes) {
                 if (toPeerNode != fromPeerNode) {
                     toPeerNode.acceptNewBlock(newBlock);
                 }
             }
         }
    }

    public static List<Integer> getAllPeerNodeChainLength() {
        List<Integer> lengthList = new ArrayList<>();

        synchronized (peerNodes) {
            for (PeerNode peerNode : peerNodes) {
                lengthList.add(peerNode.getChain().getLength());
            }
        }
        return lengthList;
    }

    public static JSONObject toJson() {
        JSONObject json = new JSONObject();
        synchronized (peerNodes) {
            for (PeerNode peerNode : peerNodes) {
                json.put(peerNode.getName(), peerNode.toJson());
            }
        }
        return json;
    }
}
