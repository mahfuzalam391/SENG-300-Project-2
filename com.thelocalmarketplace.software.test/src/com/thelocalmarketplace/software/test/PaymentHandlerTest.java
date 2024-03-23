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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.PaymentHandler;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.OutOfInkException;
import com.thelocalmarketplace.software.OutOfPaperException;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

public class PaymentHandlerTest {
	private SelfCheckoutStationGold checkoutStation;
    private ArrayList<Product> coinsList;
    private Coin coin1, coin2;
    private Banknote banknote1, banknote2;
    private BigDecimal totalCost;
    private PaymentHandler paymentHandler;
    private BarcodedItem barcodedItem;
    private BarcodedProduct barcodedProduct;
    private PLUCodedItem pluCodeItem;
    private PLUCodedProduct pluProduct;
    private ElectronicScaleBronze baggingArea;
    private Order testOrder;
    private PLUCodedProduct pluCodedProduct;

    @Before
    public void setUp() throws OverloadedDevice, EmptyDevice {
        // Mock SelfCheckoutStation and its components as needed
    	SelfCheckoutStationGold.resetConfigurationToDefaults();
    	BigDecimal[] coinDenominations = {new BigDecimal("0.25"), new BigDecimal("0.10"), new BigDecimal("0.50"), new BigDecimal("1.0")};
    	SelfCheckoutStationGold.configureCoinDenominations(coinDenominations);
    	BigDecimal[] bankNoteDenominations = {new BigDecimal("5.0"),new BigDecimal("10.0"), new BigDecimal("20.0"), new BigDecimal("50.0")};
    	SelfCheckoutStationGold.configureBanknoteDenominations(bankNoteDenominations);
    	SelfCheckoutStationGold.configureCurrency(Currency.getInstance("CAD"));
    	checkoutStation = new SelfCheckoutStationGold();

        baggingArea = new ElectronicScaleBronze();
        // Initializing mock barcoded item
 		Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five};
 		Barcode barcode = new Barcode(barcodeDigits);
 		Mass itemMass = new Mass(1000000000); // 1kg in micrograms
 		barcodedItem = new BarcodedItem(barcode, itemMass);

 		// Initializing mock product (using same barcode as the barcoded item)
 		String productDescription = "banana";
 		long productPrice = 5;
 		double productWeightInGrams = 1000;
 		barcodedProduct = new BarcodedProduct(barcode, productDescription, productPrice, productWeightInGrams);

