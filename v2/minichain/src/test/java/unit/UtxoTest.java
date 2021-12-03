package unit;

import data.*;
import network.Network;
import utils.SecurityUtil;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;

public class UtxoTest {

    @org.junit.Test
    public void utxoTest() {

        Network network = new Network();

        // 生成一笔特殊交易
        Transaction transaction = getOneTransaction(network);
        // 将该交易放入交易池中
        network.getTransactionPool().put(transaction);

        // 矿工工作，开始挖矿，这里挖出第一个块就会进入无线循环状态，仅作练习代码测试使用

        // 交易数组只有这一个交易
        Transaction[] transactions = { transaction };
        // 前一个区块的哈希
        String preBlockHash = SecurityUtil.sha256Digest(network.getBlockChain().getLatestBlock().toString());
        // 因为本区块只有一个交易，所以merkle根哈希即为该交易的哈希
        String merkleRootHash = SecurityUtil.sha256Digest(transaction.toString());
        // 构建区块
        BlockHeader blockHeader = new BlockHeader(preBlockHash, merkleRootHash, Math.abs(new Random().nextLong()));
        BlockBody blockBody = new BlockBody(merkleRootHash, transactions);
        Block block = new Block(blockHeader, blockBody);
        // 添加到链中
        network.getBlockChain().addNewBlock(block);

        // 广播区块
        network.getMinerPeer().broadcast(block);

        System.out.println("block: " + block);
    }

    /**
     * 生成一笔特殊交易：accounts[1] 支付给 accounts[2] 1000元, accounts[1]使用自己的公钥对交易签名
     * 可参考 TransactionProducer中的getOneTransaction() 设计你的代码
     * @param network
     * @return
     */
    Transaction getOneTransaction(Network network) {
        Transaction transaction = null;
        Account[] accounts = network.getAccounts();
        Account accountA = accounts[1];
        Account accountB = accounts[2];
        int txAmount = 1000;

        byte[] sign = SecurityUtil.signature(accountA.getPublicKey().getEncoded(), accountA.getPrivateKey());
        UTXO[] trueUtxos = network.getBlockChain().getTrueUtxos(accountA.getWalletAddress());

        ArrayList<UTXO> inUtxoList = new ArrayList<>();
        ArrayList<UTXO> outUtxoList = new ArrayList<>();

        int inAmount = 0;
        for (UTXO utxo: trueUtxos) {
            if (utxo.unlockScript(sign, accountA.getPublicKey())) {
                inAmount += utxo.getAmount();
                inUtxoList.add(utxo);
                if (inAmount >= txAmount) {
                    break;
                }
            }
        }

        outUtxoList.add(new UTXO(accountB.getWalletAddress(), txAmount, accountB.getPublicKey()));
        if (inAmount > txAmount) {
            outUtxoList.add(new UTXO(accountA.getWalletAddress(), inAmount - txAmount, accountA.getPublicKey()));
        }

        UTXO[] inUtxos = inUtxoList.toArray(new UTXO[0]);
        UTXO[] outUtxos = outUtxoList.toArray(new UTXO[0]);
        byte[] data = SecurityUtil.utxos2Bytes(inUtxos, outUtxos);
        byte[] sendSign = SecurityUtil.signature(data, accountA.getPrivateKey());
        PublicKey sendPublicKey = accountA.getPublicKey();
        long timestamp = System.currentTimeMillis();

        transaction = new Transaction(inUtxos,outUtxos, sendSign, sendPublicKey, timestamp);

        return transaction;
    }
}
