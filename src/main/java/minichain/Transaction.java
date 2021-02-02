package minichain;

import java.util.UUID;

public class Transaction {

    String data;
    String hash;


    public Transaction(String data) {
        this.data = data;
        this.hash = Util.sha256Digest(data);
    }


    static public Transaction randomTransaction() {
        String data = UUID.randomUUID().toString();
        return new Transaction(data);
    }
}