 		// Adding mock product into product database
 		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);

         // Initializing mock PLU-coded item
        String pluDigits = "0001";
        PriceLookUpCode pluCode = new PriceLookUpCode(pluDigits);
        Mass mass = new Mass(1000000000); // Converts the weight of the product to a mass
        pluCodeItem = new PLUCodedItem(pluCode, mass);

        // Initializing mock product (using same PLU code as the PLU-coded item)

        String pluCodeProductDescription = "orange";
        long pluCodeProductPrice = 10;
        double pluCodeproductWeightInGrams = 1000;
        pluProduct = new PLUCodedProduct(pluCode, pluCodeProductDescription, pluCodeProductPrice);


        // Adding mock product into product database
        ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, pluProduct);

        // Initializing testOrder
 		testOrder = new Order(baggingArea);
 		testOrder.addItemViaBarcodeScan(barcode);
         
        paymentHandler = new PaymentHandler((SelfCheckoutStationGold)checkoutStation, testOrder);

        paymentHandler.getStation().coinStorage.connect(PowerGrid.instance());
        paymentHandler.getStation().coinStorage.activate();
        paymentHandler.getStation().coinSlot.connect(PowerGrid.instance());
        paymentHandler.getStation().coinSlot.activate();
        paymentHandler.getStation().coinValidator.connect(PowerGrid.instance());
        paymentHandler.getStation().coinValidator.activate();
        PowerGrid.engageUninterruptiblePowerSource();
        PowerGrid.instance().forcePowerRestore();
    }
    
    @After
    public void teardown() {
    	paymentHandler = null;
    	checkoutStation = null;
 
    }

    @Test (expected = NullPointerSimulationException.class)
    public void testPrinterIfOrderIsNull() throws OutOfPaperException, OutOfInkException, EmptyDevice, OverloadedDevice {
        paymentHandler.receiptPrinter(null);
    }

    @Test
    public void testReceiptPrinterWithBarcodedProduct() throws OutOfPaperException, OutOfInkException, EmptyDevice, OverloadedDevice {


        baggingArea = new ElectronicScaleBronze();
        // Initializing mock barcoded item
        Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five};
        Barcode barcode = new Barcode(barcodeDigits);
        Mass itemMass = new Mass(1000000000); // 1kg in micrograms
        barcodedItem = new BarcodedItem(barcode, itemMass);

        // Initializing mock product (using same barcode as the barcoded item)
        String barcodeProductDescription = "banana";
        long barcodeProductPrice = 5;
        double barcodeProductWeightInGrams = 1000;
        barcodedProduct = new BarcodedProduct(barcode, barcodeProductDescription, barcodeProductPrice, barcodeProductWeightInGrams);

        // Adding mock product into product database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
        testOrder = new Order(baggingArea);
        testOrder.addItemViaBarcodeScan(barcode);

        paymentHandler = new PaymentHandler(checkoutStation, testOrder);
        paymentHandler.amountSpent = BigDecimal.valueOf(5); // Set amount spent for testing
        paymentHandler.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing


        paymentHandler.receiptPrinter(testOrder);
    }

    @Test
    public void testReceiptPrinterWithPLUProduct() throws OutOfPaperException, OutOfInkException, EmptyDevice, OverloadedDevice {


        baggingArea = new ElectronicScaleBronze();

        String pluDigits = "0001";
        PriceLookUpCode pluCode = new PriceLookUpCode(pluDigits);
        Mass mass = new Mass(1000000000); // Converts the weight of the product to a mass
        pluCodeItem = new PLUCodedItem(pluCode, mass);


        String pluCodeProductDescription = "orange";
        long pluCodeProductPrice = 10;
        double pluCodeproductWeightInGrams = 1000;


        pluProduct = new PLUCodedProduct(pluCode, pluCodeProductDescription, pluCodeProductPrice);

        ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, pluProduct);


        testOrder.addItemToOrder(pluCodeItem);
        paymentHandler.receiptPrinter(testOrder);


        paymentHandler = new PaymentHandler(checkoutStation, testOrder);
        paymentHandler.amountSpent = BigDecimal.valueOf(10); // Set amount spent for testing
        paymentHandler.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing

        paymentHandler.receiptPrinter(testOrder);
    }

    @Test (expected = NullPointerException.class)
    public void testReceiptPrinterWithUnsupportedProduct() throws OutOfPaperException, OutOfInkException, EmptyDevice, OverloadedDevice {
        testOrder.addItemToOrder(null);
        paymentHandler.receiptPrinter(testOrder);
    }

    @Test (expected = OutOfPaperException.class)
    public void testReceiptPrinterOutOfPaper() throws EmptyDevice, OverloadedDevice, OutOfPaperException, OutOfInkException {
        baggingArea = new ElectronicScaleBronze();
        // Initializing mock barcoded item
        Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five};
        Barcode barcode = new Barcode(barcodeDigits);
        Mass itemMass = new Mass(1000000000); // 1kg in micrograms
        barcodedItem = new BarcodedItem(barcode, itemMass);

        // Initializing mock product (using same barcode as the barcoded item)
        String barcodeProductDescription = "banana";
        long barcodeProductPrice = 5;
        double barcodeProductWeightInGrams = 1000;
        barcodedProduct = new BarcodedProduct(barcode, barcodeProductDescription, barcodeProductPrice, barcodeProductWeightInGrams);

        // Adding mock product into product database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
        testOrder = new Order(baggingArea);


        for (int i = 0; i < ReceiptPrinterBronze.MAXIMUM_PAPER + 1; i++) {
            testOrder.addItemViaBarcodeScan(barcode);

        }

        paymentHandler.receiptPrinter(testOrder);


    }

    @Test (expected = OutOfInkException.class)
    public void testReceiptPrinterOutOfInk() throws EmptyDevice, OverloadedDevice, OutOfPaperException, OutOfInkException {

        baggingArea = new ElectronicScaleBronze();
        // Initializing mock barcoded item
        Numeral[] barcodeDigits = {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five};
        Barcode barcode = new Barcode(barcodeDigits);
        Mass itemMass = new Mass(1000000000); // 1kg in micrograms
        barcodedItem = new BarcodedItem(barcode, itemMass);

        // Initializing mock product (using same barcode as the barcoded item)
        String barcodeProductDescription = "banana";
        long barcodeProductPrice = 5;
        double barcodeProductWeightInGrams = 1000;
        barcodedProduct = new BarcodedProduct(barcode, barcodeProductDescription, barcodeProductPrice, barcodeProductWeightInGrams);

        // Adding mock product into product database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
        testOrder = new Order(baggingArea);


        for (int i = 0; i < ReceiptPrinterBronze.MAXIMUM_PAPER + 1; i++) {
            testOrder.addItemViaBarcodeScan(barcode);

        }

        paymentHandler.receiptPrinter(testOrder);

    }


