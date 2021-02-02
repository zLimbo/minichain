package minichain;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Block {

    static final int MAX_TX_SIZE = 100;

    private long index = -1;
    private String hash;
    private String parentHash;
    private int difficulty;
    private String miner;
    private long nonce;
    private Long timestamp;
    private List<Transaction> transactions;

    public Block(String hash,
                 String parentHash,
                 int difficulty,
                 String miner,
                 long nonce,
                 List<Transaction> transactions) {
        this.hash = hash;
        this.parentHash = parentHash;
        this.difficulty = difficulty;
        this.miner = miner;
        this.nonce = nonce;
        this.transactions = transactions;
        this.timestamp = System.currentTimeMillis();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public JSONObject toJson() {
        return (JSONObject) JSONObject.toJSON(this);
    }
}
