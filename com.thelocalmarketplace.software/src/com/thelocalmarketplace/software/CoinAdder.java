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



import java.util.ArrayList;


import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;


public class CoinAdder {

	private AbstractSelfCheckoutStation checkoutSystem;
	private ArrayList<Coin> coinsList;

	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For bronze self checkout station.
	 */
	public CoinAdder(SelfCheckoutStationBronze cStation) {
		if(cStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutSystem = cStation;
		this.coinsList = new ArrayList<Coin>();

	}
	
	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For silver self checkout station.
	 */
	public CoinAdder(SelfCheckoutStationSilver cStation) {
		if(cStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutSystem = cStation;
		this.coinsList = new ArrayList<Coin>();

	}
	
	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For gold self checkout station.
	 */
	public CoinAdder(SelfCheckoutStationGold cStation) {
		if(cStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutSystem = cStation;
		this.coinsList = new ArrayList<Coin>();

	}
	
	/*
	 * Creates a list of inserted coins that can be used as argument for PaymentHandler.
	 * For gold self checkout station.
	 * FOR TESTING
	 */
	public CoinAdder(CheckoutStub cStation) {
		if(cStation == null) throw new NullPointerException("No argument may be null.");
		this.checkoutSystem = cStation;
		this.coinsList = new ArrayList<Coin>();

	}

	/**
	 * TA confirmed that iteration 1 would not need user interaction and picking payment mode would come at a later time
	 */

	/**
	 * Inserts a machine into the coin slot and adds it to a list of accepted coins.
	 *
	 * @param coin The coin to be inserted into the system
	 * @return true if the coin was accepted into machine and added to coinsList, false otherwise.
	 * @throws DisabledException If the coin slot is disabled
	 * @throws CashOverloadException If the coin storage is overloaded
	 */
	public boolean insertCoin(Coin coin) throws DisabledException, CashOverloadException {
		if(coin == null)
			throw new NullPointerException("coin cannot be null."); // Check for null parameters.
		boolean successfulInsertion = acceptInsertedCoin(coin);
		if (successfulInsertion) {
			coinsList.add(coin);
			return true;
		}
		return false;
	}

	/**
	 * Get the list of coins that were successfully added to the machine
	 * @return the list of accepted coins
	 */
	public ArrayList<Coin> getAcceptedCoinsList() {
		return coinsList;
	}


	/**
	 * Accepts a coin inserted by the customer into the coin slot.
	 *
	 * @param coin The coin to be validated and accepted.
	 * @return true if the coin is successfully accepted, false otherwise.
	 * @throws DisabledException     If the coin slot is disabled.
	 * @throws CashOverloadException If the cash storage is overloaded.
	 */
	public boolean acceptInsertedCoin(Coin coin) throws DisabledException, CashOverloadException {
		if (this.checkoutSystem.coinStorage.hasSpace()) {
			if (this.checkoutSystem.coinSlot.hasSpace()) {
				this.checkoutSystem.coinSlot.receive(coin);
				this.checkoutSystem.coinValidator.receive(coin);
				return true;
			}
		}
		this.checkoutSystem.coinTray.receive(coin);
		return false;
	}
	
	
}