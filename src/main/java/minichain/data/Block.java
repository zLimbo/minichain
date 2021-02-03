package minichain.data;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Block {

    public static final int MAX_TX_SIZE = 10;

    private final long index;
    private final String hash;
    private final String parentHash;
    private final int difficulty;
    private final String miner;
    private final long nonce;
    private final long timestamp;
    private final int transactionNum;
    private final List<Transaction> transactions;

    public Block(long index,
                 String hash,
                 String parentHash,
                 int difficulty,
                 String miner,
                 long nonce,
                 List<Transaction> transactions) {
        this.index = index;
        this.hash = hash;
        this.parentHash = parentHash;
        this.difficulty = difficulty;
        this.miner = miner;
        this.nonce = nonce;
        this.transactions = transactions;
        this.transactionNum = transactions.size();
        this.timestamp = System.currentTimeMillis();
    }

    public long getIndex() {
        return index;
    }

    public String getHash() {
        return hash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getMiner() {
        return miner;
    }

    public long getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTransactionNum() {
        return transactionNum;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public JSONObject toJson() {
        return (JSONObject) JSONObject.toJSON(this);
    }
}
