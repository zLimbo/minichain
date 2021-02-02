package minichain;

import java.util.List;
import java.util.UUID;

public class PeerNode extends Thread {

    private String hash;
    private long createTime;
    private long money = 0;
    private PeerNode otherLongChainPeerNode = null;
    final private Object otherLongChainPeerNodeMutex = new Object();
    private Chain chain;

    public PeerNode() {
        // 默认节点名为线程名
        createTime = System.currentTimeMillis();
        hash = Util.sha256Digest(getName() + createTime);
        Net.registerPeerNode(this);    // 注册节点到区块链”网络“中
    }

    public PeerNode(String name) {
        this();
        this.setName(name);

        chain = new Chain();
    }

    public Chain getChain() {
        return chain;
    }

    public void setOtherLongChainPeerNode(PeerNode otherLongChainPeerNode) {
        synchronized (otherLongChainPeerNodeMutex) {
            this.otherLongChainPeerNode = otherLongChainPeerNode;
        }
    }

    public void sendTransaction() {
//        Transaction transaction = Transaction.randomTransaction();
        Transaction transaction = new Transaction(getName() + " - " + UUID.randomUUID());
        Net.addTransaction(transaction);
    }


    public boolean checkChain(Chain otherLongChain) {

        return true;
    }

    public void updateChain(Chain otherLongChain) {
        chain.addBlock(otherLongChain.latestBlock());
    }


    public boolean mineBlock() {

        long nonce = 1;
        String parentHash = chain.latestBlock().getHash();

        while (true) {
            // 每次计算块哈希前检查是否有其他节点已经挖出块
            synchronized (otherLongChainPeerNodeMutex) {
                if (otherLongChainPeerNode != null) {
                    System.out.println(getName() + " " + otherLongChainPeerNode.getName());
                    Chain otherLongChain = otherLongChainPeerNode.getChain();
                    if (checkChain(otherLongChain)) {
                        updateChain(otherLongChain);
                    }
                    otherLongChainPeerNode = null;
                    return false;
                }
            }

            ++nonce;
            String blockHash = Util.sha256Digest(parentHash + nonce);
            if (blockHash.startsWith(Net.hashPrefixTarget())) {
                System.out.println(Thread.currentThread().getName() + ": " +
                        getName() + " mined a new Block! [nonce: " +
                        nonce + ", blockHash: " + blockHash + ", parentHash: " +
                        parentHash + "]");
                List<Transaction> transactions = Net.getTransactions();
                Block block = new Block(blockHash, parentHash, Net.getDifficulty(), hash, nonce, transactions);
                chain.addBlock(block);
                Net.boardcast(this);
                return true;
            }
        }

    }


    @Override
    public void run() {

        while (true) {
            mineBlock();
        }
    }

}