//    @Test
//    public void testReceiptPrinter() throws Exception{
//        // Mocking System.out for testing output
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        paymentHandler = new PaymentHandler(checkoutStation, testOrder);
//        paymentHandler.amountSpent = BigDecimal.valueOf(5); // Set amount spent for testing
//        paymentHandler.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing
//
//        paymentHandler.receiptPrinter(testOrder);
//
//        // Check if the receipt contains correct information
//        assertTrue(outContent.toString().contains("banana $5.00"));
//        assertTrue(outContent.toString().contains("Total: $5.00"));
//        assertTrue(outContent.toString().contains("Paid: $5.00"));
//        assertTrue(outContent.toString().contains("Change: $0.00"));
//
//        // Reset System.out
//        System.setOut(System.out);
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void testReceiptPrinterIncorrectProduct() throws Exception{
//        // Mocking System.out for testing output
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//
//        paymentHandler = new PaymentHandler(checkoutStation, testOrder);
//
//        Numeral[] barcodeDigits = {Numeral.zero, Numeral.two, Numeral.three};
// 		Barcode barcode = new Barcode(barcodeDigits);
// 		Mass mass = new Mass(3); // Converts the weight of the product to a mass
// 		BarcodedItem barcodedItem = new BarcodedItem(barcode, mass);
//        testOrder.addItemToOrder(barcodedItem);
//
//        paymentHandler.receiptPrinter(testOrder);
//	    assertTrue(outContent.toString().contains("This product is not a supported product, can not be registered for a price"));
//
//        // Reset System.out
//        System.setOut(System.out);
//    }
//
//    @Test(expected = OutOfPaperException.class)
//    public void testReceiptPrinterOutOfPaperException() throws Exception{
//    	// Mocking System.out for testing output
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//        paymentHandler.paperSpaceCounter = 0;
//        paymentHandler.inkCounter = 10;
//
//	    // Check if the out of ink exception is thrown
//	    paymentHandler.receiptPrinter(testOrder); // Should throw outOfInkException
//	    assertTrue(outContent.toString().contains("The printer is out of Paper currently, needs maintenance."));
//
//	    // Reset System.out
//	    System.setOut(System.out);
//    }
//
//    @Test(expected = OutOfInkException.class)
//    public void testReceiptPrinterOutOfInkException() throws Exception{
//    	// Mocking System.out for testing output
//        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outContent));
//        paymentHandler.inkCounter = 0;
//        paymentHandler.paperSpaceCounter = 100;
//
//	    // Check if the out of ink exception is thrown
//	    paymentHandler.receiptPrinter(testOrder); // Should throw outOfInkException
//	    assertTrue(outContent.toString().contains("The printer is out of Ink currently, needs maintenance."));
//
//	    // Reset System.out
//	    System.setOut(System.out);
//    }
 
    @Test(expected = NullPointerException.class)
    public void constructor_NullStation_ThrowsException() throws OverloadedDevice, EmptyDevice {
    	checkoutStation = null;
    	new PaymentHandler(checkoutStation, new Order(null));
    }

    @Test
    public void getChangeRemainingTest() throws Exception {
        // Simulate exact payment
        assertEquals(paymentHandler.getChangeRemaining(), BigDecimal.ZERO);
    }
    
    @Test
    public void processPaymentWithCoinsTestWithOverpayment() throws Exception { //there is a probelm here 
        // Simulate sufficient payment
    	Currency currency = Currency.getInstance("CAD");
    	checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.totalCost = new BigDecimal("0.25");
        
    	ArrayList<Coin> coinsList = new ArrayList<Coin>();
    	
    	coin1 = new Coin(currency,new BigDecimal("0.10"));
        coin2 = new Coin(currency,BigDecimal.valueOf(0.25));

        coinsList.add(coin1);
        coinsList.add(coin2);
        
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, new BigDecimal("0.25")));
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, new BigDecimal("0.10")));
        
        assertTrue(paymentHandler.processPaymentWithCoins(coinsList));
    }
    
    @Test
    public void processPaymentWithCoinsTestWithOverpaymentNoDispense() throws Exception { //there is a probelm here 
        // Simulate sufficient payment
    	Currency currency = Currency.getInstance("CAD");
    	checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.totalCost = new BigDecimal("0.25");
        
    	ArrayList<Coin> coinsList = new ArrayList<Coin>();
    	
    	coin1 = new Coin(currency,new BigDecimal("1.0"));
        coin2 = new Coin(currency,new BigDecimal("0.25"));

        coinsList.add(coin1);
        coinsList.add(coin2);
        
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, new BigDecimal("0.25")));
        
        assertFalse(paymentHandler.processPaymentWithCoins(coinsList));
    }
    
    @Test
    public void processPaymentWithCoinsTestWithUnderpayment() throws Exception {
        // Simulate insufficient payment
    	
    	ArrayList<Coin> coinsList = new ArrayList<Coin>();
    	Currency currency = Currency.getInstance("CAD");
    	
    	coin1 = new Coin(currency,new BigDecimal("1.00"));
        coin2 = new Coin(currency,new BigDecimal("2.00"));

        coinsList.add(coin1);
        coinsList.add(coin2);
        
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        
        assertFalse(paymentHandler.processPaymentWithCoins(coinsList));
    }
    
    
    @Test
    public void testProcessPaymentWithExactAmount() throws Exception {
    	ArrayList<Coin> coinsList = new ArrayList<Coin>();
    	Currency currency = Currency.getInstance("CAD");
    	
    	coin1 = new Coin(currency,new BigDecimal("10.00"));
        coin2 = new Coin(currency,new BigDecimal("2.00"));

        coinsList.add(coin1);
        coinsList.add(coin2);
        
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        
        paymentHandler.totalCost = new BigDecimal("12.0");
        
        assertTrue("Payment should succeed with exact amount", paymentHandler.processPaymentWithCoins(coinsList));
    }

    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullCoinsList() throws Exception {
        paymentHandler.processPaymentWithCoins(null); // This should throw NullPointerException
    }
    
    /**
     * Test that the coin storage is empty
     * @throws SimulationException
     * @throws CashOverloadException
     */
    @Test
    public void testEmptyCoinStorage() throws SimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
    	Currency currency = Currency.getInstance("CAD");
        coin1 = new Coin(currency, new BigDecimal("1.00"));
        paymentHandler.getStation().coinStorage.load(coin1);
        paymentHandler.getStation().coinStorage.unload();
    }
    
   /**
     * Checks if coins are actually loaded in the coin dispenser
     * @throws CashOverloadException
     */
    @Test
    public void testLoadCoinDispenser() throws CashOverloadException {
    	Currency currency = Currency.getInstance("CAD");
    	// Prepare some coins
        Coin coin1 = new Coin(currency, BigDecimal.valueOf(0.25));
        Coin coin2 = new Coin(currency, new BigDecimal("0.10"));

        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();

        paymentHandler.loadCoinDispenser(coin1, coin2);
        assertTrue(checkoutStation.coinDispensers.get(BigDecimal.valueOf(0.25)).size() == 1);
        assertTrue(checkoutStation.coinDispensers.get(new BigDecimal("0.10")).size() == 1);
    }
    
    /**
     * Test for CashOverloadException
     * @throws CashOverloadException
     * @throws OverloadedDevice 
     * @throws EmptyDevice 
     */
    @Test (expected = CashOverloadException.class)
    public void testLoadCoinDispenserOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
    	Currency currency = Currency.getInstance("CAD");
    	// Prepare some coins
        Coin coin1 = new Coin(currency, new BigDecimal("0.10"));
        Coin coin2 = new Coin(currency, new BigDecimal("0.10"));

        // Load coins into dispenser
        this.checkoutStation.configureCoinDispenserCapacity(2);
        this.checkoutStation = new SelfCheckoutStationGold();
        paymentHandler = new PaymentHandler(checkoutStation, testOrder);
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.loadCoinDispenser(coin1, coin2);
    	
        //should throw overlaod error on this load
		Coin c = new Coin(currency, new BigDecimal("0.10"));
		paymentHandler.loadCoinDispenser(c);
    }
    
    /**
     * Test for NullPointerException when there is no coindispenser for a specific denomination of a coin
     * @throws OverloadedDevice 
     * @throws EmptyDevice 
     * @throws NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testLoadCoinDispenserCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
    	Currency currency = Currency.getInstance("CAD");
    	// Prepare some coins
        Coin coin1 = new Coin(currency, BigDecimal.valueOf(0.27));
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandler.loadCoinDispenser(coin1);
    }

    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandler.loadCoinDispenser(null);	
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
    	Currency currency = Currency.getInstance("CAD");
        coin1 = new Coin(currency, new BigDecimal("1.00"));
        coin2 = new Coin(currency, new BigDecimal("2.00"));
        Coin coin3 = null;
        paymentHandler.loadCoinDispenser(coin3,coin1,coin2);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullStation() throws OverloadedDevice, EmptyDevice {
    	checkoutStation = null;
        new PaymentHandler(checkoutStation, new Order(null));
    }

    @Test
    public void testDispenseAccurateChange() throws Exception {
    	Currency currency = Currency.getInstance("CAD");

        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")));
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, BigDecimal.valueOf(0.25)),
        		new Coin(currency, BigDecimal.valueOf(0.25)),
        		new Coin(currency, BigDecimal.valueOf(0.25)),
        		new Coin(currency, BigDecimal.valueOf(0.25)));
        
        // Testing accurate change dispensing
        assertTrue(paymentHandler.dispenseAccurateChange(BigDecimal.valueOf(1.30)));
        assertEquals(0, checkoutStation.coinDispensers.get(BigDecimal.valueOf(0.25)).size());
        assertEquals(2, checkoutStation.coinDispensers.get(new BigDecimal("0.10")).size());
    }
    
    @Test
    public void testDispenseAccurateChangeLowestCoin() throws Exception {
    	Currency currency = Currency.getInstance("CAD");

        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")),
        		new Coin(currency, new BigDecimal("0.10")));
        paymentHandler.loadCoinDispenser(
        		new Coin(currency, new BigDecimal("0.25")),
        		new Coin(currency, new BigDecimal("0.25")),
        		new Coin(currency, new BigDecimal("0.25")),
        		new Coin(currency, new BigDecimal("0.25")));
        
        // Testing accurate change dispensing
        assertTrue(paymentHandler.dispenseAccurateChange(new BigDecimal("1.35")));
        assertEquals(0, checkoutStation.coinDispensers.get(new BigDecimal("0.25")).size());
        assertEquals(1, checkoutStation.coinDispensers.get(new BigDecimal("0.10")).size());
    }
    
    
 
 // Tests for acceptInsertedCoin
    
 // Tests whether valid coins will be inserted if the checkout station's storage unit has space
    @Test
    public void testInsertValidCoinsIfEnoughSpace() throws DisabledException, CashOverloadException {
        System.out.println(paymentHandler.getStation().coinStorage.getCapacity());
        Coin coin1 = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05"));
        Coin coin2 = new Coin(Currency.getInstance("USD"), new BigDecimal("1.00"));
        assertTrue(paymentHandler.getStation().coinStorage.hasSpace());
        assertTrue(paymentHandler.acceptInsertedCoin(coin1));
        assertTrue(paymentHandler.acceptInsertedCoin(coin2));

    }

    // Tests whether valid coins inserted into a checkout station with no space will disable the coin slot
    @Test (expected = CashOverloadException.class)
    public void testInsertValidCoinsIfNoSpace() throws DisabledException, CashOverloadException {
        System.out.println(paymentHandler.getStation().coinStorage.getCapacity());
        Coin coin = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05"));
        assertTrue(paymentHandler.getStation().coinStorage.hasSpace());
        for (int i = 0; i < 3000; i++) {
            paymentHandler.acceptInsertedCoin(coin);
        }
        assertFalse(paymentHandler.getStation().coinStorage.hasSpace());
        assertFalse(paymentHandler.acceptInsertedCoin(coin));

    }
    
    // test process payment with banknote
    
    
    @Test
    public void processPaymentWithBanknotesTestWithOverpayment() throws Exception { 
    	
    	Currency currency = Currency.getInstance("CAD");


        paymentHandler = new PaymentHandler(checkoutStation, testOrder);
    	checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.totalCost = new BigDecimal("20.0");
        
    	ArrayList<Banknote> notesList = new ArrayList<Banknote>();
    	
    	banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("20.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);
        
        paymentHandler.loadBanknoteDispenser(
        		new Banknote(currency, new BigDecimal("10.0")));
        paymentHandler.loadBanknoteDispenser(
        		new Banknote(currency, new BigDecimal("20.0")));
        
        assertTrue(paymentHandler.processPaymentWithBanknotes(notesList));
    }
    
    @Test
    public void processPaymentWithBanknotesTestWithOverpaymentNoDispense() throws Exception { 
    	
    	Currency currency = Currency.getInstance("CAD");

    	checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.totalCost = new BigDecimal("20.0");
        
    	ArrayList<Banknote> notesList = new ArrayList<Banknote>();
    	
    	banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("20.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);
        
        paymentHandler.loadBanknoteDispenser(
        		new Banknote(currency, new BigDecimal("20.0")));
        
        assertFalse(paymentHandler.processPaymentWithBanknotes(notesList));
    }
    
 
    @Test
    public void processPaymentWithBanknotesTestWithUnderpayment() throws Exception {
        // Simulate insufficient payment
    	
    	Currency currency = Currency.getInstance("CAD");

    	checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.totalCost = new BigDecimal("15.0");
    	
    	
    	ArrayList<Banknote> notesList = new ArrayList<Banknote>();
    	
    	banknote1 = new Banknote(currency,new BigDecimal("5.0"));
        banknote2 = new Banknote(currency,new BigDecimal("5.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);
        
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        
        assertFalse(paymentHandler.processPaymentWithBanknotes(notesList));
    }
    
    
    @Test
    public void testProcessPaymentBanknotesWithExactAmount() throws Exception {
    	
    	Currency currency = Currency.getInstance("CAD");

    	checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        paymentHandler.totalCost = new BigDecimal("15.0");
    	
    	ArrayList<Banknote> notesList = new ArrayList<Banknote>();
    	
    	
    	banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("5.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);
        
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        
        assertTrue("Payment should succeed with exact amount", paymentHandler.processPaymentWithBanknotes(notesList));
    }

    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullBanknotesList() throws Exception {
        paymentHandler.processPaymentWithBanknotes(null); // This should throw NullPointerException
    } 
    
    // test load banknote dispenser

    /**
     * Checks if coins are actually loaded in the coin dispenser
     * @throws CashOverloadException
     */
    @Test
    public void testLoadBanknoteDispenser() throws CashOverloadException, EmptyDevice, OverloadedDevice {
    	Currency currency = Currency.getInstance("CAD");
    	// Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(5.00));
        Banknote banknote2 = new Banknote(currency, BigDecimal.valueOf(10.00));
    	
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();

        paymentHandler.loadBanknoteDispenser(banknote1, banknote2);
        assertTrue(checkoutStation.banknoteDispensers.get(BigDecimal.valueOf(10.00)).size() == 1);
        assertTrue(checkoutStation.banknoteDispensers.get(BigDecimal.valueOf(5.00)).size() == 1);
    }
    
    /**
     * Test for CashOverloadException
     * @throws CashOverloadException
     * @throws OverloadedDevice 
     * @throws EmptyDevice 
     */
    @Test (expected = CashOverloadException.class)
    public void testLoadBanknoteDispenserOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
    	Currency currency = Currency.getInstance("CAD");
    	// Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(5.00));
        
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
    
        BigDecimal v = ((Banknote) banknote1).getDenomination();
		int capacity = checkoutStation.banknoteDispensers.get(v).getCapacity();
        for (int i = 0 ; i < capacity ; i++) {
        	paymentHandler.loadBanknoteDispenser(banknote1);
        }
        //should throw overlaod error on this load
		Banknote banknote2 = new Banknote(currency, BigDecimal.valueOf(5.00));
		paymentHandler.loadBanknoteDispenser(banknote2);
    }
    
    /**
     * Test for NullPointerException when there is no coindispenser for a specific denomination of a coin
     * @throws OverloadedDevice 
     * @throws EmptyDevice 
     * @throws NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testLoadBanknotesDispenserCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
    	Currency currency = Currency.getInstance("CAD");
    	// Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(5.00));
    	
        checkoutStation.plugIn(PowerGrid.instance());
        checkoutStation.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandler.loadBanknoteDispenser(banknote1);
    }

    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandler.loadBanknoteDispenser(null);	
    }
    
    @Test (expected = NullPointerException.class)
    public void loadBanknoteDispenserTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
    	Currency currency = Currency.getInstance("CAD");
        Banknote banknote1 = new Banknote(currency, new BigDecimal("5.00"));
        Banknote banknote2 = new Banknote(currency, new BigDecimal("10.00"));
        Banknote banknote3 = null;
        paymentHandler.loadBanknoteDispenser(banknote1, banknote2, banknote3);
    }

}
    