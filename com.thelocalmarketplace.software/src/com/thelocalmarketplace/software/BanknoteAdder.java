package com.thelocalmarketplace.software;

import java.util.ArrayList;


import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;


public class BanknoteAdder {

	private AbstractSelfCheckoutStation bStation;
	private ArrayList<Banknote> banknoteList;

	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For bronze self checkout station.
	 */
	public BanknoteAdder(SelfCheckoutStationBronze bStation) {
		if(bStation == null) throw new NullPointerException("No argument may be null.");
		this.bStation = bStation;
		this.banknoteList = new ArrayList<Banknote>();

	}
	
	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For silver self checkout station.
	 */
	public BanknoteAdder(SelfCheckoutStationSilver bStation) {
		if(bStation == null) throw new NullPointerException("No argument may be null.");
		this.bStation = bStation;
		this.banknoteList = new ArrayList<Banknote>();

	}
	
	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For gold self checkout station.
	 */
	public BanknoteAdder(SelfCheckoutStationGold bStation) {
		if(bStation == null) throw new NullPointerException("No argument may be null.");
		this.bStation = bStation;
		this.banknoteList = new ArrayList<Banknote>();

	}
	
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
	
	public boolean acceptInsertedBanknote(Banknote banknote) throws DisabledException, CashOverloadException {
		if (this.bStation.banknoteStorage.hasSpace()) {
			if(this.bStation.banknoteInput.hasSpace()) {
				this.bStation.banknoteInput.receive(banknote);
				return true;
			}
		}
		return false;
	}
}
