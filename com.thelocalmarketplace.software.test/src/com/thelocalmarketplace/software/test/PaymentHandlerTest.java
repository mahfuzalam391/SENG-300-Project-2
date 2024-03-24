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

//test

package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.PaymentHandler;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.Order;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

public class PaymentHandlerTest {
    private SelfCheckoutStationGold checkoutStationG;
    private SelfCheckoutStationSilver checkoutStationS;
    private SelfCheckoutStationBronze checkoutStationB;
    private ArrayList<Product> coinsList;
    private Coin coin1, coin2;
    private Banknote banknote1, banknote2;
    private BigDecimal totalCost;
    private PaymentHandler paymentHandlerG;
    private PaymentHandler paymentHandlerS;
    private PaymentHandler paymentHandlerB;
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
        checkoutStationG = new SelfCheckoutStationGold();
        
        SelfCheckoutStationSilver.resetConfigurationToDefaults();
        SelfCheckoutStationSilver.configureCoinDenominations(coinDenominations);
        SelfCheckoutStationSilver.configureBanknoteDenominations(bankNoteDenominations);
        SelfCheckoutStationSilver.configureCurrency(Currency.getInstance("CAD"));
        checkoutStationS = new SelfCheckoutStationSilver();
        
        SelfCheckoutStationBronze.resetConfigurationToDefaults();
        SelfCheckoutStationBronze.configureCoinDenominations(coinDenominations);
        SelfCheckoutStationBronze.configureBanknoteDenominations(bankNoteDenominations);
        SelfCheckoutStationBronze.configureCurrency(Currency.getInstance("CAD"));
        checkoutStationB = new SelfCheckoutStationBronze();

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

        paymentHandlerG = new PaymentHandler((SelfCheckoutStationGold)checkoutStationG, testOrder);

        paymentHandlerG.getStation().plugIn(PowerGrid.instance());
        paymentHandlerG.getStation().turnOn();
        
        paymentHandlerS = new PaymentHandler((SelfCheckoutStationSilver)checkoutStationS, testOrder);

        paymentHandlerS.getStation().plugIn(PowerGrid.instance());
        paymentHandlerS.getStation().turnOn();
        
        paymentHandlerB = new PaymentHandler((SelfCheckoutStationBronze)checkoutStationB, testOrder);

