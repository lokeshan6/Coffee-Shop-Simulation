package p2;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Lokesh Shanmuganandam
 */


/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private Customer currentCustomer;
	public List<Food> foodCompletedList = new LinkedList<>();
	

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				
				//Pseudo-code: run()
				//Synchronized(Customer order list queue)
				//WHILE the Customer order list queue is empty
				//	wait for the customer order by calling wait()
				//END WHILE
				//GET the current customer order from the Customer order list queue
				//Generate the cookReceivedOrder event
				//notify all
				//For each order of the customer
				//	Check for the food type
				//	Synchronized(food type)
				//	Generate an event for the corresponding food type
				//	Make the food type for the customer order
				//END LOOP
				//Check if all the customer's order is complete
				//Generate the cookCompletedOrder for the current customer
				//Update the completed order HashMap for the current customer with value true
				//notify all
				synchronized (Simulation.ordersPlacedList){
					while(Simulation.ordersPlacedList.isEmpty()){
						Simulation.ordersPlacedList.wait();
					}
					currentCustomer = Simulation.ordersPlacedList.remove();
					Simulation.ordersPlacedList.notifyAll();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, currentCustomer.getOrder(), currentCustomer.getOrderNum()));
				}
				for(int i = 0; i < currentCustomer.getOrder().size(); i++){
					Food currentFoodOrder = currentCustomer.getOrder().get(i);
					if(currentFoodOrder.equals(FoodType.burger)){
						synchronized(Simulation.machineGrill.machineFoodList){
							while(Simulation.machineGrill.machineFoodList.size() >= Simulation.machineGrill.capacity){
								Simulation.machineGrill.machineFoodList.wait();
							}
							Simulation.machineGrill.makeFood(this, currentCustomer.getOrderNum());
							Simulation.machineGrill.machineFoodList.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.burger, currentCustomer.getOrderNum()));
						}
					}
					else if(currentFoodOrder.equals(FoodType.fries)){
						synchronized(Simulation.machineFryer.machineFoodList){
							while(Simulation.machineFryer.machineFoodList.size() >= Simulation.machineFryer.capacity){
								Simulation.machineFryer.machineFoodList.wait();
							}
							Simulation.machineFryer.makeFood(this, currentCustomer.getOrderNum());
							Simulation.machineFryer.machineFoodList.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.fries, currentCustomer.getOrderNum()));
						}
					}
					else{
						synchronized(Simulation.machineCoffeeMaker.machineFoodList){
							while(Simulation.machineCoffeeMaker.machineFoodList.size() >= Simulation.machineCoffeeMaker.capacity){
								Simulation.machineCoffeeMaker.wait();
							}
							Simulation.machineCoffeeMaker.makeFood(this, currentCustomer.getOrderNum());
							Simulation.machineCoffeeMaker.machineFoodList.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.coffee, currentCustomer.getOrderNum()));
						}
					}
				}
				synchronized(foodCompletedList){
					while(currentCustomer.getOrder().size() != foodCompletedList.size()){
						foodCompletedList.wait();
						foodCompletedList.notifyAll();
					}
				}
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currentCustomer.getOrderNum()));
				foodCompletedList = new LinkedList<>();
				synchronized (Simulation.ordersCompletedMap){
					Simulation.ordersCompletedMap.put(currentCustomer, true);
					Simulation.ordersCompletedMap.notifyAll();
				}
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}