/**
 * Yotam Rojnov (UCID: 30173949)
 * Duncan McKay (UCID: 30177857)
 * Mahfuz Alam (UCID:30142265)
 * Luis Trigueros Granillo (UCID: 30167989)
 * Lilia Skumatova (UCID: 30187339)
 * Abdelrahman Abbas (UCID: 30110374)
 * Talaal Irtija (UCID: 30169780)
 * Alejandro Cardona (UCID: 30178941)
 * Alexandre Duteau (UCID: 30192082)
 * Grace Johnson (UCID: 30149693)
 * Abil Momin (UCID: 30154771)
 * Tara Ghasemi M. Rad (UCID: 30171212)
 * Izabella Mawani (UCID: 30179738)
 * Binish Khalid (UCID: 30061367)
 * Fatima Khalid (UCID: 30140757)
 * Lucas Kasdorf (UCID: 30173922)
 * Emily Garcia-Volk (UCID: 30140791)
 * Yuinikoru Futamata (UCID: 30173228)
 * Joseph Tandyo (UCID: 30182561)
 * Syed Haider (UCID: 30143096)
 * Nami Marwah (UCID: 30178528)
 */
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

import com.jjjwelectronics.Item;
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
	private Mass massLimit; 
	

	
	
	/**
	 * Initializing order, addOwnBag and scale for test set up. 
	 * @throws OverloadedDevice
	 */
	
	@Before
	public void setUp() throws OverloadedDevice { 
		scale = new mockScale(new Mass(40000000),new Mass(40000000));
		order = new Order(scale);  
		massLimit = scale.getMassLimit();
		addOwnBag = new AddOwnBag(order, scale); 
	}

	
	/** MockItem class for use when adding an item to the order. 
	 * 
	 */
	class MockItem extends Item {
	       public MockItem(Mass mass) {
	           super(mass);
	       }
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
	
	
	
	// testing that when a bag is added with a weight above the threshold. If the new mass of the scale is above 
	// the scale threshold weight then it should block because too heavy 
	
	@Test 
	public void testAddBagWeightOverThreshold() throws OverloadedDevice { 
	// scale is over the massLimit 
	mockScale scaleOverLimit = new mockScale(new Mass(40000000), new Mass(40000000));
	//adding a new item to push scale over limit 
	scaleOverLimit.addAnItem(new MockItem(new Mass(45000000))); 
	//adding bag (1 gram) to the over limit scale 
	addOwnBag.addbagweight(order, scaleOverLimit, 1000000); 
	assertTrue(SelfCheckoutStationSoftware.getStationBlock()); 
	
	}
	
	
	@Test
	public void testAddBagWeightWithinThreshold() throws OverloadedDevice {
		//BigDecimal threshholdLimit = new ; 
		mockScale scaleInLimit = new mockScale(new Mass(30000000), new Mass(30000000));
		scaleInLimit.addAnItem(new MockItem(new Mass(40000000)));  
		addOwnBag.addbagweight(order, scaleInLimit, 10000000);
	}
}







