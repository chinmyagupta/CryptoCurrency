package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

import java.util.ArrayList;
import java.util.List;

public class BlockChain_Malicious {
    public int tr_count;
    public static final String start_string = "DSCoin";
    public TransactionBlock[] lastBlocksList;

    public static boolean checkTransactionBlock(TransactionBlock tB) {
        CRF obj = new CRF(64);
        if (!tB.dgst.startsWith("0000")) return false;
        if (tB.previous != null) {
            if (!obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce).equals(tB.dgst))
            return false;
        } else {
            if (!obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce).equals(tB.dgst))
                return false;

        }
        MerkleTree merkleTreeCheck = new MerkleTree();
        merkleTreeCheck.Build(tB.trarray);
        if (!merkleTreeCheck.rootnode.val.equals(tB.trsummary)) return false;
        for (Transaction tr : tB.trarray) {
            if (tB.checkTransaction(tr)) continue;
            return false;
        }
        return true;
    }

    public TransactionBlock FindLongestValidChain() {

        int maximum_len = 0;
        TransactionBlock result = null;

        for (TransactionBlock block1 : lastBlocksList) {
            if (block1 == null) continue;

            List<TransactionBlock> allBlocks = new ArrayList<>();
            while (true) {
                allBlocks.add(0, block1);
                if (block1.previous == null) break;
                block1 = block1.previous;
            }
            TransactionBlock localBlock = allBlocks.get(0);
            int length = 0;
            for (TransactionBlock block : allBlocks) {
                if (!checkTransactionBlock(block)) {
                    break;
                }
                localBlock = block;
                length += 1;
            }
            if (length < maximum_len) continue;
            maximum_len = length;
            result = localBlock;
        }
        return result;

    }

    public void InsertBlock_Malicious(TransactionBlock newBlock) {
        CRF obj = new CRF(64);

        boolean first_block = true;
        for (TransactionBlock tB : lastBlocksList)
            if (tB != null) {
                first_block = false;
                break;
            }
        if (first_block) {
            long final_nonce = 1000000001;
            while (!obj.Fn((start_string + "#" + newBlock.trsummary + "#") + final_nonce).startsWith("0000")) {
                final_nonce++;
            }
            newBlock.nonce = Long.toString(final_nonce);
            newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
            newBlock.previous = null;
            lastBlocksList = new TransactionBlock[100];
            lastBlocksList[0] = newBlock;
            return;
        }


        TransactionBlock block = FindLongestValidChain();
        long final_nonce = 1000000001;
        while (!obj.Fn((block.dgst + "#" + newBlock.trsummary + "#") + final_nonce).startsWith("0000")) {
            final_nonce++;
        }
        newBlock.nonce = Long.toString(final_nonce);
        newBlock.dgst = obj.Fn(block.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
        newBlock.previous = block;

        boolean wasThisLeaf = false;
        for (int i = 0; i < lastBlocksList.length; i++) {
            if (lastBlocksList[i] == null) continue;
            if (lastBlocksList[i].equals(block)) {
                wasThisLeaf = true;
                lastBlocksList[i] = newBlock;
                break;
            }
        }

        if (!wasThisLeaf) {
            for (int i = 0; i < lastBlocksList.length; i++) {
                if (lastBlocksList[i] == null) {
                    lastBlocksList[i] = newBlock;
                    break;
                }
            }
        }
    }
}