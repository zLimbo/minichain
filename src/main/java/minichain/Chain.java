package minichain;

import java.util.ArrayList;
import java.util.List;

public class Chain {

    private List<Block> blockList = new ArrayList<>();

    public Chain() {
        // 创世区块
        Block genesisBlock = new Block(Util.sha256Digest("genesis"),
                "", 0, "", 0, new ArrayList<>());
        blockList.add(genesisBlock);
    }

    public Block latestBlock() {
        return blockList.get(blockList.size() - 1);
    }

    public void addBlock(Block block) {
        blockList.add(block);
    }


//    public Block getBlock(int num) {
//
//    }

}
