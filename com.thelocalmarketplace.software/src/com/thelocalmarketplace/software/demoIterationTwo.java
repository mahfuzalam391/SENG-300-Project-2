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

package com.thelocalmarketplace.software;

import java.util.Scanner;
import com.tdc.coin.Coin;
import com.jjjwelectronics.*;
import com.jjjwelectronics.scanner.*;
import com.jjjwelectronics.scale.*;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;


/**
 * This class is a Demo of the functions created in software.
 * Is extra, not required for the assignment, but the professor recommended it.
 */
public class demoIterationTwo {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        
        // Initialize the station and scale
        SelfCheckoutStationGold stationGold = new SelfCheckoutStationGold();
        SelfCheckoutStationSilver stationSilver = new SelfCheckoutStationSilver();
        SelfCheckoutStationBronze stationBronze = new SelfCheckoutStationBronze();
        
        
        SelfCheckoutStationSoftware software = new SelfCheckoutStationSoftware();
        ElectronicScaleGold scaleGold = new ElectronicScaleGold();
        ElectronicScaleSilver scaleSilver = new ElectronicScaleSilver();
        ElectronicScaleBronze scaleBronze = new ElectronicScaleBronze();
       
        
        try {
            // Represents user input
            Scanner input = new Scanner(System.in);

            // Test startSession function
            software.startSession(input);

            // Create order and addItemViaBarcode scan objects
            Order order = new Order(scale);

            // Create barcodes for two items, an apple and banana
            Numeral[] list = {Numeral.valueOf((byte)5), Numeral.valueOf((byte)5)};
            Numeral[] list1 = {Numeral.valueOf((byte)7), Numeral.valueOf((byte)4)};
            Barcode barcodeOfApple = new Barcode(list);
            Barcode barcodeOfBanana = new Barcode(list1);

            // Create a string to hold user input.
            String itemInput;

            // Makes barcoded product for apple and banana
            BarcodedProduct apple = new BarcodedProduct(barcodeOfApple, "An apple", 5, 1.00 );
            BarcodedProduct banana = new BarcodedProduct(barcodeOfApple, "A banana", 3, 2.00 );

            ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeOfApple, apple);
            ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeOfBanana, banana);

            // Make an arraylist for the coins added
            ArrayList<Coin> coinsList = new ArrayList<>();

            // User interaction
            System.out.println("Enter '1' for Apple.");
            System.out.println("Enter '2' for Banana.");
            itemInput = input.nextLine();

            // If the user wants to add an Apple, this occurs
            if (itemInput.equals("1")) {
                // Tests addItemViaBarcodeScan function
                order.addItemViaBarcodeScan(barcodeOfApple);
                PaymentHandler paymentHandler = new PaymentHandler(station, order);

                System.out.println("The price of an apple is $5. You insert 5 $1 bills.");

                // Add 5 1 dollar coins to the coinsList
                for (int i = 0; i < 5; i++) {
                    Coin coin = new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(1));
                    coinsList.add(coin);
                }


                // Test processPaymentWithCoins function, if successful print out a receipt.
                if (paymentHandler.processPaymentWithCoins(coinsList)) {
                    System.out.println("Payment Successful!");
                    paymentHandler.receiptPrinter(order);
                } else
                    System.out.println("Unsuccessful Payment!");

            // If the user wants to add a Banana, this occurs
            } else if (itemInput.equals("2")) {
                // Tests addItemViaBarcodeScan function
                order.addItemViaBarcodeScan(barcodeOfBanana);
                PaymentHandler paymentHandler = new PaymentHandler(station, order);

                System.out.println("The price of a banana is $3. You insert 3 $1 bills.");

                // Add 5 1 dollar coins to the coinsList
                for (int i = 0; i < 3; i++) {
                    Coin coin = new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(1));
                    coinsList.add(coin);
                }

                // Test processPaymentWithCoins function, if successful print out a receipt.
                if (paymentHandler.processPaymentWithCoins(coinsList)) {
                    System.out.println("Payment Successful!");
                    paymentHandler.receiptPrinter(order);
                } else
                    System.out.println("Unsuccessful Payment!");
            }
            else {
                System.out.println("Unable to process input.");
            }
        } catch (Exception e) {
            System.out.println("Failed to initialize order: " + e.getMessage());
        }
    }
        
        System.out.println("Checkout completed. How would you like to pay?");
        System.out.println("1. Card (debit/credit)");
        System.out.println("2. Coin");
        System.out.println("3. Banknote");
        System.out.print("Enter your choice (1/2/3): ");
        
        int choice = scanner.nextInt();
        
        switch (choice) {
            case 1:
                System.out.println("You have selected to pay with card.");
                System.out.print("Enter card type (debit/ credit): ");
                String cardType = scanner.next().toLowerCase();
                if (cardType.equals("debit")) {
                    System.out.println("You have selected to pay with a debit card.");
                    
                    // Add your debit card payment logic here
                    
                } else if (cardType.equals("credit")) {
                    System.out.println("You have selected to pay with a credit card.");
                    
                    // Add your credit card payment logic here
                    
                } else {
                	
                     System.out.println("Invalid card type. Please enter either 'debit' or 'credit'.");
                }
                break;
            case 2:
                System.out.println("You have selected to pay with coin.");
                
                // Add your coin payment logic here
                
                break;
            case 3:
                System.out.println("You have selected to pay with banknote.");
                
                // Add your banknote payment logic here
                
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
        
        scanner.close();
    }
}
