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

public class PayWithBanknote implements BanknoteValidatorObserver {

	  private BanknoteInsertionSlot insertionSlot;
	    private BanknoteValidator validator;
	    private BanknoteStorageUnit storageUnit;
	    private BanknoteDispenserBronze dispenser;
	    //Banknotdispersier Gold 
	    private BigDecimal amountDue;
	    private Currency currency;
	    private boolean waitingForValidation;

	    public PayWithBanknote(BanknoteInsertionSlot insertionSlot, BanknoteValidator validator,
	                                 BanknoteStorageUnit storageUnit, BanknoteDispenserBronze dispenser,
	                                 BigDecimal amountDue, Currency currency) {
	        this.insertionSlot = insertionSlot;
	        this.validator = validator;
	        this.storageUnit = storageUnit;
	        this.dispenser = dispenser;
	        this.amountDue = amountDue;
	        this.currency = currency;
	        this.waitingForValidation = false;

	       
	        validator.attach(this);
	    }

	    public void pay(Banknote banknote) throws Exception {
	        if (!banknote.getCurrency().equals(currency)) {
	            throw new IllegalArgumentException("Invalid currency.");
	        }

	        insertionSlot.receive(banknote);
	        validator.receive(banknote);
	        waitingForValidation = true;
	    }
	    
	    // pay (10 dollars)
	    // goodBanknote(

	    @Override
	    public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
	        if (!waitingForValidation) return;
	        waitingForValidation = false;

	        updateTransactionTotal(denomination);
	        try {
	            dispenseChange();
	        } catch (Exception e) {
	            e.printStackTrace(); // create a loop here for not sufficient change 
	        }
	    }

	    @Override
	    public void badBanknote(BanknoteValidator validator) {
	        if (!waitingForValidation) return;
	        waitingForValidation = false;

	    }

	    private void updateTransactionTotal(BigDecimal banknoteValue) {
	        amountDue = amountDue.subtract(banknoteValue);
	    }

	    

	        

	    private void dispenseChange() throws Exception {
	        if (amountDue.compareTo(BigDecimal.ZERO) >= 0) {
	            return; // No change required
	        }

	        BigDecimal changeAmount = amountDue.negate();

	        // Simplified logic to dispense change
	        while (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
	            Banknote banknoteToDispense = selectBanknoteForChange(changeAmount);
	            if (banknoteToDispense != null) {
	                dispenser.emit(); // Emit the banknote
	                changeAmount = changeAmount.subtract(banknoteToDispense.getDenomination());
	            } else {
	                // No suitable banknote available for dispensing
	                throw new Exception("Unable to dispense the required change.");
	            }
	        }
	    }

	        private Banknote selectBanknoteForChange(BigDecimal changeAmount) {
	            
	            if (changeAmount.compareTo(new BigDecimal("20")) >= 0) {
	                return new Banknote(currency, new BigDecimal("20"));
	            } else if (changeAmount.compareTo(new BigDecimal("10")) >= 0) {
	                return new Banknote(currency, new BigDecimal("10"));
	            } // ... add other denominations as needed
	            else {
	                return null; // No suitable denomination found
	            }
	        

	    }



	//amount that the cuomer gave 
	 
	// amount of cahnge due 

		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}
	

	


	}


// 1. Customer: Inserts a banknote in the System.
// 2. System: Reduces the remaining amount due by the value of the inserted banknote.
// 3. System: Signals to the Customer the updated amount due after the insertion of the banknote.
// 4. System: If the remaining amount due is greater than 0, go to 1.
// 5. System: If the remaining amount due is less than 0, dispense the amount of change due.
