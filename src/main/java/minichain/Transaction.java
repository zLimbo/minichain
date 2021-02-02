package minichain;

import java.util.UUID;

public class Transaction {

    String data;
    String hash;

    public Transaction(String data) {
        this.data = data;
        this.hash = Util.sha256Digest(data);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
