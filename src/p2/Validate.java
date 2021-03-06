package p2;

import java.util.List;

import p2.SimulationEvent;


/**
 * Validates a simulation
 */
public class Validate {
	private static class InvalidSimulationException extends Exception {
		public InvalidSimulationException() { }
	};

	// Helper method for validating the simulation
	private static void check(boolean check,
			String message) throws InvalidSimulationException {
		if (!check) {
			System.err.println("SIMULATION INVALID : "+message);
			throw new Validate.InvalidSimulationException();
		}
	}

	/** 
	 * Validates the given list of events is a valid simulation.
	 * Returns true if the simulation is valid, false otherwise.
	 *
	 * @param events - a list of events generated by the simulation
	 *   in the order they were generated.
	 *
	 * @returns res - whether the simulation was valid or not
	 */
	public static boolean validateSimulation(List<SimulationEvent> events, int noOfCustomer,
			int noOfCooks, int noOfTable, int machineCapacity) {
		try {
			check(events.get(0).event == SimulationEvent.EventType.SimulationStarting,
					"Simulation didn't start with initiation event");
			check(events.get(events.size()-1).event == 
					SimulationEvent.EventType.SimulationEnded,
					"Simulation didn't end with termination event");

			/* In P2 you will write validation code for things such as:
				Should not have more eaters than specified
				Should not have more cooks than specified
				The coffee shop capacity should not be exceeded
				The capacity of each machine should not be exceeded
				Eater should not receive order until cook completes it
				Eater should not leave coffee shop until order is received
				Eater should not place more than one order
				Cook should not work on order before it is placed
			 */
			check(checkCustomerCount(events, noOfCustomer),
					"Should not have more eaters than specified");
			check(checkNoOfCooks(events, noOfCooks),
					"Should not have more cooks than specified");
			check(checkCoffeeShopCapacity(events, noOfTable),
					"The coffee shop capacity should not be exceeded");
			check(checkMachineCapacity(events, machineCapacity),
					"The capacity of each machine should not be exceeded");
			check(checkCustomerLeavesBeforeOrderReceived(events),
					"Eater should not leave coffee shop until order is received");
			check(checkCustomerOrder(events, noOfCustomer),
					"Eater should not place more than one order");
			check(checkCookCookingBeforeOrderPlaced(events),
					"Cook should not work on order before it is placed");
			
			

			return true;
		} catch (InvalidSimulationException e) {
			return false;
		}
	}
	private static boolean checkCustomerCount(List<SimulationEvent> events, int customerCount){
		boolean flag = true;
		int currentCustomerCount = 0; 
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop){
				currentCustomerCount++;
			}
			if(currentCustomerCount > customerCount){
				return false;
			}
		}
		return flag;
	}
	
	private static boolean checkCoffeeShopCapacity(List<SimulationEvent> events, int noOfTable){
		boolean flag = true;
		int currentCustomerCount = 0; 
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop){
				currentCustomerCount++;
			}
			if(se.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop){
				currentCustomerCount--;
			}
			if(currentCustomerCount > noOfTable){
				return false;
			}
		}
		return flag;
	}
	private static boolean checkNoOfCooks(List<SimulationEvent> events, int noOfCooks){
		boolean flag = true;
		int currentCookCount = 0;
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.CookStarting){
				currentCookCount++;
			}
			if(currentCookCount > noOfCooks){
				flag = false;
			}
		}
		return flag;
	}
	private static boolean checkMachineCapacity(List<SimulationEvent> events, int machineCapacity){
		boolean flag = true;
		int burgersCount = 0; 
		int friesCount = 0;
		int coffeeCount = 0;
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.MachineStarting){
				if(se.machine.machineName.equalsIgnoreCase("Grill")){
					burgersCount++;
				}
				if(se.machine.machineName.equalsIgnoreCase("Fryer")){
					friesCount++;
				}
				if(se.machine.machineName.equalsIgnoreCase("CoffeeMaker2000")){
					coffeeCount++;
				}
			}
			if(se.event == SimulationEvent.EventType.MachineDoneFood){
				if(se.machine.machineName.equalsIgnoreCase("Grill")){
					burgersCount--;
				}
				if(se.machine.machineName.equalsIgnoreCase("Fryer")){
					friesCount--;
				}
				if(se.machine.machineName.equalsIgnoreCase("CoffeeMaker2000")){
					coffeeCount--;
				}
			}
			if(burgersCount > machineCapacity){
				flag = false;
			}
			if(friesCount > machineCapacity){
				flag = false;
			}
			if(coffeeCount > machineCapacity){
				flag = false;
			}
		}
		return flag;
	}
	private static boolean checkCustomerLeavesBeforeOrderReceived(List<SimulationEvent> events){
		boolean flag = false;
		int customerNo = 0;
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop){
				customerNo = se.customer.getOrderNum();
			}
			for(SimulationEvent se1: events){
				if(se1.event == SimulationEvent.EventType.CustomerReceivedOrder){
					if(customerNo == se1.customer.getOrderNum()){
						flag = true;
					}
				}
			}
		}
		return flag;
	}
	private static boolean checkCustomerOrder(List<SimulationEvent> events, int noOfCustomer){
		boolean flag = true;
		int customerOrdersCount = 0; 
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.CustomerPlacedOrder){
				customerOrdersCount++;
			}
			if(customerOrdersCount > noOfCustomer){
				flag = false;
			}
		}
		return flag;
	}
	private static boolean checkCookCookingBeforeOrderPlaced(List<SimulationEvent> events){
		boolean flag = false;
		int orderNumber = 0;
		for(SimulationEvent se: events){
			if(se.event == SimulationEvent.EventType.CookStartedFood){
				orderNumber = se.orderNumber;
			}
			for(SimulationEvent se1: events){
				if(se1.event == SimulationEvent.EventType.CustomerPlacedOrder){
					if(orderNumber == se1.orderNumber){
						flag = true;
					}
				}
			}
		}
		return flag;
	}

}
