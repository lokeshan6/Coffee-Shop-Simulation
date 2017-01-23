package p2;
/**
 * @author Lokesh Shanmuganandam
 */

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;  
	private final int priority;
	private final int customerNo;
	private static int id = 0;
	
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order, int priority) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
		this.priority = priority;
		this.customerNo = ++id;
	}

	public String toString() {
		return name;
	}
	
	public List<Food> getOrder() {
		return order;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public int getPriority() {
		return priority;
	}

	public int getCustomerNo() {
		return customerNo;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		
		//Pseudo-code: run()
		//Log the customerStarting event, this should be the first event
		//Synchronized(Customer list queue)
		//WHILE the current customer list size >= number of table in the coffee shop
		//	Make the customer wait by calling the wait() 
		//END WHILE
		//The current customer is added to the customer list
		//Generate the customerEnteredCoffeeShop event
		//Synchronized(Customer order list)
		//Add the food items added by the customer to the Customer order list
		//Log the customerPlacedOrder event
		//notify all
		//Synchronized(Completed order HashMap)
		//Add the current customer order to the Completed order HashMap
		//Generate the customerReceivedOrder event when the customer order is complete
		//notify all
		//Synchronized(Customer List queue)
		//remove the current customer from the Customer List queue
		//Generate the customerLeavingCoffeeShop event
		//notify all
		
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		synchronized(Simulation.customerCapacityList){
			while(Simulation.customerCapacityList.size() >= Simulation.events.get(0).simParams[2]){
				try{
					Simulation.customerCapacityList.wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			Simulation.customerCapacityList.add(this);
			Simulation.customerCapacityList.notifyAll();
			Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
		}
		synchronized(Simulation.ordersPlacedList){
			Simulation.ordersPlacedList.add(this);
			Simulation.ordersPlacedList.notifyAll();
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
		}
		synchronized(Simulation.ordersCompletedMap){
			Simulation.ordersCompletedMap.put(this, false);
		}
		synchronized(Simulation.ordersCompletedMap){
			while(!Simulation.ordersCompletedMap.get(this)){
				try{
					Simulation.ordersCompletedMap.wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
			Simulation.ordersCompletedMap.notifyAll();
		}
		synchronized(Simulation.customerCapacityList){
			Simulation.customerCapacityList.remove(this);
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
			Simulation.customerCapacityList.notifyAll();
		}
	}
}