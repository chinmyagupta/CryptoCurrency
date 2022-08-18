package DSCoinPackage;
import HelperClasses.CRF;
public class BlockChain_Honest {
    public int tr_count;
    public static final String start_string = "DSCoin";
    public TransactionBlock lastBlock;
    public void InsertBlock_Honest(TransactionBlock newBlock) {
        CRF obj = new CRF(64);
        if (lastBlock == null) {
            long final_nonce = 1000000001;
            while (!obj.Fn((start_string + "#" + newBlock.trsummary + "#") + Long.toString(final_nonce)).startsWith("0000")) final_nonce = final_nonce + 1;
            newBlock.nonce = Long.toString(final_nonce);
            newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
            this.lastBlock = newBlock;
            return;
        }
        long final_nonce = 1000000001;
        while (!obj.Fn((lastBlock.dgst + "#" + newBlock.trsummary + "#") + Long.toString(final_nonce)).startsWith("0000")) final_nonce++;
        newBlock.nonce = Long.toString(final_nonce);
        newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
        newBlock.previous = lastBlock;
        this.lastBlock = newBlock;
    }
}
