package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.RelativeTime;

public class DelayCalculator {

	public static RelativeTime calculateMaximumDelayBetweenThreads(AbsoluteTime[][] threadTimes, int numberOfThreads, int numberOfLoops) {
		// calculating the minimum and maximum finishing times of the threads
		AbsoluteTime[] minimumTime = new AbsoluteTime[numberOfLoops];
		AbsoluteTime[] maximumTime = new AbsoluteTime[numberOfLoops];
		
		
		for (int i = 0; i < numberOfThreads; i++) {
			for (int j = 0; j < numberOfLoops; j++) {
				if (minimumTime[j] == null || threadTimes[i][j].compareTo(minimumTime[j]) < 0 )
					minimumTime[j] = threadTimes[i][j];
				
				if (maximumTime[j] == null || threadTimes[i][j].compareTo(maximumTime[j]) > 0 )
					maximumTime[j] = threadTimes[i][j];
				
			}
		}
		
		
		// calculating the maximum delay overall
		RelativeTime maximumDelay = new RelativeTime();
		
		for (int i = 0; i < numberOfLoops; i++) {
			// TODO: subtract absolute times
			RelativeTime temp = maximumTime[i].subtract(minimumTime[i]);
			
			if (temp.compareTo(maximumDelay) > 0)
				maximumDelay = temp;
		}
					    
		return maximumDelay;
		
	}
}
