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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;
import com.thelocalmarketplace.software.test.HandleTest.MockItem;

import powerutility.PowerGrid;

@RunWith(Parameterized.class)
public class WeightDiscrepancyTest {            
	
	private WeightDiscrepancy weightDiscrepancy;
	private Order order; 
	private mockScale scale;
	private Order order4;
	private mockScale  scale4;
	private Order order5;
	private mockScale scale5;
	private WeightDiscrepancy weightDiscrepancy4; 
	
	private AbstractElectronicScale eScale;
	private PowerGrid grid;
     
     
    /** Create before test for set up that initializes scale, order and weight Discrepancy
     * @throws OverloadedDevice */
	
	
	 
	@Before
	public void setUp() throws OverloadedDevice {   
		
		 		
	        scale = new mockScale(new Mass(40000000),new Mass(40000000));  
	        PowerGrid grid = PowerGrid.instance();
	        scale.plugIn(grid);
	        scale.turnOn();
	        scale.enable();
	        order = new Order(scale);
	        weightDiscrepancy = new WeightDiscrepancy(order, scale);  
	        
	        grid = PowerGrid.instance();
	        eScale.plugIn(grid);
	        eScale.turnOn();
	        eScale.enable();
	        order5 = new Order(eScale);
    }
	
	  	
	/** Create MockItem class that extends Item class, will be used when need to make an item to add to order/scale etc */
    class MockItem extends Item {
        public MockItem(Mass mass) {
            super(mass);
        }
    }   
    
    
    
    /** Create test for update mass, do this by creating two items, set the mass, add it to order
     * and then call update mass, the expected mass should be equal to what the scale says
     * @throws OverloadedDevice
     */
    @Test
    public void testUpdateMass_AddItemToOrder() throws OverloadedDevice {
         
    	MockItem item1 = new MockItem(new Mass(100));
    	MockItem item2 = new MockItem(new Mass(100));      
        order.addItemToOrder(item1);
        order.addItemToOrder(item2);
        WeightDiscrepancy weightDiscrepancy2 = new WeightDiscrepancy(order,scale);  
        weightDiscrepancy2.updateMass();
        Mass expectedMass = new Mass(200);
        assertEquals(expectedMass, scale.getCurrentMassOnTheScale());       
    } 
    

    /**
     * Create test for check discrepancy if there is a difference in weight
     * do this by calling the mock scale class we created, then creating an item and adding it to order and scale
     * next in order to compare the order and the scale we set what the weight should be (2) and then we call  
     * weightDiscrepancy.checkDiscrepancy,and the function compares the two values and add assertTrue to ensure 
     * the function is returning the correct answer
     * @throws OverloadedDevice
     */

    @Test
    public void testcheckDiscrepancy_diff() throws OverloadedDevice {
    	 scale5 = new mockScale(new Mass(6000000),new Mass (6000000));
         PowerGrid grid = PowerGrid.instance();
         scale5.plugIn(grid);
         scale5.turnOn();
         scale5.enable();
         
         Mass mass1 = new Mass(5000000);
         MockItem item1 = new MockItem(mass1); 
         scale5.addAnItem(item1); 
         order5 = new Order(scale5);
        
         order5.addTotalWeightInGrams(2);  
       
         weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);  
         weightDiscrepancy.checkDiscrepancy();
        
