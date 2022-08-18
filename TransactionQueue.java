package DSCoinPackage;

import java.util.ArrayList;
import java.util.List;

public class TransactionQueue {

    public Transaction firstTransaction;
    public Transaction lastTransaction;
    public int numTransactions;

    List<Transaction> allTransactions = new ArrayList<>();

    public void AddTransactions(Transaction transaction) {
        if (firstTransaction != null) {
            allTransactions.add(transaction);
            lastTransaction = transaction;
            numTransactions++;
        } else {
            firstTransaction = transaction;
            lastTransaction = transaction;
            numTransactions = 1;
            allTransactions.add(transaction);
        }
    }

    public Transaction RemoveTransaction() throws EmptyQueueException {
        if (allTransactions.size() == 0) throw new EmptyQueueException();
        Transaction removed = firstTransaction;
        numTransactions--;
        if (firstTransaction == lastTransaction){
            firstTransaction = null;
            lastTransaction = null;
            allTransactions.remove(0);
        }else{
            allTransactions.remove(0);
            firstTransaction = allTransactions.get(0);
        }
        return removed;
    }

    public int size() {
        return allTransactions.size();
    }
}
