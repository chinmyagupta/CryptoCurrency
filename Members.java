package DSCoinPackage;

import java.util.*;

import HelperClasses.MerkleTree;
import HelperClasses.Pair;
import HelperClasses.TreeNode;

public class Members {

    public String UID;
    public List<Pair<String, TransactionBlock>> mycoins;
    public Transaction[] in_process_trans;

    public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {

        Pair<String, TransactionBlock> current_coin = mycoins.remove(0);
        Transaction new_trans = new Transaction();
        new_trans.coinsrc_block = current_coin.second;
        new_trans.coinID = current_coin.first;
        new_trans.Source = this;
        for (Members m : DSobj.memberlist) {
            if (m.UID.equals(destUID)) {
                new_trans.Destination = m;
                break;
            }
        }
        for (int i = 0; i < in_process_trans.length; i++) {
            if (in_process_trans[i] != null) continue;
            in_process_trans[i] = new_trans;
            break;
        }
        DSobj.pendingTransactions.AddTransactions(new_trans);
    }

    public Pair<List<Pair<String, String>>, List<Pair<String, String>>>
    finalizeCoinsend(Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {

        TransactionBlock block = DSObj.bChain.lastBlock;
        int position = -1;
        while (true) {
            if (block == null) throw new MissingTransactionException();
            boolean contains = false;
            int cnt = 0;
            for (Transaction tr : block.trarray) {
                if (tr.coinID != null & tr.Destination != null & tr.Source != null) {
                    if (tobj.coinID.equals(tr.coinID) & tobj.Source.equals(tr.Source)
                            & tobj.Destination.equals(tr.Destination)) {
                        contains = true;
                        position = cnt;
                        break;
                    }
                    cnt = cnt + 1;
                }
            }
            if (!contains) block = block.previous;
            else break;
        }

        MerkleTree newMerkleTree = block.Tree;

        ArrayList<Pair<String, String>> SCpath = new ArrayList<Pair<String, String>>();
        int depth = (int) (Math.log(newMerkleTree.numdocs) / Math.log(2));
        for (int i = 0; i < depth + 1; i++) SCpath.add(null);
        int checker = (newMerkleTree.numdocs) / 2;
        TreeNode current = newMerkleTree.rootnode;
        for (int i = depth - 1; i >= 0; i--) {
            Pair<String, String> p = new Pair<>(current.left.val, current.right.val);
            SCpath.set(i, p);
            if ((checker & (position - 1)) == 0) current = current.left;
            else current = current.right;
            checker = checker / 2;
        }
        SCpath.set(depth, new Pair<>(newMerkleTree.rootnode.val, null));
        ArrayList<Pair<String, String>> block_couples = new ArrayList<>();
        TransactionBlock cur = DSObj.bChain.lastBlock;
        while (true) {
            block_couples.add(0, new Pair<>(cur.dgst, cur.previous.dgst + "#" + cur.trsummary + "#" + cur.nonce));
            if (cur == block) {
                block_couples.add(0, new Pair<>(block.previous.dgst, null));
                break;
            }
            cur = cur.previous;
        }

        for (int i = 0; i < in_process_trans.length; i++) {
            if (in_process_trans[i] == tobj) {
                in_process_trans[i] = null;
            }
        }

        tobj.Destination.mycoins.add(new Pair<>(tobj.coinID, block));
        tobj.Destination.mycoins.sort(Comparator.comparingInt(o -> Integer.parseInt(o.first)));
        return new Pair<>(SCpath, block_couples);
    }

    public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {

        HashMap<String, Integer> dict = new HashMap<>();
        Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
        int position = 0;
        while (position < DSObj.bChain.tr_count - 1) {
            Transaction transaction = DSObj.pendingTransactions.RemoveTransaction();
            if (dict.containsKey(transaction.coinID)) continue;
            if (!DSObj.bChain.lastBlock.checkTransaction(transaction)) continue;

            dict.put(transaction.coinID, 1);
            trarray[position] = transaction;
            position++;
        }

        Transaction minerRewardTransaction = new Transaction();
        minerRewardTransaction.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this;
        minerRewardTransaction.coinsrc_block = null;
        trarray[DSObj.bChain.tr_count - 1] = minerRewardTransaction;

        TransactionBlock block = new TransactionBlock(trarray);
        DSObj.bChain.InsertBlock_Honest(block);

        mycoins.add(new Pair<>(minerRewardTransaction.coinID, null));
        mycoins.sort(Comparator.comparingInt(o -> Integer.parseInt(o.first)));

        DSObj.latestCoinID = minerRewardTransaction.coinID;
    }

    public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {

        HashMap<String, Integer> dict = new HashMap<>();
        Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
        int position = 0;
        while (position < DSObj.bChain.tr_count - 1) {
            Transaction transaction = DSObj.pendingTransactions.RemoveTransaction();
            if (dict.containsKey(transaction.coinID)) continue;

            dict.put(transaction.coinID, 1);
            trarray[position] = transaction;
            position++;

        }

        Transaction minerRewardTransaction = new Transaction();
        minerRewardTransaction.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this;
        minerRewardTransaction.coinsrc_block = null;
        trarray[DSObj.bChain.tr_count - 1] = minerRewardTransaction;

        TransactionBlock block = new TransactionBlock(trarray);
        DSObj.bChain.InsertBlock_Malicious(block);

        mycoins.add(new Pair<>(minerRewardTransaction.coinID, null));
        mycoins.sort(Comparator.comparingInt(o -> Integer.parseInt(o.first)));

        DSObj.latestCoinID = minerRewardTransaction.coinID;
    }
}