package minichain.data;

import minichain.utils.SHA256Util;

public class Transaction {

    String data;
    String hash;

    public Transaction(String data) {
        this.data = data;
        this.hash = SHA256Util.sha256Digest(data);
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
