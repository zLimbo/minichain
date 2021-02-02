package minichain;

import java.util.*;

public class Net {

    static final Set<PeerNode> peerNodes = new HashSet<>();
    static final Queue<Transaction> transactionPool = new LinkedList<>();
    static int difficulty = 4;

    static void registerPeerNode(PeerNode peerNode) {
        synchronized (peerNodes) {
            System.out.println("注册节点 [" + peerNode.getName() + "]");
            peerNodes.add(peerNode);
        }
    }

    static void addTransaction(Transaction transaction) {

        synchronized (transactionPool) {
            transactionPool.add(transaction);
        }
    }

    static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        while (true) {
            synchronized (transactionPool) {
                if (transactionPool.isEmpty()) {
                    break;
                }
                transactions.add(transactionPool.remove());
            }
        }
        return transactions;
    }

    static int getDifficulty() {
        return difficulty;
    }

    static String hashPrefixTarget() {
        StringBuilder stringBuilder = new StringBuilder("0x");
        for (int i = 0; i < difficulty; ++i) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }

    static void boardcast(PeerNode fromPeerNode) {
         synchronized (peerNodes) {
             for (PeerNode toPeerNode: peerNodes) {
                 if (toPeerNode != fromPeerNode) {
                     toPeerNode.setOtherLongChainPeerNode(fromPeerNode);
                 }
             }
         }
    }

    public Net(int peerNodeNum) {
        for (int i = 0; i < peerNodeNum; ++i) {
            PeerNode peerNode = new PeerNode("Node" + i);
            peerNode.start();
        }
    }
}
