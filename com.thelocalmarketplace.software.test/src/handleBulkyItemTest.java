package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.thelocalmarketplace.software.test.WeightDiscrepancyTest.MockItem;

import powerutility.PowerGrid;


public class handleBulkyItemTest {
	
	private Order orderB;
	private Order orderG;
	private Order orderS;
	private ElectronicScaleBronze scaleBronze;
	private ElectronicScaleGold scaleGold;
	private ElectronicScaleSilver scaleSilver;
	
	
	@Before
	public void setUp() throws OverloadedDevice {
		scaleBronze = new ElectronicScaleBronze();
		scaleGold = new ElectronicScaleGold();
		scaleSilver = new ElectronicScaleSilver();
        PowerGrid grid = PowerGrid.instance();
        scaleBronze.plugIn(grid);
        scaleBronze.turnOn();
        scaleBronze.enable();
        scaleGold = new ElectronicScaleGold();
        scaleGold.plugIn(grid);
        scaleGold.turnOn();
        scaleGold.enable();
        scaleSilver = new ElectronicScaleSilver();
        scaleSilver.plugIn(grid);
        scaleSilver.turnOn();
        scaleSilver.enable();
        orderB = new Order(scaleBronze);
        orderG = new Order(scaleGold);
        orderS = new Order(scaleSilver);
        
	}

    class MockItem extends Item {
        public MockItem(Mass mass) {
            super(mass);
        }
    }   
	
    // if creating and item requires a mass, why is there a separate addTotalWeightInGrams
    
    /*
     * Create test for handleBulkyItem, by creating an order and adding items + weights
     * Call handle bulky item, it should remove the weight of the second item
     * 
     * Should we check the scale-using methods work with the updated TotalWeight?
     *  
     */
    @Test
  	public void testHandleBulkyItem_finalWeightB() throws OverloadedDevice {
        MockItem item1 = new MockItem(new Mass(10));
        MockItem item2 = new MockItem(new Mass(60));
		
        orderB.addItemToOrder(item1); 
        scaleBronze.addAnItem(item1); // irrelevant?
        orderB.addTotalWeightInGrams(10);
        orderB.addItemToOrder(item2); 
        orderB.addTotalWeightInGrams(60);
       
             
        WeightDiscrepancy.handleBulkyItem(orderB, 60);
               
        
        double expectedTotalWeight = 10;
        assertEquals(expectedTotalWeight, orderB.getTotalWeightInGrams(), 0);
  	}
    
    @Test
  	public void testHandleBulkyItem_finalWeightG () throws OverloadedDevice {
        MockItem item1 = new MockItem(new Mass(10));
        MockItem item2 = new MockItem(new Mass(60));
		
        orderG.addItemToOrder(item1); 
        scaleGold.addAnItem(item1); // irrelevant?
        orderG.addTotalWeightInGrams(10);
        orderG.addItemToOrder(item2); 
        orderG.addTotalWeightInGrams(60);
       
             
        WeightDiscrepancy.handleBulkyItem(orderG, 60);
               
        
        double expectedTotalWeight = 10;
        assertEquals(expectedTotalWeight, orderG.getTotalWeightInGrams(), 0);
  	}
    
    @Test
  	public void testHandleBulkyItem_finalWeightS () throws OverloadedDevice {
        MockItem item1 = new MockItem(new Mass(10));
        MockItem item2 = new MockItem(new Mass(60));
		
        orderS.addItemToOrder(item1); 
        scaleSilver.addAnItem(item1); // irrelevant?
        orderS.addTotalWeightInGrams(10);
        orderS.addItemToOrder(item2); 
        orderS.addTotalWeightInGrams(60);
       
             
        WeightDiscrepancy.handleBulkyItem(orderS, 60);
               
        
        double expectedTotalWeight = 10;
        assertEquals(expectedTotalWeight, orderS.getTotalWeightInGrams(), 0);
  	}
}
