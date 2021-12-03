package spv;

import consensus.MinerPeer;
import data.*;
import network.Network;
import utils.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 轻节点类，只存储区块头信息，不存储具体交易数据，可向全节点发送验证请求获取某交易的验证路径，以此验证该交易的真实性
 */
public class SpvPeer {

    // 该spv节点只存储区块头
    private final List<BlockHeader> headers = new ArrayList<>();

    // 该spv拥有一个账户信息
    private final Account account;

    // 节点连接到网络
    private final Network network;

    public SpvPeer(Account account, Network network) {
        this.account = account;
        this.network = network;
    }

    /**
     * 添加一个区块头
     * @param blockHeader
     */
    public void accept(BlockHeader blockHeader) {
        headers.add(blockHeader);

        // 接受后验证一下
        verifyLatest();
    }

    /**
     * 如果有相关的交易, 验证最新块的交易
     */
    public void verifyLatest() {
        // spv节点通过网络搜集最新块与自己相关的交易

        String walletAddress = account.getWalletAddress();
        List<Transaction> transactions = network.getTransactionsInLatestBlock(walletAddress);
        if (transactions.isEmpty()) {
            return;
        }
        // 富翁使用自己"贫瘠不堪"的spv节点使用spv请求验证他参与的交易
        System.out.println("Account[" + walletAddress + "] began to verify the transaction...");
        for (Transaction transaction: transactions) {
            if (!simplifiedPaymentVerify(transaction)) {
                // 因为理论上肯定能验证成功，如果失败说明程序出现了bug，所以直接退出
                System.out.println("verification failed!");
                System.exit(-1);
            }
        }
        System.out.println("Account[" + walletAddress + "] verifies all transactions are successful!\n");
    }

    /**
     * spv验证
     * @param transaction
     * @return
     */
    public boolean simplifiedPaymentVerify(Transaction transaction) {
        // 获取交易哈希
        String txHash = SecurityUtil.sha256Digest(transaction.toString());

        // 通过网络向其他全节点获取验证路径（我们这里网络只有矿工一个全节点）
        MinerPeer minerPeer = network.getMinerPeer();
        Proof proof = minerPeer.getProof(txHash);

        if (proof == null) {
            return false;
        }

        // 使用获得的验证路径计算merkel根哈希
        String hash = proof.getTxHash();
        for (Proof.Node node: proof.getPath()) {
            // 此处可查看 MinerPeer 节点类里计算根哈希的方式
            switch (node.getOrientation()) {
                case LEFT: hash = SecurityUtil.sha256Digest(node.getTxHash() + hash); break;
                case RIGHT: hash = SecurityUtil.sha256Digest(hash + node.getTxHash()); break;
                default: return false;
            }
        }

        // 获得本地区块头部中的根哈希
        int height = proof.getHeight();
        String localMerkleRootHash = headers.get(height).getMerkleRootHash();

        // 获取远程节点发送过来的根哈希
        String remoteMerkleRootHash = proof.getMerkleRootHash();

        // 调试
        System.out.println("\n--------> verify hash:\t" + txHash);
        System.out.println("calMerkleRootHash:\t\t" + hash);
        System.out.println("localMerkleRootHash:\t" + localMerkleRootHash);
        System.out.println("remoteMerkleRootHash:\t" + remoteMerkleRootHash);
        System.out.println();

        // 判断生成的根哈希与本地的根哈希和远程的根哈希是否相等
        return hash.equals(localMerkleRootHash) && hash.equals(remoteMerkleRootHash);
    }
}
