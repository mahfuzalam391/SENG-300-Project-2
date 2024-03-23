package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.thelocalmarketplace.software.AddOwnBag;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;

public class AddOwnBagTest {
	private Order order; 
	private AddOwnBag addOwnBag; //object under test 
	private mockScale scale;
	private BigDecimal threshold; 
	
	
	/**
	 * Initializing order, addOwnBag and scale for test set up. 
	 * @throws OverloadedDevice
	 */
	
	@Before
	public void setUp() throws OverloadedDevice { 
		scale = new mockScale(new Mass(40000000),new Mass(40000000));
		order = new Order(scale); 
		addOwnBag = new AddOwnBag(order, scale); 
		threshold = new BigDecimal(50000000);
		
	
		
	}

	@Test 
	public void testGetBagWeightBagAdded() throws OverloadedDevice {
		//the order has a weight of the mock scale in before (40000000)
		// weight of the scale with the order and the bag added 10 grams more than the weight of the order
		mockScale orderAndBagScale = new mockScale(new Mass(5000000), new Mass(5000000)); 
		AddOwnBag addOwnBag = new AddOwnBag(order, orderAndBagScale);
		//bag weight being calculated by subtracting the order weight from the order and bag weight on the scale 
		double bagWeight = addOwnBag.getBagWeight(order, orderAndBagScale);
		// the difference expected is 10 grams so bagWeight should be 10 grams 
		assertEquals(10.0, bagWeight, 10.0);
	}
 
	@Test
	public void testGetBagWeightNoBag() throws OverloadedDevice {
		mockScale orderNoBagScale = new mockScale(new Mass(4000000), new Mass (4000000));
		AddOwnBag addOwnBag = new AddOwnBag(order, orderNoBagScale);
		double bagWeight = addOwnBag.getBagWeight(order, orderNoBagScale);
		assertEquals(0.0, bagWeight, 0.0);
	}
	
	
	@Test 
	public void testAddBagWeightOverThreshold() throws OverloadedDevice {
	//making the scale weight above the threshold weight 
	mockScale scaleOverLimit = new mockScale(new Mass(6000000), new Mass(60000000));
	AddOwnBag addOwnbag = new AddOwnBag(order, scaleOverLimit); 
	addOwnbag.addbagweight(order, scaleOverLimit, 50000000); 
	assertTrue(SelfCheckoutStationSoftware.getStationBlock()); 
	
	}
	
	
	
	@Test
	public void testAddBagWeightWithinThreshold() throws OverloadedDevice {
		//BigDecimal threshholdLimit = new ; 
		mockScale scaleInLimit = new mockScale(new Mass(40000000), new Mass(40000000));
		AddOwnBag addOwnBag = new AddOwnBag(order, scaleInLimit); 
		//addOwnbag.addbagweight(order, scaleInLimit, threshholdLimit);
		
	}
}

