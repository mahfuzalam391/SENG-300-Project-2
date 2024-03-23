package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

//figure out how to use gold?

import com.jjjwelectronics.EmptyDevice;

import com.jjjwelectronics.OverloadedDevice;

import com.jjjwelectronics.printer.*;


public class PayWithBanknote {

	private BanknoteInsertionSlot insertionSlot;
	private BanknoteValidator banknoteValidator;
	public Currency currency;
	//public BigDecimal amountInserted;
	public BigDecimal changeRemaining;
	// to get the total cost of all banknotes that are inserted
	private BigDecimal valueOfAllAcceptedBanknotes = new BigDecimal("0");
	public BigDecimal totalCostRemaining;
	//not sure
	//private SelfCheckoutStationBronze checkoutSystem = null;
	//the type of bills that are accepted
	private BigDecimal[] denominations = { new BigDecimal(500), new BigDecimal(200), new BigDecimal(100),
			new BigDecimal(50), new BigDecimal(20), new BigDecimal(10), new BigDecimal(5) };
	
	private ReceiptPrinterGold gold;
	
	private AbstractSelfCheckoutStation checkoutStation;
	private ArrayList<Banknote> banknoteList;
	

	//this is the pay with bank note constuctor for the bronze station
	public PayWithBanknote(BigDecimal totalAmount,SelfCheckoutStationBronze checkoutStation) {

		this.totalCostRemaining = totalAmount;
		// create a new instance of insertionSlot
		insertionSlot = new BanknoteInsertionSlot();
		currency = Currency.getInstance("CAD");
		// set up the validator so that it's cad and the bills are from 5 to 500
		// The issue is how do we check if it's valid!!
		
		if(checkoutStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutStation = checkoutStation;
		this.banknoteList = new ArrayList<Banknote>(); 
		
		banknoteValidator = new BanknoteValidator(currency, denominations);
		
	}
	
	
	
	
	//this is the pay with bank note constructor for the gold station
	public PayWithBanknote(BigDecimal totalAmount,SelfCheckoutStationGold checkoutStation) {

		this.totalCostRemaining = totalAmount;
		// create a new instance of insertionSlot
		insertionSlot = new BanknoteInsertionSlot();
		currency = Currency.getInstance("CAD");
		// set up the validator so that it's cad and the bills are from 5 to 500
		// The issue is how do we check if it's valid!!
		
		if(checkoutStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutStation = checkoutStation;
		this.banknoteList = new ArrayList<Banknote>(); 
		
		banknoteValidator = new BanknoteValidator(currency, denominations);
		
	}
	
	
	
	public PayWithBanknote(BigDecimal totalAmount,SelfCheckoutStationSilver checkoutStation) {

		this.totalCostRemaining = totalAmount;
		// create a new instance of insertionSlot
		insertionSlot = new BanknoteInsertionSlot();
		currency = Currency.getInstance("CAD");
		// set up the validator so that it's cad and the bills are from 5 to 500
		// The issue is how do we check if it's valid!!
		
		if(checkoutStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutStation = checkoutStation;
		this.banknoteList = new ArrayList<Banknote>();  
		
		banknoteValidator = new BanknoteValidator(currency, denominations);
		
	}
	
	
	
	
	/**
	 * Update the totalCostRemaining if not enough money is giving
	 * as in constructor we only create instance of PayWithBanknote once 
	 * If adding to paymentHandler can remove later
	 * @param totalAmount
	 */
	public void setTotalCost(BigDecimal totalAmount) {
		this.totalCostRemaining = totalAmount;
	}

	/**
	 * will be used to help with Signaling to the Customer the updated amount due
	 * after the insertion of each coin.
	 *
	 * @return money left to pay
	 */
	public BigDecimal getCostRemaining() {
		return this.changeRemaining;  
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
	public boolean processPaymentWithBanknotes(ArrayList<Banknote> Banknotes)
			throws DisabledException, CashOverloadException, NoCashAvailableException, EmptyDevice, OverloadedDevice {
		
		// first check if parameter is null or not
		if (Banknotes == null) {
			throw new NullPointerException("Banknotes cannot be null.");
		}
		//moved instances of BanknoteValidator and BanknoteInsertion
		
		
		for (Banknote banknote : Banknotes) { // Calculate the total value of coins inserted.
			
			insertBanknote(banknote);

			
		}
		
		for (Banknote banknote : banknoteList) { // Calculate the total value of coins inserted.
			
			valueOfAllAcceptedBanknotes = valueOfAllAcceptedBanknotes.add(banknote.getDenomination());
			//totalCostRemaining = totalCostRemaining.subtract(banknote.getDenomination());
			

			
		}
		
		//checks if the amount that was accepted is enough to make total cost go to 0 meaning that there was enough money to be 
		//paid if not then return false ,s they will need to pay again 
		if(valueOfAllAcceptedBanknotes.compareTo(this.totalCostRemaining) < 0){
			return false;
		}// i need to return chanfge
		
		//if value is equal or greater then cost
		// have to calculate the change value
		this.changeRemaining = valueOfAllAcceptedBanknotes.subtract(this.totalCostRemaining);
		if(changeRemaining.compareTo(new BigDecimal(0)) > 0) {
			return dispenseAccurateChange(changeRemaining);// needs to be made so it can also dispense banknotes
		}
		
		return true;
	}

	//Are we using the old one for this?
	//do we need to change the old one slightly
	private boolean dispenseAccurateChange(BigDecimal changeRemaining) { // being done by abdulerahman 
		
		return false;
	}
	
	
	/**
	 * The function check if it is possible to accept a banknote
	 * 
	 * @param banknote 
	 * @return true if the banknote was accpeted 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	public boolean acceptInsertedBanknote(Banknote banknote) throws DisabledException, CashOverloadException {
		if (this.checkoutStation.banknoteStorage.hasSpace()) {
			if(this.checkoutStation.banknoteInput.hasSpace()) {
				this.checkoutStation.banknoteInput.receive(banknote);
				this.checkoutStation.banknoteValidator.receive(banknote);
				return true;
			}
		}
		this.checkoutStation.banknoteOutput.receive(banknote); // splits the banknote back out 
		return false;
		
		
	}
	
	
	/**
	 * This functions actually adds the bank note to accepted list so then we can keep track of all 
	 * the bank notes that were accepted 
	 * 
	 * @param banknote
	 * @return
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	public boolean insertBanknote(Banknote banknote) throws DisabledException, CashOverloadException {
		if(banknote == null)
			throw new NullPointerException("banknote cannot be null."); // Check for null parameters.
		boolean successfulInsertion = acceptInsertedBanknote(banknote);
		if (successfulInsertion) {
			banknoteList.add(banknote);
			return true;
		}
		return false;
	}
	
	public ArrayList<Banknote> getAcceptedBanknotesList() {
		return banknoteList;
	}
	
	

}

// 1. Customer: Inserts a banknote in the System.
// 2. System: Reduces the remaining amount due by the value of the inserted banknote.
// 3. System: Signals to the Customer the updated amount due after the insertion of the banknote.
// 4. System: If the remaining amount due is greater than 0, go to 1.
// 5. System: If the remaining amount due is less than 0, dispense the amount of change due.
