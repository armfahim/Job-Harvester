package io.naztech.jobharvestar;



import org.junit.Test;

public class CapacityDetectorTest {

	@Test
	public void test() {
		System.out.println("Capacity of this machine: "+getCapacity());
	}
	
	private int getCapacity() {
		Runtime runTime = Runtime.getRuntime();
		int processor = runTime.availableProcessors()-1;
		long usedMemory = runTime.totalMemory() - runTime.freeMemory(); //Byte
		int freeMemory = (int) ((runTime.maxMemory() - usedMemory)/1024)/1024; //MegaByte
		int finalMemory = freeMemory/512; // Demanding memory of each scraper (Approximate: 512)
		
		return finalMemory < processor ? finalMemory : processor;
	}

}
