package p2;
/**
 * @author Lokesh Shanmuganandam
 */

import java.util.LinkedList;
import java.util.Queue;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
	

	//YOUR CODE GOES HERE...
	public Queue<Food> machineFoodList;
	public int capacity;

	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		//YOUR CODE GOES HERE...
		this.capacity = capacityIn;
		this.machineFoodList = new LinkedList<>();
	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public void makeFood(Cook cook, int orderNo) throws InterruptedException {
		//YOUR CODE GOES HERE...
		
		//Pseudo-code: makeFood()
		//Create an object of the CookAnItem class
		//Create a thread for this object of the CookAnItem class
		//Start the thread
		machineFoodList.add(machineFoodType);
		CookAnItem cookItem = new CookAnItem(cook, orderNo);
		Thread foodCookThread = new Thread(cookItem);
		foodCookThread.start();
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		private Cook cook;
		private int orderNo;
		
		public CookAnItem(Cook cook, int orderNo){
			this.cook = cook;
			this.orderNo = orderNo;
		}
		
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				
				//Pseudo-code: run()
				//Generate the machineCookingFood event after the start of the thread
				//The thread should sleep for the cooking time
				//Generate the machineDoneFood event after the thread resumes from sleep during the cook time
				//Log the cookFinishedFood event
				//Synchronized(Machine food list queue)
				//The cooked food is removed from the front of the Machine food list queue
				//notify all
				//Synchronized(Completed food list of cook)
				//The food items completed by the machine are added to the completed food list of cook
				//notify all
				
				Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
				Thread.sleep(machineFoodType.cookTimeMS);
				Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
				Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, this.orderNo));
				synchronized(machineFoodList){
					machineFoodList.remove();
					machineFoodList.notifyAll();
				}
				synchronized(cook.foodCompletedList){
					cook.foodCompletedList.add(machineFoodType);
					cook.foodCompletedList.notifyAll();
				}
			} catch(InterruptedException e) { }
		}
	}
 

	public String toString() {
		return machineName;
	}
}