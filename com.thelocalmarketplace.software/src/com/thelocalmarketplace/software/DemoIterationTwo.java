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

import static com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation.resetConfigurationToDefaults;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.jjjwelectronics.card.Card;
import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinStorageUnit;
import com.jjjwelectronics.*;
import com.jjjwelectronics.scanner.*;
import com.jjjwelectronics.scale.*;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import powerutility.PowerGrid;

import java.math.BigDecimal;


/**
 * This class is a Demo of the functions created in software.
 * Is extra, not required for the assignment, but the professor recommended it.
 */
public class DemoIterationTwo {

    public static void main(String[] args) {
        // Represents user input
        Scanner input = new Scanner(System.in);

        AbstractSelfCheckoutStation station = null;
        AbstractElectronicScale scale = null;
        BigDecimal[] denominations = {BigDecimal.valueOf(3), BigDecimal.valueOf(5), BigDecimal.valueOf(1)};

        // Configure all the selfcheckoutstation fields
        AbstractSelfCheckoutStation.configureCurrency(Currency.getInstance(Locale.CANADA));
        AbstractSelfCheckoutStation.configureBanknoteDenominations(denominations);
        AbstractSelfCheckoutStation.configureBanknoteStorageUnitCapacity(10);
        AbstractSelfCheckoutStation.configureCoinDenominations(denominations);
        AbstractSelfCheckoutStation.configureCoinStorageUnitCapacity(10);
        AbstractSelfCheckoutStation.configureCoinTrayCapacity(10);
        AbstractSelfCheckoutStation.configureCoinDispenserCapacity(10);

        CardIssuer cardIssuer = new CardIssuer("Seng300 Bank", 10);

        Card creditCard = new Card("Credit", "21", "Holder1", "211");
        Card debitCard = new Card("Debit", "12", "Holder2", "122");

        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.YEAR, 5);

        cardIssuer.addCardData("21", "Holder1", expiry, "211", 2000);
        cardIssuer.addCardData("12", "Holder2", expiry, "122", 2000);

        System.out.println("Enter '1' for gold system.");
        System.out.println("Enter '2' for silver system.");
        System.out.println("Enter '3' for bronze system.");

        int stationType = input.nextInt();
        input.nextLine();

        // Initialize the station and scale
        switch (stationType) {
            case 1:
                station = new SelfCheckoutStationGold();
                scale = new ElectronicScaleGold();
                break;
            case 2:
                station = new SelfCheckoutStationSilver();
                scale = new ElectronicScaleSilver();
                break;
            case 3:
                station = new SelfCheckoutStationBronze();
                scale = new ElectronicScaleBronze();
                break;
        }

        PowerGrid.engageUninterruptiblePowerSource();
        station.plugIn(PowerGrid.instance());
        station.coinStorage.activate();
        station.coinSlot.activate();
        station.coinValidator.activate();
        station.banknoteStorage.activate();
        station.banknoteValidator.activate();
        station.banknoteInput.activate();

        for (Map.Entry<BigDecimal, ICoinDispenser> entry : station.coinDispensers.entrySet()) {
            entry.getValue().activate();
        }

        for (Entry<BigDecimal, IBanknoteDispenser> entry : station.banknoteDispensers.entrySet()) {
            entry.getValue().activate();
        }


        SelfCheckoutStationSoftware software = new SelfCheckoutStationSoftware();

