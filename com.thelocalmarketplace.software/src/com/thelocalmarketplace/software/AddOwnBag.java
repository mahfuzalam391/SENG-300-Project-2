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

	// allow customer to add thier own bags to bagging area without causing a weight discrepancy
	
	
	// 1. customer decides to make use of thier own bags, activating an appropriate control to thier station
			// some sort of button, add listener 
	
	
	
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
				
				double bag_grams = getBagWeight(order, scale1);
				addbagweight(order, scale1, bag_grams);
				
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
				WeightDiscrepancy.setStationBlock(true); //block b/c to heavy 
			//if possible add notification to attendant
				
			}
			else {
				WeightDiscrepancy.setStationBlock(false);  // change to unblock and continue 
				order.addTotalWeightInGrams(weight_of_bag);
				System.out.println("You may now continue");
			}

		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
		
		
		
	
}