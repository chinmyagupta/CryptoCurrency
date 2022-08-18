package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator {

    public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
        int tr_count = DSObj.bChain.tr_count;
        Members moderator = new Members();
        moderator.UID = "Moderator";
        int coin_available = 100000;
        int member_available = 0;
        // Distributing coins in round-robin fashion
        for (int cycle = 0; cycle < coinCount / tr_count; cycle++) {
            Transaction[] transactions = new Transaction[tr_count];
            for (int i = 0; i < tr_count; i++) {
                Transaction transaction = new Transaction();
                transaction.coinID = Integer.toString(coin_available);
                transaction.coinsrc_block = null;
                transaction.Destination = DSObj.memberlist[member_available % DSObj.memberlist.length];
                transaction.Source = moderator;
                transactions[i] = transaction;
                member_available += 1;
                coin_available += 1;
                DSObj.latestCoinID = transaction.coinID;
            }
            TransactionBlock new_block = new TransactionBlock(transactions);
            DSObj.bChain.InsertBlock_Honest(new_block);
            for (Transaction transaction : transactions) {
                transaction.Destination.mycoins.add(new Pair<>(transaction.coinID, new_block));
            }
        }
    }

    public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
        int tr_count = DSObj.bChain.tr_count;
        Members moderator = new Members();
        moderator.UID = "Moderator";
        int coin_available = 100000;
        int member_available = 0;
        // Distributing coins in round-robin fashion
        for (int cycle = 0; cycle < coinCount / tr_count; cycle++) {
            Transaction[] transactions = new Transaction[tr_count];
            for (int i = 0; i < tr_count; i++) {
                Transaction transaction = new Transaction();
                transaction.coinID = Integer.toString(coin_available);
                transaction.coinsrc_block = null;
                transaction.Destination = DSObj.memberlist[member_available % DSObj.memberlist.length];
                transaction.Source = moderator;
                transactions[i] = transaction;
                member_available += 1;
                coin_available += 1;
                DSObj.latestCoinID = transaction.coinID;
            }
            TransactionBlock new_block = new TransactionBlock(transactions);
            DSObj.bChain.InsertBlock_Malicious(new_block);
            for (Transaction transaction : transactions) {
                transaction.Destination.mycoins.add(new Pair<>(transaction.coinID, new_block));
            }
        }
    }
}