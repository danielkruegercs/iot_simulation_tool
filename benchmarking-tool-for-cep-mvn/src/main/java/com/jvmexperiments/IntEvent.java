package com.jvmexperiments;



public class IntEvent {
	private int threadNumber;
	private int loopNumber;
    private int myValue;


    public IntEvent(int threadNumber, int loopNumber, int myValue) {
		this.threadNumber = threadNumber;
		this.loopNumber = loopNumber;
		this.myValue = myValue;
	}

	public int getMyValue() {
        return myValue;
    }

	public int getThreadNumber() {
		return threadNumber;
	}

	public int getLoopNumber() {
		return loopNumber;
	}

}
