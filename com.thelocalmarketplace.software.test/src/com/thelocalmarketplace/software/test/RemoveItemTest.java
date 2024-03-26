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

import static org.junit.Assert.*;

import com.jjjwelectronics.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.BaggingAreaListener;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class RemoveItemTest {
	PowerGrid grid;
	private Order orderBronze;
	private Order orderGold;
	private Order orderSilver;
	private ElectronicScaleBronze scaleBronze;
	private ElectronicScaleGold scaleGold;
	private ElectronicScaleSilver scaleSilver;
	WeightDiscrepancy weightDiscrepancyBronze;
	WeightDiscrepancy weightDiscrepancyGold;
	WeightDiscrepancy weightDiscrepancySilver;
	BaggingAreaListener baggingAreaListenerBronze;
	BaggingAreaListener baggingAreaListenerGold;
	BaggingAreaListener baggingAreaListenerSilver;
	BarcodedItem barcodedItem;
	BarcodedProduct barcodedProduct;

	@Before
	public void setup() throws OverloadedDevice{
		// start session
		SelfCheckoutStationSoftware.setStationActive(true);

		// create a power grid
		grid = PowerGrid.instance();
		// to avoid power outages when there is a power surge
		PowerGrid.engageUninterruptiblePowerSource();
		grid.forcePowerRestore();

		// set up the scales for bronze, gold and silver
		scaleBronze = new ElectronicScaleBronze();
		scaleBronze.plugIn(grid);
		scaleBronze.turnOn();
		scaleBronze.enable();
		
		scaleSilver = new ElectronicScaleSilver();
		scaleSilver.plugIn(grid);
		scaleSilver.turnOn();
		scaleSilver.enable();

		scaleGold = new ElectronicScaleGold();
		scaleGold.plugIn(grid);
		scaleGold.turnOn();
		scaleGold.enable();

		// Initialize a mock barcoded item that will be added to each order
		Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five};
		Barcode barcode = new Barcode(barcodeDigits);
		Mass itemMass = new Mass(1000000000); // 1kg in micrograms
		barcodedItem = new BarcodedItem(barcode, itemMass);

		// Initializing mock product (using same barcode as the barcoded item)
		String productDescription = "test product";
		long productPrice = 5;
		double productWeightInGrams = 1000;
		barcodedProduct = new BarcodedProduct(barcode, productDescription, productPrice, productWeightInGrams);

		// Adding mock product into product database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);

		// initializing orders
		orderBronze = new Order(scaleBronze);
		orderGold = new Order(scaleGold);
		orderSilver = new Order(scaleSilver);

		// initialize WeightDiscrepancy
		weightDiscrepancyBronze = new WeightDiscrepancy(orderBronze, scaleBronze);
		weightDiscrepancyGold = new WeightDiscrepancy(orderGold, scaleGold);
		weightDiscrepancySilver = new WeightDiscrepancy(orderSilver, scaleSilver);

		// initialize BaggingAreaListeners and make it listen to the scale objects
		baggingAreaListenerBronze = new BaggingAreaListener(orderBronze);
		scaleBronze.register(baggingAreaListenerBronze);

		baggingAreaListenerGold = new BaggingAreaListener(orderGold);
		scaleGold.register(baggingAreaListenerGold);

		baggingAreaListenerSilver = new BaggingAreaListener(orderSilver);
		scaleBronze.register(baggingAreaListenerSilver);

		// adding a barcodedItem to the order
		orderBronze.addItemToOrder(barcodedItem);
		// add to total weight in the order
		orderBronze.addTotalWeightInGrams(productWeightInGrams);
		// add to the total price in the order
		orderBronze.addTotalPrice(productPrice);
		// add the item to the scale
		scaleBronze.addAnItem(barcodedItem);

		orderGold.addItemToOrder(barcodedItem);
		orderGold.addTotalWeightInGrams(productWeightInGrams);
		orderGold.addTotalPrice(productPrice);
		scaleGold.addAnItem(barcodedItem);

		orderSilver.addItemToOrder(barcodedItem);
		orderSilver.addTotalWeightInGrams(productWeightInGrams);
		orderSilver.addTotalPrice(productPrice);
		scaleSilver.addAnItem(barcodedItem);

		Scanner input = new Scanner(System.in);
	}

	// test that the weight in the order changed after the item is removed from order
	@Test
	public void testWeightAfterRemovingItemFromOrderBronze() {
		double prevWeight = orderBronze.getTotalWeightInGrams();
		orderBronze.removeItemFromOrder(barcodedItem);
		double newWeight = orderBronze.getTotalWeightInGrams();
		assertTrue(prevWeight > newWeight);
	}

	@Test
	public void testWeightAfterRemovingItemFromOrderGold() {
		double prevWeight = orderGold.getTotalWeightInGrams();
		orderGold.removeItemFromOrder(barcodedItem);
		double newWeight = orderGold.getTotalWeightInGrams();
		assertTrue(prevWeight > newWeight);
	}

	@Test
	public void testWeightAfterRemovingItemFromOrderSilver() {
		double prevWeight = orderSilver.getTotalWeightInGrams();
		orderSilver.removeItemFromOrder(barcodedItem);
		double newWeight = orderSilver.getTotalWeightInGrams();
		assertTrue(prevWeight > newWeight);
	}

	// test that the total price of the order decreases when an item is removed from the order
	@Test
	public void testDecreaseInPriceForOrderBronze() {
		long prevPrice = orderBronze.getTotalPrice();
		orderBronze.removeItemFromOrder(barcodedItem);
		long newPrice = orderBronze.getTotalPrice();

		assertTrue(prevPrice > newPrice);
	}

	@Test
	public void testDecreaseInPriceForOrderGold() {
		long prevPrice = orderGold.getTotalPrice();
		orderGold.removeItemFromOrder(barcodedItem);
		long newPrice = orderGold.getTotalPrice();

		assertTrue(prevPrice > newPrice);
	}

	@Test
	public void testDecreaseInPriceForOrderSilver() {
		long prevPrice = orderSilver.getTotalPrice();
		orderSilver.removeItemFromOrder(barcodedItem);
		long newPrice = orderSilver.getTotalPrice();

		assertTrue(prevPrice > newPrice);
	}

	// test that the weight decreases when the item is removed from the scale
	@Test
	public void testWeightChangedOnScaleBronze() throws OverloadedDevice {
		Mass prevMassOnScale = scaleBronze.getCurrentMassOnTheScale();
		scaleBronze.removeAnItem(barcodedItem);
		Mass currMassOnScale = scaleBronze.getCurrentMassOnTheScale();

		// comparing currMassOnScale with prevMassOnScale should return -1, since currMassOnScale < prevMassOnScale
		assertEquals(currMassOnScale.compareTo(prevMassOnScale), -1);
	}

	@Test
	public void testWeightChangedOnScaleGold() throws OverloadedDevice {
		Mass prevMassOnScale = scaleGold.getCurrentMassOnTheScale();
		scaleGold.removeAnItem(barcodedItem);
		Mass currMassOnScale = scaleGold.getCurrentMassOnTheScale();

		// comparing currMassOnScale with prevMassOnScale should return -1, since currMassOnScale < prevMassOnScale
		assertEquals(currMassOnScale.compareTo(prevMassOnScale), -1);
	}

	@Test
	public void testWeightChangedOnScaleSilver() throws OverloadedDevice {
		Mass prevMassOnScale = scaleSilver.getCurrentMassOnTheScale();
		scaleSilver.removeAnItem(barcodedItem);
		Mass currMassOnScale = scaleSilver.getCurrentMassOnTheScale();

		// comparing currMassOnScale with prevMassOnScale should return -1, since currMassOnScale < prevMassOnScale
		assertEquals(currMassOnScale.compareTo(prevMassOnScale), -1);
	}


	// test that item is not removed when order is empty
	@Test
	public void testRemoveItemWhenOrderBronzeIsEmpty() throws OverloadedDevice{
		orderBronze = new Order(scaleBronze);
		// removeItemFromOrder should return false since the order is empty
		assertFalse(orderBronze.removeItemFromOrder(barcodedItem));
	}

	@Test
	public void testRemoveItemWhenOrderGoldIsEmpty() throws OverloadedDevice{
		orderGold = new Order(scaleGold);
		// removeItemFromOrder should return false since the order is empty
		assertFalse(orderGold.removeItemFromOrder(barcodedItem));
	}

	@Test
	public void testRemoveItemWhenOrderSilverIsEmpty() throws OverloadedDevice{
		orderSilver = new Order(scaleSilver);
		// removeItemFromOrder should return false since the order is empty
		assertFalse(orderSilver.removeItemFromOrder(barcodedItem));
	}

	@Test
	public void testSignalToRemoveItemFromOrderBronze() throws OverloadedDevice {
		ArrayList<Item> orderBeforeRemoval = orderBronze.getOrder(); // get the order list before removal

		int lengthBefore = orderBeforeRemoval.size(); // get the length of the order list before removal

		
		String inputData = "1"; // remove the first item in the order
		System.setIn(new java.io.ByteArrayInputStream(inputData.getBytes()));
		Scanner testInput = new Scanner(System.in);
		orderBronze.signalToRemoveItemFromOrder(testInput); // remove the item from the order

		ArrayList<Item> orderAfterRemoval = orderBronze.getOrder();
		int lengthAfter = orderAfterRemoval.size();

		assertEquals(lengthAfter, lengthBefore - 1);
	}

	@Test
    	public void testRemovingItemWithNullProductInDatabaseGold() {
	 	Mass itemMass = new Mass(1000000000); // 1kg in micrograms
        	Barcode nullProductBarcode = new Barcode(new Numeral[]{Numeral.six, Numeral.seven, Numeral.eight, Numeral.nine, Numeral.zero});
        	BarcodedItem itemWithNullProduct = new BarcodedItem(nullProductBarcode, itemMass);
        	orderGold.addItemToOrder(itemWithNullProduct);
        	assertTrue("The item should be removed even if the product is null in the database", orderGold.removeItemFromOrder(itemWithNullProduct));
    	}
	@Test
    	public void testRemovingItemWithNullProductInDatabaseSilver() {
	 	Mass itemMass = new Mass(1000000000); // 1kg in micrograms
        	Barcode nullProductBarcode = new Barcode(new Numeral[]{Numeral.six, Numeral.seven, Numeral.eight, Numeral.nine, Numeral.zero});
        	BarcodedItem itemWithNullProduct = new BarcodedItem(nullProductBarcode, itemMass);
        	orderSilver.addItemToOrder(itemWithNullProduct);
        	assertTrue("The item should be removed even if the product is null in the database", orderSilver.removeItemFromOrder(itemWithNullProduct));
    	}
	@Test
    	public void testRemovingItemWithNullProductInDatabaseBronze() {
	 	Mass itemMass = new Mass(1000000000); // 1kg in micrograms
        	Barcode nullProductBarcode = new Barcode(new Numeral[]{Numeral.six, Numeral.seven, Numeral.eight, Numeral.nine, Numeral.zero});
        	BarcodedItem itemWithNullProduct = new BarcodedItem(nullProductBarcode, itemMass);
        	orderBronze.addItemToOrder(itemWithNullProduct);
        	assertTrue("The item should be removed even if the product is null in the database", orderBronze.removeItemFromOrder(itemWithNullProduct));
    	}

	@Test
   	 public void testRemovingLastItemGold() {
        	orderGold.removeItemFromOrder(barcodedItem); // Remove the initial item first
        	orderGold.addItemToOrder(barcodedItem); // Add it back to have only one item in the order
        	assertTrue("The last item should be removed successfully", orderGold.removeItemFromOrder(barcodedItem));
	}
	@Test
    	public void testRemovingLastItemSilver() {
        	orderSilver.removeItemFromOrder(barcodedItem); // Remove the initial item first
        	orderSilver.addItemToOrder(barcodedItem); // Add it back to have only one item in the order
        	assertTrue("The last item should be removed successfully", orderSilver.removeItemFromOrder(barcodedItem));
	}
	@Test
    	public void testRemovingLastItemBronze() {
        	orderBronze.removeItemFromOrder(barcodedItem); // Remove the initial item first
        	orderBronze.addItemToOrder(barcodedItem); // Add it back to have only one item in the order
        	assertTrue("The last item should be removed successfully", orderBronze.removeItemFromOrder(barcodedItem));
	}

	@After
	public void tearDown() {
		// deregister BaggingAreaListeners
		scaleBronze.deregister(baggingAreaListenerBronze);

		scaleGold.deregister(baggingAreaListenerGold);
		scaleSilver.deregister(baggingAreaListenerBronze);

		// disable, turnOff and unplug scales for all bronze, silver and gold
		scaleBronze.disable();
		scaleBronze.turnOff();
		scaleBronze.unplug();
		
		scaleSilver.disable();
		scaleSilver.turnOff();
		scaleSilver.unplug();

		scaleGold.disable();
		scaleGold.turnOff();
		scaleGold.unplug();

		
	}

}