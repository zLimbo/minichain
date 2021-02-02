package minichain.data;

import minichain.utils.SHA256Util;

import java.util.ArrayList;
import java.util.List;

public class Chain {

    private final List<Block> blockList = new ArrayList<>();

    public Chain() {
        // 创世区块
        Block genesisBlock = new Block(SHA256Util.sha256Digest("genesis"),
                "", 0, "", 0, new ArrayList<>());
        blockList.add(genesisBlock);
    }

    public Block latestBlock() {
        synchronized (blockList) {
            return blockList.get(blockList.size() - 1);
        }
    }

    public void addBlock(Block block) {
        synchronized (blockList) {
            block.setIndex(blockList.size());
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

}
