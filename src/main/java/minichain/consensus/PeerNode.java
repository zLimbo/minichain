package minichain.consensus;

import com.alibaba.fastjson.JSONObject;
import minichain.utils.SHA256Util;
import minichain.data.Block;
import minichain.data.Chain;
import minichain.data.Transaction;
import minichain.network.Net;

import java.util.*;

public class PeerNode extends Thread {

    private final String hash;
    private final long createTime;
    private long money = 0;
    private final Chain chain = new Chain();;
    private final Map<String, Block> newBlocks = new HashMap<>();

    public PeerNode() {
        // 默认节点名为线程名
        createTime = System.currentTimeMillis();
        hash = SHA256Util.sha256Digest(getName() + createTime);
        Net.registerPeerNode(this);    // 注册节点到区块链”网络“中
    }

    public PeerNode(String name) {
        this();
        this.setName(name);
    }

    public Chain getChain() {
        return chain;
    }

    public void acceptNewBlock(Block block) {
        synchronized (newBlocks) {
            newBlocks.put(block.getParentHash(), block);
        }
    }

    public void sendTransaction() {
        Transaction transaction = new Transaction(getName() + " - " + UUID.randomUUID());
        Net.addTransaction(transaction);
    }

    public boolean checkChain(Block block) {
        // todo
        return true;
    }

    public void synchronizeChain() {

        while (true) {
            Block block;
            synchronized (newBlocks) {
                block = newBlocks.remove(chain.getLatestBlock().getHash());
            }
            if (block == null) {
                break;
            }
            if (checkChain(block)) {
                chain.addBlock(block);
            }
        }
    }

    private void mineReward() {
        money += 100;
    }

    public void mineBlock() {
        Block latestBlock = chain.getLatestBlock();
        String parentHash = latestBlock.getHash();
        long index = latestBlock.getIndex();
        long nonce = Math.abs(new Random().nextLong());
        String blockHash = SHA256Util.sha256Digest(parentHash + nonce);
        if (blockHash.startsWith(Net.hashPrefixTarget())) {
            List<Transaction> transactions = Net.getTransactions();
            Block newBlock = new Block(
                    index + 1, blockHash, parentHash, Net.getDifficulty(), hash, nonce, transactions);
            chain.addBlock(newBlock);
            // 向其他节点广播
            Net.boardcast(this, newBlock);
            mineReward();

            // print log
            System.out.println("mined a block!");
            System.out.println(JSONObject.toJSONString(Net.toJson(), true));
            System.out.println(Thread.currentThread().getName() + ": " +
                    getName() + " mined a new Block! [nonce: " +
                    nonce + ", blockHash: " + blockHash + ", parentHash: " +
                    parentHash + "]");
            System.out.println("current node: " + getName() + " chain length: " + chain.getLength());
            System.out.println("all peer node chain length: " + Net.getAllPeerNodeChainLength());
        }

    }


    @Override
    public void run() {

        while (true) {
            // 一定概率发送一笔交易
            if (new Random().nextInt(Net.getSendProbability()) == 0) {
                sendTransaction();
            }
            // 每次计算块哈希前检查是否有其他节点已经挖出块
            synchronizeChain();
            // 一次挖矿尝试
            mineBlock();
        }
    }

    public String getHash() {
        return hash;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getMoney() {
        return money;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("hash", hash);
        json.put("createTime", createTime);
        json.put("money", money);
        synchronized (newBlocks) {
            json.put("new block num", newBlocks.size());
        }
        json.put("chain", chain.toJson());
        return json;
    }
}