         assertTrue(SelfCheckoutStationSoftware.getStationBlock());     
        
            }
    
    
    /**
     * Create test for check discrepancy if there is no difference in weight
     * do this by calling the mockscale class we created, then creating an item and adding it to order and scale
     * next in order to compare the order and the scale, we set what the weight should be (5) and then we call  
     * weightDiscrepancy.checkDiscrepancy,the function compares the two values and add assertfalse to ensure 
     * the function is returning the correct answer, here because the two values are the same, no difference in weight
     * @throws OverloadedDevice
     */

    @Test
    public void testcheckDiscrepancy_same() throws OverloadedDevice {
    
    	scale5 = new mockScale(new Mass(6000000),new Mass (6000000));
         PowerGrid grid = PowerGrid.instance();
         scale5.plugIn(grid);
         scale5.turnOn();
         scale5.enable();
         
         Mass mass1 = new Mass(5000000);
         MockItem item1 = new MockItem(mass1); 
         scale5.addAnItem(item1); 
         order5 = new Order(scale5);
      
         order5.addTotalWeightInGrams(5);  
      
         weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);  
         weightDiscrepancy.checkDiscrepancy();
        
        assertFalse(SelfCheckoutStationSoftware.getStationBlock());     
        
    }                
    
    
    /** Create function for check removal, first in the situation that value/order is greater than scale
     * This can be done by adding more items to the order then items being added to scale
     * Then we call the checkDiscrepancy function as there is now a difference between order and scale
     * and then we call check removal and thus assert false as since value is not less than 
     * weightatblockdouble the function returns false
     * @throws OverloadedDevice
     */
    @Test
    public void testCheckRemoval_greaterthan() throws OverloadedDevice {
    	
            
        MockItem item1 = new MockItem(new Mass(100));
        MockItem item2 = new MockItem(new Mass(1000));
        MockItem item3 = new MockItem(new Mass(6000));
        
        order.addItemToOrder(item1); 
        order.addItemToOrder(item2);
        order.addItemToOrder(item3);
        
        scale.addAnItem(item1);
            
        
        weightDiscrepancy.checkDiscrepancy(); 
        
        assertFalse(weightDiscrepancy.checkRemoval());    
    }  

    /** Create a test for ublock test that adds 2 items to an order but only adds one item to the scale 
     * then when we call unblock function, because there is a difference in weight and do assertFalse
     * @throws Exception
     */
	@Test
	public void unBlockTest() throws Exception {
    	scale5 = new mockScale(new Mass(6000000),new Mass (6000000));
        PowerGrid grid = PowerGrid.instance();
        scale5.plugIn(grid);
        scale5.turnOn();
        scale5.enable();
        
      
        order5 = new Order(scale5);
     
		
   
        weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);   
    	
    	 MockItem item1 = new MockItem(new Mass(100));
         MockItem item2 = new MockItem(new Mass(150));
         scale5.addAnItem(item1);
    	
         order5.addItemToOrder(item1);
         order5.addItemToOrder(item2);
        
          
        weightDiscrepancy4.checkIfCanUnblock();
        
        assertFalse(SelfCheckoutStationSoftware.getStationBlock());     
        
            }
	
		
	
	
	/** Create an exception test for the exception that is thrown in the unblock method
	 * hrer we create an item with a large mass so that it surpasses the mass limit and causes exception
	 * to be thrown, then we can do assertTrue to ensure the right exception was thrown
	 * @throws OverloadedDevice
	 */
	@Test
	public void unBlockCatchExceptionTest() throws OverloadedDevice{
        order4 = new Order(eScale);
        weightDiscrepancy4 = new WeightDiscrepancy(order4, eScale);   
    	
        MockItem item2 = new MockItem(new Mass(200000000000000000L));
   
        eScale.addAnItem(item2);
          
        weightDiscrepancy4.checkIfCanUnblock();
        assertTrue(SelfCheckoutStationSoftware.getStationBlock());    
 
            }
		
 
	/**Create function for check removal,in the situation that value/order is less than scale
     * This can be done by creating an item with a mass, adding it to scale but not to order
     * Then we call the checkDiscrepancy function as there is now a difference between order and scale
     * and then we call check removal and thus assert true as since value is  less than 
     * weightatblockdouble the function returns true
     * @throws OverloadedDevice
     */
    @Test
    public void testCheckRemoval_lessthan() throws OverloadedDevice {
            
    	scale5 = new mockScale(new Mass (6000000), new Mass (60000000));
        PowerGrid grid = PowerGrid.instance();
        scale5.plugIn(grid);
        scale5.turnOn();
        scale5.enable();
        
        Mass mass1 = new Mass(5000000);
        MockItem item1 = new MockItem(mass1); 
        scale5.addAnItem(item1); 
        order5 = new Order(scale5);
       
        
        order5.addTotalWeightInGrams(3);  
      
        weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);  
     
        assertTrue(weightDiscrepancy.checkRemoval());
        
    	}
    
    
    /** Create test for check baggage for greater than scenario, here value is greater than weightatblockdouble
     * This means that an item was added to the bagging area, so we create an item with a mass smaller than value and add that to 
     * scale, then we set order to be larger (5)and then when we call check baggage that compares these two values
     * and in this scenario because order is larger return true thus assertTrue
     * @throws Exception
     */
    @Test
    public void testCheckbaggage_greaterthan() throws Exception {
        
    	scale5 = new mockScale(new Mass (6000000), new Mass (60000000));
         PowerGrid grid = PowerGrid.instance();
         scale5.plugIn(grid);
         scale5.turnOn();
         scale5.enable();
         
         Mass mass1 = new Mass(3000000);
         MockItem item1 = new MockItem(mass1); 
         scale5.addAnItem(item1); 
         order5 = new Order(scale5);
        
         
         order5.addTotalWeightInGrams(5);  
       
         weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);  
         
        assertTrue(weightDiscrepancy.checkBaggageAddition()); 
    }  
    
    
    /** Create test for check baggage for less than scenario, here value is less than weightatblockdouble
     * This means that an item is not added to the bagging area, so we create 3 items and all three to the order but only one of them to the scale 
     * then when we call checkBaggage, because value is less then scale function returns false thus assertFalse
     * @throws Exception
     */
    @Test
    public void testCheckbaggage_lessthan() {
   
        MockItem item1 = new MockItem(new Mass(1000));
        MockItem item2 = new MockItem(new Mass(10));
        MockItem item3 = new MockItem(new Mass(60));
        
        order.addItemToOrder(item1); 
        order.addItemToOrder(item2);
        order.addItemToOrder(item3);
        
        scale.addAnItem(item1);
    
        weightDiscrepancy.checkBaggageAddition();
        assertFalse(weightDiscrepancy.checkBaggageAddition());       
        
    }  
    
    
    /** Create test for check weight change for true, here if value is not equal to weightatblockdouble
     * This means that there was a change in bagging area so we create an item with different mass as order and 
     * then when we call the function because values are different return true, thus asserttrue
     * @throws Exception
     */
    @Test
    public void checkWeightChangeTestTrue() throws Exception{
    	scale5 = new mockScale(new Mass (6000000), new Mass (60000000));
         PowerGrid grid = PowerGrid.instance();
         scale5.plugIn(grid);
         scale5.turnOn();
         scale5.enable();
         
         Mass mass1 = new Mass(5000000);
         MockItem item1 = new MockItem(mass1); 
         scale5.addAnItem(item1); 
         order5 = new Order(scale5);
        
         
         order5.addTotalWeightInGrams(3);  
       
         weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);  
        assertTrue(weightDiscrepancy.checkWeightChange());    
    }
    
    
    /** Create test for check weight change for false, here if value is equal to weightatblockdouble
     * This means that there is no change in bagging area so we here order will be same as mass
     * then when we call the function and because values are same,returns false then do assertfalse 
     * @throws Exception
     */
  	@Test
    public void checkWeightChangeTestFalse() throws Exception{
        
  		scale5 = new mockScale(new Mass (6000000), new Mass (60000000));
        PowerGrid grid = PowerGrid.instance();
        scale5.plugIn(grid);
        scale5.turnOn();
        scale5.enable();
        
        Mass mass1 = new Mass(5000000);
        MockItem item1 = new MockItem(mass1); 
        scale5.addAnItem(item1); 
        order5 = new Order(scale5);
    
        order5.addTotalWeightInGrams(5);  
      
        weightDiscrepancy4 = new WeightDiscrepancy(order5, scale5);  
        
        assertFalse(weightDiscrepancy.checkWeightChange());    
        
    } 

  	

  	/** Create test that checks if the weight has changed, so create an item add to order and scale
  	 * and call function thus testing that the weight has changed */
