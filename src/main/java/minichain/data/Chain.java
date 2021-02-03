package minichain.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import minichain.utils.SHA256Util;

import java.util.ArrayList;
import java.util.List;

public class Chain {

    private final List<Block> blockList = new ArrayList<>();

    public Chain() {
        // 创世区块
        Block genesisBlock = new Block(0, SHA256Util.sha256Digest("genesis"),
                "", 0, "", 0, new ArrayList<>());
        blockList.add(genesisBlock);
    }

    public Block getLatestBlock() {
        synchronized (blockList) {
            return blockList.get(blockList.size() - 1);
        }
    }

    public void addBlock(Block block) {
        synchronized (blockList) {
            blockList.add(block);
        }
    }

    public int getLength() {
        synchronized (blockList) {
            return blockList.size();
        }
    }

    public List<Block> getBlockList() {
        return blockList;
    }

    public Block getBlock(int num) {
        return blockList.get(num);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        synchronized (blockList) {
            json.put("length", blockList.size());
            json.put("blocks", JSON.toJSON(blockList));
        }
        return json;
    }
}
