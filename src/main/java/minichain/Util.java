package minichain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    static final public String HEX_CHAR = "0123456789abcdef";


    static public String bytes2HexString(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        for (byte x: data) {
            int y = x & 0xff;
            stringBuilder.append(HEX_CHAR.charAt(y >>> 4));
            stringBuilder.append(HEX_CHAR.charAt(0xf & y));
        }
        return stringBuilder.toString();
    }


    static public String sha256Digest(String data) {
        MessageDigest sha256Digest = null;
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        sha256Digest.update(data.getBytes(StandardCharsets.UTF_8));
        return bytes2HexString(sha256Digest.digest());
    }


}
