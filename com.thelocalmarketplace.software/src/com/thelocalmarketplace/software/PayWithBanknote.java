package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;
import java.util.Formatter.BigDecimalLayoutForm;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

// How do we use self-checkout station gold?
// The total amount get passed though as a parameter



package com.thelocalmarketplace.software;


import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.printer.*;

import com.tdc.ComponentFailure;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.ProductDatabases;


import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;


/**
 * Manages the payment process with Banknotes for a self-checkout system.
 * Handles coin insertion, validation, and change dispensing.
 */
public class PayWithBanknote  {


	public BigDecimal amountInserted;
	public BigDecimal costRemaining;
	//public BigDecimal changeRemaining = BigDecimal.ZERO;
	public BigDecimal totalCost;
	private SelfCheckoutStationBronze checkoutSystem = null;
	//private ArrayList<Item> allItemOrders;
	private ReceiptPrinterGold gold;



	public PayWithBanknote( BigDecimal totalAmount)  {

		this.totalCost = totalAmount;
	}


	/**
	 * will be used to help with Signaling to the Customer the updated amount
	 * due after the insertion of each coin.
	 *
	 * @return money left to pay
	 */
	public BigDecimal getCostRemaining() {
		return this.costRemaining ;
	}


	/**
	 * Processes payment using coins inserted by the customer.
	 *
	 * @param coinsList List of coins inserted by the customer.
	 * @return true if payment is successful, false otherwise.
	 * @throws DisabledException        If the coin slot is disabled.
	 * @throws CashOverloadException    If the cash storage is overloaded.
	 * @throws NoCashAvailableException If no cash is available for dispensing
	 *                                  change.
	 * @throws OutOfInkException
	 * @throws OutOfPaperException
	 * @throws OverloadedDevice
	 * @throws EmptyDevice
	 */
	public boolean processPaymentWithBanknotes(ArrayList<Banknote> BanknotesList)
			throws DisabledException, CashOverloadException, NoCashAvailableException, EmptyDevice, OverloadedDevice {
		
		if (BanknotesList == null)
			throw new NullPointerException("Banknotes cannot be null."); // Check for null parameters.

		
		BanknoteInsertionSlot insertionSlot = new BanknoteInsertionSlot() ;
		
		BigDecimal totalAmountOfBanknotes = new BigDecimal("0");
		
		
		for (Banknote banknote : BanknotesList) { // Calculate the total value of coins inserted.
			insertionSlot.receive(banknote);
			
			// totalAmountOfBanknotes = totalAmountOfBanknotes.add(banknote.getDenomination());
			
		}
		
		
		


		this.amountInserted = totalAmountOfBanknotes;
		
		
		boolean isSuccess = false;
		
		
		validator =
		
		for(Banknote banknote : BanknotesList) { // Accepts banknotes.
			isSuccess = banknote.isValid();
			if(!isSuccess) totalAmountOfBanknotes = totalAmountOfBanknotes.subtract(banknote.getDenomination()) ;
			isSuccess = false;
		}
		
		
		this.changeRemaining = value.subtract(this.totalCost);


		if (value.compareTo(this.totalCost) < 0)
			return false; // Return false if the total value of valid coins is less than the total cost.


		this.amountSpent = this.totalCost;


		// Return true if accurate change is dispensed.
		if (value.compareTo(this.totalCost) > 0) {
			BigDecimal changeValue = value.subtract(this.totalCost);
			return dispenseAccurateChange(changeValue);
		}
		return true;
	}


	/**
	 * Dispenses the correct amount of change to the customer and gives them the
	 * choice to print a receipt.
	 *
	 * Implements change dispensing logic using available coin denominations.
	 *
	 * @param changeValue The amount of change to be dispensed.
	 * @return true if correct change is dispensed, false otherwise.
	 * @throws DisabledException        If the coin slot is disabled.
	 * @throws CashOverloadException    If the cash storage is overloaded.
	 * @throws NoCashAvailableException If no cash is available for dispensing
	 *                                  change.
	 * @throws OutOfInkException
	 * @throws OutOfPaperException
	 * @throws OverloadedDevice
	 * @throws EmptyDevice
	 */
	public boolean dispenseAccurateChange(BigDecimal changeValue)
			throws DisabledException, CashOverloadException, NoCashAvailableException, OutOfPaperException,
			OutOfInkException, EmptyDevice, OverloadedDevice {

		BigDecimal amountDispensed = new BigDecimal("0.0");
		BigDecimal remainingAmount = changeValue;
		List<BigDecimal> coinDenominations = this.checkoutSystem.coinDenominations;
		Collections.sort(coinDenominations);
		Collections.reverse(coinDenominations);


		if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
			for (int i = 0; i < coinDenominations.size(); i++) {
				BigDecimal val = coinDenominations.get(i);
				System.out.println(val);
				while (remainingAmount.compareTo(val) >= 0 && checkoutSystem.coinDispensers.get(val).size() > 0) {
					this.checkoutSystem.coinDispensers.get(val).emit();
					amountDispensed = amountDispensed.add(val);
					remainingAmount = remainingAmount.subtract(val);
				}
			}
			BigDecimal lowestCoin = coinDenominations.get(coinDenominations.size() - 1);
			if (remainingAmount.compareTo(lowestCoin) < 0 && remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
				this.checkoutSystem.coinDispensers.get(lowestCoin).emit();
				amountDispensed = changeValue;
				remainingAmount = BigDecimal.ZERO;
			}
		}

