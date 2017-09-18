package SimulationToolForTheInternetOfThings;

/**
 * Created by daniel on 19.07.17.
 */
public class SensorThreadClock extends Thread {
    private boolean isSendable = false;
    private int frequency = -1;

    public SensorThreadClock (int frequency) {
        this.frequency = frequency;
    }

    @Override
    public void run() {
            synchronized (this) {
                System.out.println("Notifying threads");

                try {
                    Thread.sleep(Long.valueOf(frequency));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                notify();
            }
    }
}
