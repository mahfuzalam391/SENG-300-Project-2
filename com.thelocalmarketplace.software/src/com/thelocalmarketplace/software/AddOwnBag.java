package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
//import com.jjjwelectronics.scale.ElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;

public class AddOwnBag {

	// create instance
	private ElectronicScaleListener electronicScaleListener;
	

	
	public AddOwnBag(Order order, AbstractElectronicScale scale1) {
		electronicScaleListener = new ElectronicScaleListener () {

			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				// TODO Auto-generated method stub
				
				double bag_grams = getBagWeight(order, scale1); //get weight of customers bags in grams 
				addbagweight(order, scale1, bag_grams); //try to account for bag weight to prevent discrepancy 
				
			}

			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				// TODO Auto-generated method stub
				
			}
			
		};	
		System.out.println("Add all your bags now");
	}
	
	public double getBagWeight(Order order, AbstractElectronicScale scale) {  
		double order_weight = order.getTotalWeightInGrams();
		double bag_weight = 0;
		//get order weight
		BigDecimal order_weight_double = new BigDecimal(Double.toString(order_weight));
		BigDecimal scale_weight;
		try {
			//scale - order = bag weight
			scale_weight = scale.getCurrentMassOnTheScale().inGrams();
			BigDecimal bag_weight_grams = scale_weight.subtract(order_weight_double);
			bag_weight = bag_weight_grams.doubleValue(); //convert to double 
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bag_weight;
	}
	
	// now that customer has signaled they want to add their own bags, pass in the weight of their own bags
	public void addbagweight(Order order, AbstractElectronicScale scale, double weight_of_bag) {
		
		//threshold = scale limit in mcg 
		BigInteger threshold = scale.getMassLimit().inMicrograms();
		
		try {
			//compare scale mass which bag mass to mass limit 
			int compare_to_threshold = scale.getCurrentMassOnTheScale().compareTo(new Mass(threshold));
			
			if (compare_to_threshold>=0) {
				System.out.println("Bags too heavy, not allowed");
				WeightDiscrepancy.setStationBlock(true); //block b/c to heavy 
				//call attendant 
				mockAttendant attend = new mockAttendant(order,scale,weight_of_bag);
				attend.notifyAttendant();
			
				
			}
			else {
				//bag weight is fine, add weight of bag to order, system unblocks
				WeightDiscrepancy.setStationBlock(false);  // change to unblock and continue 
				order.addTotalWeightInGrams(weight_of_bag);
				System.out.println("You may now continue");
			}

		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void print_mess() {
		System.out.print("You may now continue");
	}
		
		
		
	
}