


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
 

public class BenchmarkManager {
	
	boolean benchmarkFinished = false;
	
	public void setBenchmarkFinished(boolean benchmarkFinished) {
		this.benchmarkFinished = benchmarkFinished;
	}
	
	public static void main(String[] args) {
    	int NUMBER_OF_THREADS = 35;
    	int NUMBER_OF_LOOPS   = 1;
    	
    	long startTime = System.currentTimeMillis();
        
    	BenchmarkManager myManager = new BenchmarkManager();
        BenchmarkThreadManager myThreadManager = new BenchmarkThreadManager(NUMBER_OF_THREADS);
        myThreadManager.startThreads(myManager, NUMBER_OF_LOOPS);
        BenchmarkQuery.query();

//        TODO: add NUMBER_OF_THREADS as parameter
//        JniProducerCaller.main(null);
        
//        wo wird das hier gespeichert???
        
        

        while (!myManager.benchmarkFinished) {
        	try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        
        long endTime = System.currentTimeMillis();
        
    	try{
    		File myFile = new File("benchmarkTime.txt");
            PrintWriter writer = new PrintWriter(myFile);
            writer.println("Total execution time: " + (endTime-startTime) + "ms");
            writer.close();
        } catch (IOException e) {
           e.printStackTrace();
        }
        
        System.out.println("Benchmark finished.");
    }
}