		if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
			Scanner receiptRequest = new Scanner(System.in);
			System.out.println("Would you like a receipt?"); // Asks the user for a receipt
			String receiptAnswer = receiptRequest.nextLine();
			while (receiptAnswer.compareToIgnoreCase("yes") != 0 || receiptAnswer.compareToIgnoreCase("no") != 0) {
				System.out.println("Sorry, that input is not acceptable. Try again."); // Keeps prompting user for receipt until "yes" or "no" answer
				System.out.println("Would you like a receipt?");
				receiptAnswer = receiptRequest.nextLine();}
			if (receiptAnswer.compareToIgnoreCase("yes") == 0) { // If yes, receiptPrinter and thank user
				printReceiptForCustomer(null);
				System.out.println("Thank you for your time. We hope to see you again!");
				return true;}
			if (receiptAnswer.compareToIgnoreCase("no") == 0) { // If no, thank user
				System.out.println("No worries. Thank you for your time. We hope to see you again!");
				return true;}}
		return false;

	}


	/**
	 * Prints a receipt for the customer, with all the products' info, price, the
	 * total cost, total amount paid, and change due.
	 * @throws OverloadedDevice
	 * @throws EmptyDevice
	 */


	public String printReceiptForCustomer(Order order) throws OutOfPaperException, OutOfInkException, EmptyDevice, OverloadedDevice {


		ArrayList<String> receiptItems = new ArrayList<String>();


		System.out.println(order.getOrder().size());
		for (int i = 0; i < order.getOrder().size(); i++) {
			String productDescription;
			Item item = order.getOrder().get(i);


			if (item instanceof BarcodedItem) { // Gets the product description and the price of a barcoded product
				BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(((BarcodedItem) item).getBarcode());
				productDescription = product.getDescription();
				long price = product.getPrice();
				receiptItems.add(productDescription + " $" + String.format("%.2f", (float)price));
			}


			else if (item instanceof PLUCodedItem) { // Gets the product description and the price of a product inputted
				// through price-lookup (PLU)
				PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(((PLUCodedItem) item).getPLUCode());
				productDescription = product.getDescription();
				long price = product.getPrice();
				receiptItems.add(productDescription + " $" + String.format("%.2f", (float)price));
			}
			else {
				throw new NullPointerException("This product is not a supported product, can not be registered for a price");
			}


		}


		BigDecimal purchaseValue = totalCost;
		BigDecimal amountPaid = amountSpent;
		BigDecimal changeDue = changeRemaining;


		receiptItems.add("Total: $" + String.format("%.2f", purchaseValue));
		receiptItems.add("Paid: $" + String.format("%.2f", amountPaid));
		receiptItems.add("Change: $" + String.format("%.2f", changeDue));


		for (int i = 0; i < receiptItems.size(); i++) {
			this.gold.print('\n');

			if (this.gold.paperRemaining() == 0) {
				this.checkoutSystem = null;
				throw new OutOfPaperException("This station is out of paper and needs maintenance.");
			}

			for (int j = 0; j < receiptItems.get(i).length(); j++) {
				this.gold.print(receiptItems.get(i).charAt(j));

				if (this.gold.paperRemaining() == 0) {
					this.checkoutSystem = null;
					throw new OutOfPaperException("This station is out of paper and needs maintenance.");
				}

				if (this.gold.inkRemaining() == 0) {
					this.checkoutSystem = null;
					throw new OutOfPaperException("This station is out of ink and needs maintenance.");
				}
			}

		}

		this.gold.cutPaper();
		return this.gold.removeReceipt();
	}


	/**
	 * Loads coins into the coin dispensers for change.
	 *
	 * @param coins Coins to be loaded into the dispensers.
	 * @throws CashOverloadException If the coin dispensers are overloaded with
	 *                               coins.
	 */
	public void loadCoinDispenser(Coin... coins) throws CashOverloadException {
		if (coins == null) {
			throw new NullPointerSimulationException("coins instance cannot be null.");
		}
		for (Coin c : coins) {
			if (c == null) {
				throw new NullPointerSimulationException("coin instance cannot be null.");
			}
			BigDecimal v = c.getValue();
			try {
				this.checkoutSystem.coinDispensers.get(v).load(c);
			} catch (CashOverloadException e) {
				throw new CashOverloadException("Coin Dispenser for coins of value " + v.doubleValue() + " is full.");
			} catch (NullPointerException e) {
				throw new NullPointerException("This coin type does not exist.");
			}
		}
	}


}


// 1. Customer: Inserts a banknote in the System.
// 2. System: Reduces the remaining amount due by the value of the inserted banknote.
// 3. System: Signals to the Customer the updated amount due after the insertion of the banknote.
// 4. System: If the remaining amount due is greater than 0, go to 1.
// 5. System: If the remaining amount due is less than 0, dispense the amount of change due.
