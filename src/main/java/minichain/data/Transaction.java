package minichain.data;

import minichain.utils.SHA256Util;

public class Transaction {

    private final String data;
    private final String hash;

    public Transaction(String data) {
        this.data = data;
        this.hash = SHA256Util.sha256Digest(data);
    }

    public String getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }
}
