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
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.CheckoutStub;
import com.thelocalmarketplace.software.CoinAdder;
import com.thelocalmarketplace.software.PaymentHandler;

import powerutility.PowerGrid;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

public class CoinAdderTest {

    private CoinAdder coinAdder;
    private CheckoutStub cStation;
    private ArrayList<Coin> coinsList;

    @Before
    public void setUp() {
        
        //  coinsList = new ArrayList<Coin>();
        cStation = new CheckoutStub();
        coinAdder = new CoinAdder(cStation);
        cStation.coinStorage.connect(PowerGrid.instance());
        cStation.coinStorage.activate();
        cStation.coinSlot.connect(PowerGrid.instance());
        cStation.coinSlot.activate();
        cStation.coinValidator.connect(PowerGrid.instance());
        cStation.coinValidator.activate();
        PowerGrid.engageUninterruptiblePowerSource();
        PowerGrid.instance().forcePowerRestore();

    }

    @After
    public void tearDown() {
        cStation = null;
        // coinsList = null;
        coinAdder = null;
    }

    // Checks if a NullPointerException is thrown if coinAdder is initialized with a null station
    @Test (expected = NullPointerException.class)
    public void testInitializeCoinAdderWithNullStation() {
    	cStation = null;
        coinAdder = new CoinAdder(cStation);
    }

    // Tests if a NullPointerException is thrown if the coin being inserted is null
    @Test(expected = NullPointerException.class)
    public void testInsertCoinWithNullCoin() throws DisabledException, CashOverloadException {
        coinAdder.insertCoin(null);
    }

    // Tests if a DisabledException is thrown if a valid coin is inserted into a disabled coin slot
    @Test(expected = DisabledException.class)
    public void testInsertCoinWithDisabledCoinSlot() throws DisabledException, CashOverloadException {
        cStation.coinSlot.disable();
        coinAdder.insertCoin(new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05")));
    }

    // Tests whether the acceptedCoinsList is empty (null) when no coin has been added
    @Test
    public void testGetAcceptedCoinsListEmpty() {
        for (Coin coin : coinAdder.getAcceptedCoinsList()) {
        	assertTrue(coin == null);
        }
    }

    // Tests whether valid coins will be inserted if the checkout station's storage unit has space
    @Test
    public void testInsertValidCoinsIfEnoughSpace() throws DisabledException, CashOverloadException {
        System.out.println(cStation.coinStorage.getCapacity());
        Coin coin1 = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05"));
        Coin coin2 = new Coin(Currency.getInstance("USD"), new BigDecimal("1.00"));
        assertTrue(cStation.coinStorage.hasSpace());
        assertTrue(coinAdder.acceptInsertedCoin(coin1));
        assertTrue(coinAdder.acceptInsertedCoin(coin2));

    }

    // Tests whether valid coins inserted into a checkout station with no space will disable the coin slot
    @Test (expected = CashOverloadException.class)
    public void testInsertValidCoinsIfNoSpace() throws DisabledException, CashOverloadException {
        System.out.println(cStation.coinStorage.getCapacity());
        Coin coin = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05"));
        assertTrue(cStation.coinStorage.hasSpace());
        for (int i = 0; i < 2000; i++) {
            coinAdder.insertCoin(coin);
        }
        assertTrue(cStation.coinStorage.hasSpace());
        assertTrue(coinAdder.acceptInsertedCoin(coin));

    }

    // Tests if a valid coin that is inserted will into the acceptedCoinsList
    @Test
    public void testInsertCoin() throws DisabledException, CashOverloadException {
        Coin coin = new Coin(Currency.getInstance("CAD"), new BigDecimal("1.00"));
        assertTrue(coinAdder.insertCoin(coin));
        assertTrue(coinAdder.getAcceptedCoinsList().contains(coin));
    }


}