//  	@Test
//  	public void notifymasschange_blocked() {
//  		MockItem item1 = new MockItem(new Mass(1000));
//  		order.addItemToOrder(item1); 
//        scale.addAnItem(item1);
//        weightDiscrepancy.notifyMassChanged();    
//  	} 
    
  	/** Create Test for teststation block first for true that checks that if in weight discrepancy class
  	 * the function pass true then SelfCheckoutStationSoftware should also return true */
  	@Test
  	public void teststationblock_true() {
  		WeightDiscrepancy.setStationBlock(true);
  		assertTrue(SelfCheckoutStationSoftware.getStationBlock());
  		
  	}
  	
  	/** Create Test for teststation block for false that checks that if in weight discrepancy class
  	 * the function pass false then SelfCheckoutStationSoftware should also return false */
  	@Test
  	public void teststationblock_false() {
  		WeightDiscrepancy.setStationBlock(false);
  		assertFalse(SelfCheckoutStationSoftware.getStationBlock());
  		
  	}  
  	
  	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{new ElectronicScaleGold()},
			{new ElectronicScaleSilver()},
			{new ElectronicScaleBronze()}
		});
	}

	public WeightDiscrepancyTest(AbstractElectronicScale eScale) {
		this.eScale = eScale;
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
    	Order order = new Order(eScale);
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
   		Order order = new Order(eScale);
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
   		Order order = new Order(eScale);
   		
   		WeightDiscrepancy.handleBulkyItem(order, 79);
   		
   		double expectedTotalWeight = 0;
        assertEquals(expectedTotalWeight, order.getTotalWeightInGrams(), 0);
   		
   	}
   	
   	@Test
   	public void testHandleMultipleBulkyItems() throws OverloadedDevice {
   		Order order = new Order(eScale);
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