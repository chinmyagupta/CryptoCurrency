package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

    public Transaction[] trarray;
    public TransactionBlock previous;
    public MerkleTree Tree;
    public String trsummary;
    public String nonce;
    public String dgst;

    TransactionBlock(Transaction[] t) {
        trarray = new Transaction[t.length];
        for (int i = 0; i < t.length; i++) this.trarray[i] = t[i];
        previous = null;
        Tree = new MerkleTree();
        Tree.Build(trarray);
        trsummary = Tree.rootnode.val;
        nonce = null;
        dgst = null;
    }

    public boolean checkTransaction(Transaction t) {

        if (t.coinsrc_block == null) return true;
        boolean transaction_present = false;

        for (Transaction transaction : t.coinsrc_block.trarray) {
            if (t.Source.equals(transaction.Destination) & t.coinID.equals(transaction.coinID)) {
                transaction_present = true;
                break;
            }
        }
        if (!transaction_present) return transaction_present;

        TransactionBlock current_block = this;

        while (true) {
            if (t.coinsrc_block.equals(current_block)) return true;
            for (Transaction transaction : current_block.trarray) {
                if (!t.coinID.equals(transaction.coinID)) {
                    continue;
                }
                return false;
            }
            current_block = current_block.previous;
        }
    }
}