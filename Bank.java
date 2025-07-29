public class Bank implements Runnable {
    private static int balance = 0;
    // Optional: uncomment to fix race conditions
    private static final Object lock = new Object();

    public void deposit() {
        // Simulate delay to emphasize race condition (optional)
        // try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        balance += 100;
    }

    public void withdraw() {
        balance -= 100;
    }

    public int getValue() {
        return balance;
    }

    @Override
    public void run() {
        // Uncomment synchronized block to fix race condition
         synchronized (lock) {
            deposit();
            System.out.println("Value for Thread after deposit " + Thread.currentThread().getName() + ": " + getValue());
            withdraw();
            System.out.println("Value for Thread after withdraw " + Thread.currentThread().getName() + ": " + getValue());
         }
    }

    public static void main(String[] args) {
        Bank bankInstance = new Bank();

        Thread t1 = new Thread(bankInstance, "Thread1");
        Thread t2 = new Thread(bankInstance, "Thread2");
        Thread t3 = new Thread(bankInstance, "Thread3");

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
    }
}
