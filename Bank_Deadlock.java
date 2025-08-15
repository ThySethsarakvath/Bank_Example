import java.util.concurrent.locks.ReentrantLock;

public class Bank_Deadlock implements Runnable {
    private static final int NUM_ACCOUNTS = 3;
    private static final int INITIAL_BALANCE = 1000;
    private static final int[] balance = new int[NUM_ACCOUNTS];
    private static final ReentrantLock[] locks = new ReentrantLock[NUM_ACCOUNTS];

    static {
        for (int i = 0; i < NUM_ACCOUNTS; i++) {
            balance[i] = INITIAL_BALANCE;
            locks[i] = new ReentrantLock();
        }
    }

    private int fromAccount;
    private int toAccount;
    private int amount;

    public Bank_Deadlock(int from, int to, int amount) {
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
    }

    public void transfer(int account1, int account2, int amount) {
        int a = Math.min(account1, account2);
        int b = Math.max(account1, account2);

        locks[a].lock();
        try {
            locks[b].lock();
            try {
                if (balance[account1] >= amount) {
                    balance[account1] -= amount;
                    balance[account2] += amount;
                    System.out.printf("Transferred %d from account %d to account %d%n",
                                      amount, account1, account2);
                } else {
                    System.out.printf("Insufficient funds to transfer from account %d to account %d%n",
                                      account1, account2);
                }
            } finally {
                locks[b].unlock();
            }
        } finally {
            locks[a].unlock();
        }
    }

    public static void printBalances() {
        for (int i = 0; i < NUM_ACCOUNTS; i++) {
            System.out.printf("Account %d balance: %d%n", i, balance[i]);
        }
    }

    @Override
    public void run() {
        transfer(fromAccount, toAccount, amount);
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new Bank_Deadlock(0, 1, 100), "Thread1");
        Thread t2 = new Thread(new Bank_Deadlock(1, 2, 200), "Thread2");
        Thread t3 = new Thread(new Bank_Deadlock(2, 0, 150), "Thread3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printBalances();
    }
}