        try {

            // Starts a new session.
            software.startSession(input);

            // Create order and addItemViaBarcode scan objects
            Order order = new Order(scale);

            // Create barcodes for two items, an apple and banana
            Numeral[] list = {Numeral.valueOf((byte) 5), Numeral.valueOf((byte) 5)};
            Numeral[] list1 = {Numeral.valueOf((byte) 7), Numeral.valueOf((byte) 4)};
            Barcode barcodeOfApple = new Barcode(list);
            Barcode barcodeOfBanana = new Barcode(list1);

            // Create a string to hold user input.
            String itemInput;

            // Makes barcoded product for apple and banana
            BarcodedProduct apple = new BarcodedProduct(barcodeOfApple, "An apple", 5, 1.00);
            BarcodedProduct banana = new BarcodedProduct(barcodeOfApple, "A banana", 3, 2.00);

            ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeOfApple, apple);
            ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeOfBanana, banana);

            // Make an arraylist for the coins added
            ArrayList<Coin> coinsList = new ArrayList<>();

            double price = 0;

            while (true) {
                // User interaction
                System.out.println("Enter '1' for Apple.");
                System.out.println("Enter '2' for Banana.");
                itemInput = input.nextLine();

                // The user chooses what item they want and the item is added to the order.
                if (itemInput.equals("1")) {
                    order.addItemViaBarcodeScan(barcodeOfApple);
                    System.out.println("The price of an apple is $5. You must pay $5 total.");
                    price = 5;
                    break;
                } else if (itemInput.equals("2")) {
                    order.addItemViaBarcodeScan(barcodeOfBanana);
                    System.out.println("The price of a banana is $3. You must pay $3 total.");
                    price = 3;
                    break;
                } else {
                    System.out.println("Unable to process input. Please try again.");
                }
            }

            PaymentHandler paymentHandler = new PaymentHandler(station, order);

            boolean breakWhileLoop = true;

            while (breakWhileLoop) {
                System.out.println("Checkout completed. How would you like to pay?");
                System.out.println("1. Credit Card");
                System.out.println("2. Debit Card");
                System.out.println("3. Coin");
                System.out.println("4. Banknote");
                System.out.print("Enter your choice (1/2/3/4): ");

                int paymentChoice = input.nextInt();
                input.nextLine();

                switch (paymentChoice) {
                    case 1:
                        System.out.println("You pay $" + price + " with your credit card.");
                        if (paymentHandler.payWithCreditViaSwipe(creditCard, price, cardIssuer) == -1) {
                            System.out.println("Unsuccessful Payment! Please try again.");
                            break;
                        }
                        price = paymentHandler.getTotalCost().doubleValue();
                        System.out.println("Successful Payment! The total price is now $" + price + ".");

                        breakWhileLoop = false;
                        break;
                    case 2:
                        System.out.println("You pay $" + price + " with your debit card.");
                        if (paymentHandler.payWithDebitViaSwipe(debitCard, price, cardIssuer) == -1) {
                            System.out.println("Unsuccessful Payment! Please try again.");
                            break;
                        }
                        
                        price = paymentHandler.getTotalCost().doubleValue();
                        System.out.println("Successful Payment! The total price is now $" + price + ".");

                        breakWhileLoop = false;
                        break;

                    case 3:
                        if (price == 5) {
                            System.out.println("You insert 5 $1 coins.");

                            // Add 5 1 dollar coins to the coinsList
                            for (int i = 0; i < 5; i++) {
                                Coin coin = new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(1));
                                coinsList.add(coin);
                            }

                            if (paymentHandler.processPaymentWithCoins(coinsList)) {
                                System.out.println("Payment Successful!");
                            } else {
                                System.out.println("Unsuccessful Payment! Please try again.");
                                break;
                            }

                        } else {
                            System.out.println("You insert 3 $1 coins.");

                            // Add 3 1 dollar coins to the coinsList
                            for (int i = 0; i < 3; i++) {
                                Coin coin = new Coin(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(1));
                                coinsList.add(coin);
                            }

                            // Test processPaymentWithCoins function, if successful print out a receipt.
                            if (paymentHandler.processPaymentWithCoins(coinsList)) {
                                System.out.println("Payment Successful!");
                            } else {
                                System.out.println("Unsuccessful Payment! Please try again.");
                                break;
                            }
                        }

                        breakWhileLoop = false;
                        break;
                    case 4:
                        System.out.println("You have selected to pay with banknote.");

                        if (price == 5) {
                            System.out.println("You insert a $5 bill.");
                            Banknote banknote = new Banknote(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(5));
                            ArrayList<Banknote> banknotes = new ArrayList<>();
                            banknotes.add(banknote);
                            if (paymentHandler.processPaymentWithBanknotes(banknotes)) {
                                System.out.println("Payment Successful!");
                            } else {
                                System.out.println("Unsuccessful Payment! Please try again.");
                                break;
                            }
                        } else {
                            System.out.println("You insert a $3 bill.");
                            Banknote banknote = new Banknote(Currency.getInstance(Locale.CANADA), BigDecimal.valueOf(3));
                            ArrayList<Banknote> banknotes = new ArrayList<>();
                            banknotes.add(banknote);
                            if (paymentHandler.processPaymentWithBanknotes(banknotes)) {
                                System.out.println("Payment Succesful!");
                            } else {
                                System.out.println("Unsuccessful Payment! Please try again.");
                                break;
                            }
                        }
                        breakWhileLoop = false;
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            } // End of payment while loop

            System.out.println("Would you like a receipt? (Yes/No)");
            String receiptChoice = input.nextLine();
            
            if (receiptChoice.equalsIgnoreCase("Yes")) {
                System.out.println(paymentHandler.receiptPrinter(order));
            }
            // Proper closure of resources and final messages can be added here
            System.out.println("Thank you for using the self-checkout system.");

        } catch (Exception e) {
            System.out.println("Failed to initialize order: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (input != null) {
                input.close();
            }
            System.exit(0);
        }
    }
}
