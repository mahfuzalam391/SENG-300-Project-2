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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before; 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.thelocalmarketplace.software.Order;

import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;


@RunWith(Parameterized.class)
public class handleBulkyItemTest {
	private AbstractElectronicScale scale;
	private PowerGrid grid;
	
	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{new ElectronicScaleGold()},
			{new ElectronicScaleSilver()},
			{new ElectronicScaleBronze()}
		});
	}
	
	public handleBulkyItemTest(AbstractElectronicScale scale) {
		this.scale = scale;
	}
	
	@Before
	public void setUp() throws OverloadedDevice {
		grid = PowerGrid.instance();
		scale.plugIn(grid);
		scale.turnOn();
		scale.enable();
	}

    	class MockItem extends Item {
        	public MockItem(Mass mass) {
            	super(mass);
        	}
    	}   
	
    
    /*
     * 
     * Create test for handleBulkyItem with ONE bulky item, by creating an order and adding items + weights
     * Call handle bulky item, it should remove the weight of the second item
     * 
     * Should we check the scale-using methods work with the updated TotalWeight?
     * 
     * Tests all the scale types
     *  
     */
   	@Test
  	public void testHandleBulkyItem_finalWeight() throws OverloadedDevice {
    	Order order = new Order(scale);
        MockItem item1 = new MockItem(new Mass(10));
        MockItem item2 = new MockItem(new Mass(60));
		
        order.addItemToOrder(item1);
        order.addTotalWeightInGrams(10);
        order.addItemToOrder(item2); 
        order.addTotalWeightInGrams(60);
       
             
        WeightDiscrepancy.handleBulkyItem(order, 60);
               
        
        double expectedTotalWeight = 10;
        assertEquals(expectedTotalWeight, order.getTotalWeightInGrams(), 0);
  	}
   	
   	/*
     * Create a test to ensure that the system handles if the weight of the bulky item is 0 (by error)
     *  
     */
   	
   	@Test
   	public void zeroWeightBulkyItem() throws OverloadedDevice{
   		Order order = new Order(scale);
   		MockItem item1 = new MockItem(new Mass(10));
   		order.addItemToOrder(item1);
        order.addTotalWeightInGrams(10);
        
        WeightDiscrepancy.handleBulkyItem(order, 0);
        double expectedTotalWeight = 10;
        assertEquals(expectedTotalWeight, order.getTotalWeightInGrams(), 0);
   	}
   	
   	/*
     * Create a test to ensure that the system handles it when the bulky item is the first item added 
     * 
     * The system should not crash and it should correctly adjust the weight 
     *  
     */
   	
   	@Test
   	public void emptyOrderBulkyItem() throws OverloadedDevice{
   		Order order = new Order(scale);
   		
   		WeightDiscrepancy.handleBulkyItem(order, 79);
   		
   		double expectedTotalWeight = 0;
        assertEquals(expectedTotalWeight, order.getTotalWeightInGrams(), 0);
   		
   	}
   	
   	@Test
   	public void testHandleMultipleBulkyItems() throws OverloadedDevice {
   		Order order = new Order(scale);
   		MockItem item1 = new MockItem(new Mass(10));
   		MockItem bulkyItem1 = new MockItem(new Mass(60));
   		MockItem bulkyItem2 = new MockItem(new Mass(80));
   		
   		order.addItemToOrder(item1);;
   		order.addTotalWeightInGrams(10);
   		
   		order.addItemToOrder(bulkyItem1);
   		order.addTotalWeightInGrams(60);
   		
   		order.addItemToOrder(bulkyItem2);
   		order.addTotalWeightInGrams(80);
   		
   		WeightDiscrepancy.handleBulkyItem(order, 60);
   		WeightDiscrepancy.handleBulkyItem(order, 80);
   		
   		double expectedTotalWeight = 10;
   		
   		assertEquals(expectedTotalWeight, order.getTotalWeightInGrams(), 0);
   	}
}