        paymentHandlerB.getStation().plugIn(PowerGrid.instance());
        paymentHandlerB.getStation().turnOn();
        PowerGrid.engageUninterruptiblePowerSource();
        PowerGrid.instance().forcePowerRestore();
        SelfCheckoutStationSoftware.setStationBlock(false);
    }

    @After
    public void teardown() {
        paymentHandlerG = null;
        checkoutStationG = null;
        
        paymentHandlerS = null;
        checkoutStationS = null;
        
        paymentHandlerB = null;
        checkoutStationB = null;
        SelfCheckoutStationSoftware.setStationBlock(false);

    }

    @Test (expected = NullPointerException.class)
    public void testPrinterGIfOrderIsNull() throws EmptyDevice, OverloadedDevice {
        paymentHandlerG.receiptPrinter(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void testPrinterSIfOrderIsNull() throws EmptyDevice, OverloadedDevice {
        paymentHandlerS.receiptPrinter(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void testPrinterBIfOrderIsNull() throws EmptyDevice, OverloadedDevice {
        paymentHandlerB.receiptPrinter(null);
    }

    @Test
    public void testReceiptPrinterWithBarcodedProduct() throws EmptyDevice, OverloadedDevice {


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

        paymentHandlerG = new PaymentHandler(checkoutStationG, testOrder);
        paymentHandlerG.amountSpent = BigDecimal.valueOf(5); // Set amount spent for testing
        paymentHandlerG.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing
        
        paymentHandlerS = new PaymentHandler(checkoutStationS, testOrder);
        paymentHandlerS.amountSpent = BigDecimal.valueOf(5); // Set amount spent for testing
        paymentHandlerS.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing

        paymentHandlerB = new PaymentHandler(checkoutStationB, testOrder);
        paymentHandlerB.amountSpent = BigDecimal.valueOf(5); // Set amount spent for testing
        paymentHandlerB.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing

        paymentHandlerB.receiptPrinter(testOrder);
        paymentHandlerS.receiptPrinter(testOrder);
        paymentHandlerG.receiptPrinter(testOrder);
    }

    @Test
    public void testReceiptPrinterWithPLUProduct() throws EmptyDevice, OverloadedDevice {


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
        paymentHandlerG.receiptPrinter(testOrder);
        paymentHandlerS.receiptPrinter(testOrder);
        paymentHandlerB.receiptPrinter(testOrder);


        paymentHandlerG = new PaymentHandler(checkoutStationG, testOrder);
        paymentHandlerG.amountSpent = BigDecimal.valueOf(10); // Set amount spent for testing
        paymentHandlerG.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing
        
        paymentHandlerS = new PaymentHandler(checkoutStationS, testOrder);
        paymentHandlerS.amountSpent = BigDecimal.valueOf(10); // Set amount spent for testing
        paymentHandlerS.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing
        
        paymentHandlerB = new PaymentHandler(checkoutStationB, testOrder);
        paymentHandlerB.amountSpent = BigDecimal.valueOf(10); // Set amount spent for testing
        paymentHandlerB.changeRemaining = BigDecimal.valueOf(0); // Set change remaining for testing

        paymentHandlerG.receiptPrinter(testOrder);
        paymentHandlerS.receiptPrinter(testOrder);
        paymentHandlerB.receiptPrinter(testOrder);
    }

    @Test (expected = NullPointerException.class)
    public void testReceiptPrinterGWithUnsupportedProduct() throws EmptyDevice, OverloadedDevice {
        testOrder.addItemToOrder(null);
        paymentHandlerG.receiptPrinter(testOrder);
    }
    
    @Test (expected = NullPointerException.class)
    public void testReceiptPrinterSWithUnsupportedProduct() throws EmptyDevice, OverloadedDevice {
        testOrder.addItemToOrder(null);
        paymentHandlerS.receiptPrinter(testOrder);
    }
    
    @Test (expected = NullPointerException.class)
    public void testReceiptPrinterBWithUnsupportedProduct() throws EmptyDevice, OverloadedDevice {
        testOrder.addItemToOrder(null);
        paymentHandlerB.receiptPrinter(testOrder);
    }

    @Test (expected = EmptyDevice.class)
    public void testReceiptPrinterGOutOfPaper() throws EmptyDevice, OverloadedDevice {
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
        
        paymentHandlerG.receiptPrinter(testOrder);
    }
    
    @Test (expected = EmptyDevice.class)
    public void testReceiptPrinterSOutOfPaper() throws EmptyDevice, OverloadedDevice {
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
        
        paymentHandlerS.receiptPrinter(testOrder);
    }
    
    @Test (expected = EmptyDevice.class)
    public void testReceiptPrinterBOutOfPaper() throws EmptyDevice, OverloadedDevice {
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
        
        

        paymentHandlerB.receiptPrinter(testOrder);
    }
    

    @Test (expected = EmptyDevice.class)
    public void testReceiptPrinterGOutOfInk() throws EmptyDevice, OverloadedDevice {

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
        paymentHandlerG.receiptPrinter(testOrder);
    }
    
    @Test (expected = EmptyDevice.class)
    public void testReceiptPrinterSOutOfInk() throws EmptyDevice, OverloadedDevice {

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
        paymentHandlerS.receiptPrinter(testOrder);
    }
    
    @Test (expected = EmptyDevice.class)
    public void testReceiptPrinterBOutOfInk() throws EmptyDevice, OverloadedDevice {

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
        paymentHandlerB.receiptPrinter(testOrder);
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
    public void constructor_NullGStation_ThrowsException() throws OverloadedDevice, EmptyDevice {
        checkoutStationG = null;
        new PaymentHandler(checkoutStationG, new Order(null));
    }
    
    @Test(expected = NullPointerException.class)
    public void constructor_NullSStation_ThrowsException() throws OverloadedDevice, EmptyDevice {
        checkoutStationS = null;
        new PaymentHandler(checkoutStationS, new Order(null));
    }
    
    @Test(expected = NullPointerException.class)
    public void constructor_NullBStation_ThrowsException() throws OverloadedDevice, EmptyDevice {
        checkoutStationB = null;
        new PaymentHandler(checkoutStationB, new Order(null));
    }

    @Test
    public void getChangeRemainingTest() throws Exception {
        // Simulate exact payment
        assertEquals(paymentHandlerG.getChangeRemaining(), BigDecimal.ZERO);
        assertEquals(paymentHandlerS.getChangeRemaining(), BigDecimal.ZERO);
        assertEquals(paymentHandlerB.getChangeRemaining(), BigDecimal.ZERO);
    }

    @Test
    public void processPaymentWithCoinsTestWithOverpayment() throws Exception { //there is a probelm here
        // Simulate sufficient payment
        Currency currency = Currency.getInstance("CAD");
        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("0.25");
        
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("0.25");
        
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("0.25");

        ArrayList<Coin> coinsList = new ArrayList<Coin>();

        coin1 = new Coin(currency,new BigDecimal("0.10"));
        coin2 = new Coin(currency,BigDecimal.valueOf(0.25));

        coinsList.add(coin1);
        coinsList.add(coin2);

        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")));
        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")));
        
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")));
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")));
        
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")));
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")));

        assertTrue(paymentHandlerG.processPaymentWithCoins(coinsList));
        assertTrue(paymentHandlerS.processPaymentWithCoins(coinsList));
        assertTrue(paymentHandlerB.processPaymentWithCoins(coinsList));
    }

    @Test
    public void processPaymentWithCoinsTestWithOverpaymentNoDispense() throws Exception { //there is a probelm here
        // Simulate sufficient payment
        Currency currency = Currency.getInstance("CAD");
        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("0.25");
        
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("0.25");
        
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("0.25");

        ArrayList<Coin> coinsList = new ArrayList<Coin>();

        coin1 = new Coin(currency,new BigDecimal("1.0"));
        coin2 = new Coin(currency,new BigDecimal("0.10"));

        coinsList.add(coin1);
        coinsList.add(coin2);

        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")));
        
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")));
        
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")));

        assertFalse(paymentHandlerG.processPaymentWithCoins(coinsList));
        assertFalse(paymentHandlerS.processPaymentWithCoins(coinsList));
        assertFalse(paymentHandlerB.processPaymentWithCoins(coinsList));
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

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        assertFalse(paymentHandlerG.processPaymentWithCoins(coinsList));
        assertFalse(paymentHandlerS.processPaymentWithCoins(coinsList));
        assertFalse(paymentHandlerB.processPaymentWithCoins(coinsList));
    }


    @Test
    public void testProcessPaymentCoinWithExactAmount() throws Exception {
        ArrayList<Coin> coinsList = new ArrayList<Coin>();
        Currency currency = Currency.getInstance("CAD");

        coin1 = new Coin(currency,new BigDecimal("10.00"));
        coin2 = new Coin(currency,new BigDecimal("2.00"));

        coinsList.add(coin1);
        coinsList.add(coin2);

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        
        paymentHandlerG.totalCost = new BigDecimal("12.0");
        paymentHandlerS.totalCost = new BigDecimal("12.0");
        paymentHandlerB.totalCost = new BigDecimal("12.0");

        assertTrue("Payment should succeed with exact amount", paymentHandlerG.processPaymentWithCoins(coinsList));
        assertTrue("Payment should succeed with exact amount", paymentHandlerS.processPaymentWithCoins(coinsList));
        assertTrue("Payment should succeed with exact amount", paymentHandlerB.processPaymentWithCoins(coinsList));
    }

    @Test
    public void testProcessPaymentCoinBlocked() throws Exception {
    	SelfCheckoutStationSoftware.setStationBlock(true);
    	ArrayList<Coin> coinsList = new ArrayList<Coin>();
        Currency currency = Currency.getInstance("CAD");

        coin1 = new Coin(currency,new BigDecimal("10.00"));
        coin2 = new Coin(currency,new BigDecimal("2.00"));

        coinsList.add(coin1);
        coinsList.add(coin2);

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        
        paymentHandlerG.totalCost = new BigDecimal("12.0");
        paymentHandlerS.totalCost = new BigDecimal("12.0");
        paymentHandlerB.totalCost = new BigDecimal("12.0");

        assertFalse(paymentHandlerG.processPaymentWithCoins(coinsList));
        assertFalse(paymentHandlerS.processPaymentWithCoins(coinsList));
        assertFalse(paymentHandlerB.processPaymentWithCoins(coinsList));
    }
    
    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullCoinsListG() throws Exception {
        paymentHandlerG.processPaymentWithCoins(null); // This should throw NullPointerException
    }
    
    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullCoinsListS() throws Exception {
        paymentHandlerS.processPaymentWithCoins(null); // This should throw NullPointerException
    }
    
    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullCoinsListB() throws Exception {
        paymentHandlerB.processPaymentWithCoins(null); // This should throw NullPointerException
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
        paymentHandlerG.getStation().coinStorage.load(coin1);
        paymentHandlerG.getStation().coinStorage.unload();
        paymentHandlerB.getStation().coinStorage.load(coin1);
        paymentHandlerB.getStation().coinStorage.unload();
        paymentHandlerS.getStation().coinStorage.load(coin1);
        paymentHandlerS.getStation().coinStorage.unload();
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

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        paymentHandlerG.loadCoinDispenser(coin1, coin2);
        paymentHandlerS.loadCoinDispenser(coin1, coin2);
        paymentHandlerB.loadCoinDispenser(coin1, coin2);
        assertEquals(checkoutStationG.coinDispensers.get(BigDecimal.valueOf(0.25)).size(), 1);
        assertEquals(checkoutStationG.coinDispensers.get(new BigDecimal("0.10")).size(), 1);
        assertEquals(checkoutStationS.coinDispensers.get(BigDecimal.valueOf(0.25)).size(), 1);
        assertEquals(checkoutStationS.coinDispensers.get(new BigDecimal("0.10")).size(), 1);
        assertEquals(checkoutStationB.coinDispensers.get(BigDecimal.valueOf(0.25)).size(), 1);
        assertEquals(checkoutStationB.coinDispensers.get(new BigDecimal("0.10")).size(), 1);
    }

    /**
     * Test for CashOverloadException
     * @throws CashOverloadException
     * @throws OverloadedDevice
     * @throws EmptyDevice
     */
    @Test (expected = CashOverloadException.class)
    public void testLoadCoinDispenserGOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Coin coin1 = new Coin(currency, new BigDecimal("0.10"));
        Coin coin2 = new Coin(currency, new BigDecimal("0.10"));

        // Load coins into dispenser
        this.checkoutStationG.configureCoinDispenserCapacity(2);
        this.checkoutStationG = new SelfCheckoutStationGold();
        paymentHandlerG = new PaymentHandler(checkoutStationG, testOrder);
        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.loadCoinDispenser(coin1, coin2);

        //should throw overload error on this load
        Coin c = new Coin(currency, new BigDecimal("0.10"));
        paymentHandlerG.loadCoinDispenser(c);
    }
    
    @Test (expected = CashOverloadException.class)
    public void testLoadCoinDispenserSOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Coin coin1 = new Coin(currency, new BigDecimal("0.10"));
        Coin coin2 = new Coin(currency, new BigDecimal("0.10"));

        // Load coins into dispenser
        this.checkoutStationS.configureCoinDispenserCapacity(2);
        this.checkoutStationS = new SelfCheckoutStationSilver();
        paymentHandlerS = new PaymentHandler(checkoutStationS, testOrder);
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.loadCoinDispenser(coin1, coin2);

        //should throw overload error on this load
        Coin c = new Coin(currency, new BigDecimal("0.10"));
        paymentHandlerS.loadCoinDispenser(c);
    }
    
    @Test (expected = CashOverloadException.class)
    public void testLoadCoinDispenserBOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Coin coin1 = new Coin(currency, new BigDecimal("0.10"));
        Coin coin2 = new Coin(currency, new BigDecimal("0.10"));

        // Load coins into dispenser
        this.checkoutStationB.configureCoinDispenserCapacity(2);
        this.checkoutStationB = new SelfCheckoutStationBronze();
        paymentHandlerB = new PaymentHandler(checkoutStationB, testOrder);
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.loadCoinDispenser(coin1, coin2);

        //should throw overload error on this load
        Coin c = new Coin(currency, new BigDecimal("0.10"));
        paymentHandlerB.loadCoinDispenser(c);
    }

    /**
     * Test for NullPointerException when there is no coindispenser for a specific denomination of a coin
     * @throws OverloadedDevice
     * @throws EmptyDevice
     * @throws NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testLoadCoinDispenserGCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Coin coin1 = new Coin(currency, BigDecimal.valueOf(0.27));
        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandlerG.loadCoinDispenser(coin1);
    }
    
    @Test (expected = NullPointerException.class)
    public void testLoadCoinDispenserSCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Coin coin1 = new Coin(currency, BigDecimal.valueOf(0.27));
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandlerS.loadCoinDispenser(coin1);
    }
    
    @Test (expected = NullPointerException.class)
    public void testLoadCoinDispenserBCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Coin coin1 = new Coin(currency, BigDecimal.valueOf(0.27));
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandlerB.loadCoinDispenser(coin1);
    }

    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserGTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandlerG.loadCoinDispenser(null);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserSTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandlerS.loadCoinDispenser(null);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserBTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandlerB.loadCoinDispenser(null);
    }

    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserGTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        Currency currency = Currency.getInstance("CAD");
        coin1 = new Coin(currency, new BigDecimal("1.00"));
        coin2 = new Coin(currency, new BigDecimal("2.00"));
        Coin coin3 = null;
        paymentHandlerG.loadCoinDispenser(coin3,coin1,coin2);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserSTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        Currency currency = Currency.getInstance("CAD");
        coin1 = new Coin(currency, new BigDecimal("1.00"));
        coin2 = new Coin(currency, new BigDecimal("2.00"));
        Coin coin3 = null;
        paymentHandlerS.loadCoinDispenser(coin3,coin1,coin2);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadCoinDispenserBTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        Currency currency = Currency.getInstance("CAD");
        coin1 = new Coin(currency, new BigDecimal("1.00"));
        coin2 = new Coin(currency, new BigDecimal("2.00"));
        Coin coin3 = null;
        paymentHandlerB.loadCoinDispenser(coin3,coin1,coin2);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorGNullStation() throws OverloadedDevice, EmptyDevice {
        checkoutStationG = null;
        new PaymentHandler(checkoutStationG, new Order(null));
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorSNullStation() throws OverloadedDevice, EmptyDevice {
        checkoutStationS = null;
        new PaymentHandler(checkoutStationS, new Order(null));
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorBNullStation() throws OverloadedDevice, EmptyDevice {
        checkoutStationB = null;
        new PaymentHandler(checkoutStationB, new Order(null));
    }

    @Test
    public void testDispenseAccurateChange() throws Exception {
        Currency currency = Currency.getInstance("CAD");

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")));
        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)));
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")));
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)));
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")));
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)),
                new Coin(currency, BigDecimal.valueOf(0.25)));

        // Testing accurate change dispensing
        assertTrue(paymentHandlerG.dispenseAccurateChange(BigDecimal.valueOf(1.30)));
        assertEquals(0, checkoutStationG.coinDispensers.get(BigDecimal.valueOf(0.25)).size());
        assertEquals(2, checkoutStationG.coinDispensers.get(new BigDecimal("0.10")).size());
        assertTrue(paymentHandlerS.dispenseAccurateChange(BigDecimal.valueOf(1.30)));
        assertEquals(0, checkoutStationS.coinDispensers.get(BigDecimal.valueOf(0.25)).size());
        assertEquals(2, checkoutStationS.coinDispensers.get(new BigDecimal("0.10")).size());
        assertTrue(paymentHandlerB.dispenseAccurateChange(BigDecimal.valueOf(1.30)));
        assertEquals(0, checkoutStationB.coinDispensers.get(BigDecimal.valueOf(0.25)).size());
        assertEquals(2, checkoutStationB.coinDispensers.get(new BigDecimal("0.10")).size());
    }

    @Test
    public void testDispenseAccurateChangeLowestCoin() throws Exception {
        Currency currency = Currency.getInstance("CAD");

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")));
        paymentHandlerG.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")));
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")));
        paymentHandlerS.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")));
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")),
                new Coin(currency, new BigDecimal("0.10")));
        paymentHandlerB.loadCoinDispenser(
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")),
                new Coin(currency, new BigDecimal("0.25")));

        // Testing accurate change dispensing
        assertTrue(paymentHandlerG.dispenseAccurateChange(new BigDecimal("1.35")));
        assertEquals(0, checkoutStationG.coinDispensers.get(new BigDecimal("0.25")).size());
        assertEquals(1, checkoutStationG.coinDispensers.get(new BigDecimal("0.10")).size());
        assertTrue(paymentHandlerS.dispenseAccurateChange(new BigDecimal("1.35")));
        assertEquals(0, checkoutStationS.coinDispensers.get(new BigDecimal("0.25")).size());
        assertEquals(1, checkoutStationS.coinDispensers.get(new BigDecimal("0.10")).size());
        assertTrue(paymentHandlerB.dispenseAccurateChange(new BigDecimal("1.35")));
        assertEquals(0, checkoutStationB.coinDispensers.get(new BigDecimal("0.25")).size());
        assertEquals(1, checkoutStationB.coinDispensers.get(new BigDecimal("0.10")).size());
    }



    // Tests for acceptInsertedCoin

    // Tests whether valid coins will be inserted if the checkout station's storage unit has space
    @Test
    public void testInsertValidCoinsIfEnoughSpace() throws DisabledException, CashOverloadException {
        System.out.println(paymentHandlerG.getStation().coinStorage.getCapacity());
        System.out.println(paymentHandlerS.getStation().coinStorage.getCapacity());
        System.out.println(paymentHandlerB.getStation().coinStorage.getCapacity());
        Coin coin1 = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05"));
        Coin coin2 = new Coin(Currency.getInstance("USD"), new BigDecimal("1.00"));
        assertTrue(paymentHandlerG.getStation().coinStorage.hasSpace());
        paymentHandlerG.acceptInsertedCoin(coin1);
        paymentHandlerG.acceptInsertedCoin(coin2);
        assertTrue(paymentHandlerS.getStation().coinStorage.hasSpace());
        paymentHandlerS.acceptInsertedCoin(coin1);
        paymentHandlerS.acceptInsertedCoin(coin2);
        assertTrue(paymentHandlerB.getStation().coinStorage.hasSpace());
        paymentHandlerB.acceptInsertedCoin(coin1);
        paymentHandlerB.acceptInsertedCoin(coin2);

    }

    // Tests whether valid coins inserted into a checkout station with no space will disable the coin slot
    @Test
    public void testInsertValidCoinsIfNoSpace() throws DisabledException, CashOverloadException {
        System.out.println(paymentHandlerG.getStation().coinStorage.getCapacity());
        assertTrue(paymentHandlerG.getStation().coinStorage.hasSpace());
        System.out.println(paymentHandlerS.getStation().coinStorage.getCapacity());
        assertTrue(paymentHandlerS.getStation().coinStorage.hasSpace());
        System.out.println(paymentHandlerB.getStation().coinStorage.getCapacity());
        assertTrue(paymentHandlerB.getStation().coinStorage.hasSpace());
        for (int i = 0; i < 3000; i++) {
            Coin coin = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.10"));
            paymentHandlerG.acceptInsertedCoin(coin);
            paymentHandlerS.acceptInsertedCoin(coin);
            paymentHandlerB.acceptInsertedCoin(coin);
        }
        assertFalse(paymentHandlerG.getStation().coinStorage.hasSpace());
        assertTrue(checkoutStationG.coinSlot.isDisabled());
        assertFalse(paymentHandlerS.getStation().coinStorage.hasSpace());
        assertTrue(checkoutStationS.coinSlot.isDisabled());
        assertFalse(paymentHandlerB.getStation().coinStorage.hasSpace());
        assertTrue(checkoutStationB.coinSlot.isDisabled());
    }

    // test process payment with banknote


    @Test
    public void processPaymentWithBanknotesTestWithOverpayment() throws Exception {

        Currency currency = Currency.getInstance("CAD");


        paymentHandlerG = new PaymentHandler(checkoutStationG, testOrder);
        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("20.0");
        
        paymentHandlerS = new PaymentHandler(checkoutStationS, testOrder);
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("20.0");
        
        paymentHandlerB = new PaymentHandler(checkoutStationB, testOrder);
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("20.0");

        ArrayList<Banknote> notesList = new ArrayList<Banknote>();

        banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("20.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);

        paymentHandlerG.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("10.0")));
        paymentHandlerG.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("20.0")));
        paymentHandlerS.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("10.0")));
        paymentHandlerS.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("20.0")));
        paymentHandlerB.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("10.0")));
        paymentHandlerB.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("20.0")));

        assertTrue(paymentHandlerG.processPaymentWithBanknotes(notesList));
        assertTrue(paymentHandlerS.processPaymentWithBanknotes(notesList));
        assertTrue(paymentHandlerB.processPaymentWithBanknotes(notesList));
    }

    @Test
    public void processPaymentWithBanknotesTestWithOverpaymentNoDispense() throws Exception {

        Currency currency = Currency.getInstance("CAD");

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("20.0");
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("20.0");
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("20.0");

        ArrayList<Banknote> notesList = new ArrayList<Banknote>();

        banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("20.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);

        paymentHandlerG.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("20.0")));
        paymentHandlerS.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("20.0")));
        paymentHandlerB.loadBanknoteDispenser(
                new Banknote(currency, new BigDecimal("20.0")));

        assertFalse(paymentHandlerG.processPaymentWithBanknotes(notesList));
        assertFalse(paymentHandlerS.processPaymentWithBanknotes(notesList));
        assertFalse(paymentHandlerB.processPaymentWithBanknotes(notesList));
    }


    @Test
    public void processPaymentWithBanknotesTestWithUnderpayment() throws Exception {
        // Simulate insufficient payment

        Currency currency = Currency.getInstance("CAD");

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("15.0");
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("15.0");
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("15.0");


        ArrayList<Banknote> notesList = new ArrayList<Banknote>();

        banknote1 = new Banknote(currency,new BigDecimal("5.0"));
        banknote2 = new Banknote(currency,new BigDecimal("5.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        assertFalse(paymentHandlerG.processPaymentWithBanknotes(notesList));
        assertFalse(paymentHandlerS.processPaymentWithBanknotes(notesList));
        assertFalse(paymentHandlerB.processPaymentWithBanknotes(notesList));
    }


    @Test
    public void testProcessPaymentBanknotesWithExactAmount() throws Exception {

        Currency currency = Currency.getInstance("CAD");

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("15.0");
        
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("15.0");
        
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("15.0");

        ArrayList<Banknote> notesList = new ArrayList<Banknote>();


        banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("5.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        assertTrue("Payment should succeed with exact amount", paymentHandlerG.processPaymentWithBanknotes(notesList));
        assertTrue("Payment should succeed with exact amount", paymentHandlerS.processPaymentWithBanknotes(notesList));
        assertTrue("Payment should succeed with exact amount", paymentHandlerB.processPaymentWithBanknotes(notesList));
    }
    
    @Test
    public void testProcessPaymentBanknotesBlocked() throws Exception {
    	SelfCheckoutStationSoftware.setStationBlock(true);
        Currency currency = Currency.getInstance("CAD");

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        paymentHandlerG.totalCost = new BigDecimal("15.0");
        
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        paymentHandlerS.totalCost = new BigDecimal("15.0");
        
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        paymentHandlerB.totalCost = new BigDecimal("15.0");

        ArrayList<Banknote> notesList = new ArrayList<Banknote>();


        banknote1 = new Banknote(currency,new BigDecimal("10.0"));
        banknote2 = new Banknote(currency,new BigDecimal("5.0"));

        notesList.add(banknote1);
        notesList.add(banknote2);

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        assertFalse(paymentHandlerG.processPaymentWithBanknotes(notesList));
        assertFalse(paymentHandlerS.processPaymentWithBanknotes(notesList));
        assertFalse(paymentHandlerB.processPaymentWithBanknotes(notesList));
    }

    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullBanknotesListG() throws Exception {
        paymentHandlerG.processPaymentWithBanknotes(null); // This should throw NullPointerException
    }
    
    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullBanknotesListS() throws Exception {
        paymentHandlerS.processPaymentWithBanknotes(null); // This should throw NullPointerException
    }
    
    @Test(expected = NullPointerException.class)
    public void testProcessPaymentWithNullBanknotesListB() throws Exception {
        paymentHandlerB.processPaymentWithBanknotes(null); // This should throw NullPointerException
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

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        
        paymentHandlerG.loadBanknoteDispenser(banknote1, banknote2);
        assertTrue(checkoutStationG.banknoteDispensers.get(BigDecimal.valueOf(10.00)).size() == 1);
        assertTrue(checkoutStationG.banknoteDispensers.get(BigDecimal.valueOf(5.00)).size() == 1);
        paymentHandlerS.loadBanknoteDispenser(banknote1, banknote2);
        assertTrue(checkoutStationS.banknoteDispensers.get(BigDecimal.valueOf(10.00)).size() == 1);
        assertTrue(checkoutStationS.banknoteDispensers.get(BigDecimal.valueOf(5.00)).size() == 1);
        paymentHandlerB.loadBanknoteDispenser(banknote1, banknote2);
        assertTrue(checkoutStationB.banknoteDispensers.get(BigDecimal.valueOf(10.00)).size() == 1);
        assertTrue(checkoutStationB.banknoteDispensers.get(BigDecimal.valueOf(5.00)).size() == 1);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserGTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        Currency currency = Currency.getInstance("CAD");
        Banknote note1 = new Banknote(currency, new BigDecimal("5.0"));
        Banknote note2 = new Banknote(currency, new BigDecimal("10.0"));
        Banknote note3 = null;
        paymentHandlerG.loadBanknoteDispenser(note1,note2,note3);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserSTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        Currency currency = Currency.getInstance("CAD");
        Banknote note1 = new Banknote(currency, new BigDecimal("5.0"));
        Banknote note2 = new Banknote(currency, new BigDecimal("10.0"));
        Banknote note3 = null;
        paymentHandlerS.loadBanknoteDispenser(note1,note2,note3);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserBTestWithNullCoin() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        Currency currency = Currency.getInstance("CAD");
        Banknote note1 = new Banknote(currency, new BigDecimal("5.0"));
        Banknote note2 = new Banknote(currency, new BigDecimal("10.0"));
        Banknote note3 = null;
        paymentHandlerB.loadBanknoteDispenser(note1,note2,note3);
    }

    /**
     * Test for CashOverloadException
     * @throws CashOverloadException
     * @throws OverloadedDevice
     * @throws EmptyDevice
     */
    @Test (expected = CashOverloadException.class)
    public void testLoadBanknoteDispenserGOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(5.00));

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();

        BigDecimal v = ((Banknote) banknote1).getDenomination();
        int capacity = checkoutStationG.banknoteDispensers.get(v).getCapacity();
        for (int i = 0 ; i < capacity ; i++) {
            paymentHandlerG.loadBanknoteDispenser(banknote1);
        }
        //should throw overload error on this load
        Banknote banknote2 = new Banknote(currency, BigDecimal.valueOf(5.00));
        paymentHandlerG.loadBanknoteDispenser(banknote2);
    }
    
    @Test (expected = CashOverloadException.class)
    public void testLoadBanknoteDispenserSOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(5.00));

        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();

        BigDecimal v = ((Banknote) banknote1).getDenomination();
        int capacity = checkoutStationS.banknoteDispensers.get(v).getCapacity();
        for (int i = 0 ; i < capacity ; i++) {
            paymentHandlerS.loadBanknoteDispenser(banknote1);
        }
        //should throw overload error on this load
        Banknote banknote2 = new Banknote(currency, BigDecimal.valueOf(5.00));
        paymentHandlerS.loadBanknoteDispenser(banknote2);
    }
    
    @Test (expected = CashOverloadException.class)
    public void testLoadBanknoteDispenserBOverload() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(5.00));

        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();

        BigDecimal v = ((Banknote) banknote1).getDenomination();
        int capacity = checkoutStationB.banknoteDispensers.get(v).getCapacity();
        for (int i = 0 ; i < capacity ; i++) {
            paymentHandlerB.loadBanknoteDispenser(banknote1);
        }
        //should throw overload error on this load
        Banknote banknote2 = new Banknote(currency, BigDecimal.valueOf(5.00));
        paymentHandlerB.loadBanknoteDispenser(banknote2);
    }

    /**
     * Test for NullPointerException when there is no coindispenser for a specific denomination of a coin
     * @throws OverloadedDevice
     * @throws EmptyDevice
     * @throws NullPointerException
     */
    @Test (expected = NullPointerException.class)
    public void testLoadBanknotesDispenserGCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(6.00));

        checkoutStationG.plugIn(PowerGrid.instance());
        checkoutStationG.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandlerG.loadBanknoteDispenser(banknote1);
    }
    
    @Test (expected = NullPointerException.class)
    public void testLoadBanknotesDispenserSCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(6.00));

        checkoutStationS.plugIn(PowerGrid.instance());
        checkoutStationS.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandlerS.loadBanknoteDispenser(banknote1);
    }
    
    @Test (expected = NullPointerException.class)
    public void testLoadBanknotesDispenserBCoinDoesntExist() throws CashOverloadException, EmptyDevice, OverloadedDevice {
        Currency currency = Currency.getInstance("CAD");
        // Prepare some coins
        Banknote banknote1 = new Banknote(currency, BigDecimal.valueOf(6.00));

        checkoutStationB.plugIn(PowerGrid.instance());
        checkoutStationB.turnOn();
        //should throw error for not recognizing coin in dispenser
        paymentHandlerB.loadBanknoteDispenser(banknote1);
    }

    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserGTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandlerG.loadBanknoteDispenser(null);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserSTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandlerS.loadBanknoteDispenser(null);
    }
    
    @Test (expected = NullPointerSimulationException.class)
    public void loadBanknoteDispenserBTestWithNull() throws NullPointerSimulationException, CashOverloadException {
        // Add coins to the coin storage unit before calling emptyCoinStorage()
        paymentHandlerB.loadBanknoteDispenser(null);
    }
    
    // Tests whether valid banknotes inserted into a checkout station with no space will disable the banknote input
    @Test 
    public void testInsertValidBanknotsIfNoSpace() throws DisabledException, CashOverloadException {
        System.out.println(paymentHandlerG.getStation().banknoteStorage.getCapacity());
        System.out.println(paymentHandlerS.getStation().banknoteStorage.getCapacity());
        System.out.println(paymentHandlerB.getStation().banknoteStorage.getCapacity());
        assertTrue(paymentHandlerG.getStation().banknoteStorage.hasSpace());
        assertTrue(paymentHandlerS.getStation().banknoteStorage.hasSpace());
        assertTrue(paymentHandlerB.getStation().banknoteStorage.hasSpace());
        for (int i = 0; i < 3000; i++) {
            Banknote note = new Banknote(Currency.getInstance("CAD"), new BigDecimal("5.0"));
            paymentHandlerG.acceptInsertedBanknote(note);
            paymentHandlerS.acceptInsertedBanknote(note);
            paymentHandlerB.acceptInsertedBanknote(note);
        }
        assertFalse(paymentHandlerG.getStation().banknoteStorage.hasSpace());
        assertTrue(checkoutStationG.banknoteInput.isDisabled());
        assertFalse(paymentHandlerS.getStation().banknoteStorage.hasSpace());
        assertTrue(checkoutStationS.banknoteInput.isDisabled());
        assertFalse(paymentHandlerB.getStation().banknoteStorage.hasSpace());
        assertTrue(checkoutStationB.banknoteInput.isDisabled());
    }

    @Test
    public void testPayWithCreditViaSwipeHoldFailed() throws IOException, EmptyDevice, OverloadedDevice {
        Card creditCard = new Card("Credit", "21", "Holder1", "211");
        CardIssuer cardIssuer = new CardIssuer("Seng300 Bank", 10);
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.YEAR, 5);
        cardIssuer.addCardData(creditCard.number, creditCard.cardholder, expiry, creditCard.cvv, 2000);
        int amountCharged = 0;
        int expectedResult = -1;
        assertEquals(paymentHandlerG.payWithCreditViaSwipe(creditCard, amountCharged, cardIssuer), expectedResult);
        assertEquals(paymentHandlerS.payWithCreditViaSwipe(creditCard, amountCharged, cardIssuer), expectedResult);
    	assertEquals(paymentHandlerB.payWithCreditViaSwipe(creditCard, amountCharged, cardIssuer), expectedResult);
    }
    
    @Test
    public void testPayWithDebitViaSwipeHoldFailed() throws IOException, EmptyDevice, OverloadedDevice {
    	Card debitCard = new Card("Debit", "12", "Holder2", "122");
    	CardIssuer cardIssuer = new CardIssuer("Seng300 Bank", 10);
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.YEAR, 5);
        cardIssuer.addCardData(debitCard.number, debitCard.cardholder, expiry, debitCard.cvv, 2000);
        int amountCharged = 0;
        int expectedResult = -1;
        assertEquals(paymentHandlerG.payWithDebitViaSwipe(debitCard, amountCharged, cardIssuer), expectedResult);
        assertEquals(paymentHandlerS.payWithDebitViaSwipe(debitCard, amountCharged, cardIssuer), expectedResult);
        assertEquals(paymentHandlerB.payWithDebitViaSwipe(debitCard, amountCharged, cardIssuer), expectedResult);
    }
    

    @Test
    public void testPayWithCreditViaSwipe() throws IOException, EmptyDevice, OverloadedDevice {
        Card creditCard = new Card("Credit", "21", "Holder1", "211");
        CardIssuer cardIssuer = new CardIssuer("Seng300 Bank", 10);
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.YEAR, 5);
        cardIssuer.addCardData(creditCard.number, creditCard.cardholder, expiry, creditCard.cvv, 2000);
        int amountCharged = 1;
        int expectedResult = 1;
        assertEquals(paymentHandlerG.payWithCreditViaSwipe(creditCard, amountCharged, cardIssuer), expectedResult);
        assertEquals(paymentHandlerS.payWithCreditViaSwipe(creditCard, amountCharged, cardIssuer), expectedResult);
    	assertEquals(paymentHandlerB.payWithCreditViaSwipe(creditCard, amountCharged, cardIssuer), expectedResult);
    }
    
    @Test
    public void testPayWithDebitViaSwipe() throws IOException, EmptyDevice, OverloadedDevice {
    	Card debitCard = new Card("Debit", "12", "Holder2", "122");
    	CardIssuer cardIssuer = new CardIssuer("Seng300 Bank", 10);
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.YEAR, 5);
        cardIssuer.addCardData(debitCard.number, debitCard.cardholder, expiry, debitCard.cvv, 2000);
        int amountCharged = 1;
        int expectedResult = 1;
        assertEquals(paymentHandlerG.payWithDebitViaSwipe(debitCard, amountCharged, cardIssuer), expectedResult);
        assertEquals(paymentHandlerS.payWithDebitViaSwipe(debitCard, amountCharged, cardIssuer), expectedResult);
        assertEquals(paymentHandlerB.payWithDebitViaSwipe(debitCard, amountCharged, cardIssuer), expectedResult);
    }
}
