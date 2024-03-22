package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.BanknoteAdder;
import com.thelocalmarketplace.software.CheckoutStub;
import com.thelocalmarketplace.software.PaymentHandler;

import powerutility.PowerGrid;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

public class BanknoteAdderTest {
	private BanknoteAdder banknoteAdder;
	private CheckoutStub bStation;
	private ArrayList<Banknote> banknotesList;
	
	@Before
	public void setUp() {
		bStation = new CheckoutStub();
		banknoteAdder = new BanknoteAdder(bStation);
		bStation.banknoteStorage.connect(PowerGrid.instance());
        bStation.banknoteStorage.activate();
        bStation.banknoteInput.connect(PowerGrid.instance());
        bStation.banknoteInput.activate();
        bStation.banknoteValidator.connect(PowerGrid.instance());
        bStation.banknoteValidator.activate();
        PowerGrid.engageUninterruptiblePowerSource();
        PowerGrid.instance().forcePowerRestore();	
	}
	
	 @After
	    public void tearDown() {
	        bStation = null;
	        banknoteAdder = null;
	    }
	 
	
	 /**
	  * Checks if a NullPointerException is thrown if banknoteAdder is initialized with a null station
	  */
	    @Test (expected = NullPointerException.class)
	    public void testInitializeBanknoteAdderWithNullStation() {
	    	bStation = null;
	        banknoteAdder = new BanknoteAdder(bStation);
	    }
	

	    /**
	     * Tests if a NullPointerException is thrown if the banknote being inserted is null
	     * @throws DisabledException
	     * @throws CashOverloadException
	     */
	    @Test(expected = NullPointerException.class)
	    public void testInsertBanknoteWithNullBanknote() throws DisabledException, CashOverloadException {
	        banknoteAdder.insertBanknote(null);
	    }
	    
	    /**
	     * Tests if a DisabledException is thrown if a valid 
	     * banknote is inserted into a disabled banknote slot
	     * @throws DisabledException
	     * @throws CashOverloadException
	     */
	    @Test(expected = DisabledException.class)
	    public void testInsertBanknoteWithDisabledBanknoteSlot() throws DisabledException, CashOverloadException {
	        bStation.banknoteInput.disable();
	        banknoteAdder.insertBanknote(new Banknote(Currency.getInstance("CAD"), new BigDecimal(20)));
	    }

	    /**
	     * Tests whether the acceptedBanknotesList is empty (null) when no banknote has been added
	     */
	    @Test
	    public void testGetAcceptedBanknotesListEmpty() {
	    	for(Banknote banknote : banknoteAdder.getAcceptedBanknotesList()){
	    		assertTrue(banknote == null);
	    	}
	    }
	    
	    /**
	     * Tests whether valid banknotes will be inserted if the checkout station's storage unit has space
	     * @throws DisabledException
	     * @throws CashOverloadException
	     */
	    @Test
	    public void testInsertValidBanknotesIfEnoughSpace() throws DisabledException, CashOverloadException {
	        System.out.println(bStation.banknoteStorage.getCapacity());
	        Banknote banknote1 = new Banknote(Currency.getInstance("CAD"), new BigDecimal(20));
	        Banknote banknote2 = new Banknote(Currency.getInstance("CAD"), new BigDecimal(100));
	
	        assertTrue(bStation.banknoteStorage.hasSpace());
	        assertTrue(banknoteAdder.acceptInsertedBanknote(banknote1));
	        assertTrue(banknoteAdder.acceptInsertedBanknote(banknote2));

	    }
	    
	    /**
	     * Tests whether valid banknotes inserted into a checkout station with no space will disable the banknote slot
	     * @throws DisabledException
	     * @throws CashOverloadException
	     */
	    @Test (expected = CashOverloadException.class)
	    public void testInsertValidBanknotesIfNoSpace() throws DisabledException, CashOverloadException {
	        System.out.println(bStation.banknoteStorage.getCapacity());
	        Banknote banknote = new Banknote(Currency.getInstance("CAD"), new BigDecimal(20));
	        assertTrue(bStation.banknoteStorage.hasSpace());
	        for (int i = 0; i < 2000; i++) {
	            banknoteAdder.insertBanknote(banknote);
	        }
	        assertTrue(bStation.banknoteStorage.hasSpace());
	        assertTrue(banknoteAdder.acceptInsertedBanknote(banknote));

	    }
	    
	    /**
	     * Tests if a valid banknote that is inserted will into the acceptedBanknotessList
	     * @throws DisabledException
	     * @throws CashOverloadException
	     */
	    @Test
	    public void testInsertBanknote() throws DisabledException, CashOverloadException {
	    	Banknote banknote = new Banknote(Currency.getInstance("CAD"), new BigDecimal(10));
	    	assertTrue(banknoteAdder.insertBanknote(banknote));
	        assertTrue(banknoteAdder.getAcceptedBanknotesList().contains(banknote));
	    }
	    
	    
}
