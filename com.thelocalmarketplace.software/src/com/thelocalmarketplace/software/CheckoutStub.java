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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import com.jjjwelectronics.card.CardReaderBronze;
import com.jjjwelectronics.card.CardReaderGold;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.BarcodeScannerBronze;
import com.jjjwelectronics.scanner.BarcodeScannerGold;
import com.jjjwelectronics.scanner.BarcodeScannerSilver;
import com.tdc.Sink;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteDispenserGold;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.CoinTray;


/**
 * There seems to be an issue with the hardware of the self-checkout stations
 * In all three tiers, they use the super constructor of AbstractSelfCheckoutStation
 * However, they use the configuration field values from the AbstractSelfCheckoutStation class it extends
 * These values are always left null during creation of AbstractSelfCheckoutStation
 * So, there is an error thrown during creation of the sub-parts, such as BanknoteValidtor throwing an exception due to null currency
 * Therefore, when testing, an instance of a self-checkout machine is not able to be created
 * So, we have created an altered copy to use for testing purposes.
 */


/**
 * Simulates the overall self-checkout station.
 * <p>
 * A self-checkout possesses the following units of hardware that the customer
 * can see and interact with:
 * <ul>
 * <li>two electronic scales, with a configurable maximum weight before it
 * overloads, one for the bagging area and one for the scanning area;</li>
 * <li>one receipt printer;</li>
 * <li>one card reader;</li>
 * <li>two scanners (the main one and the handheld one);</li>
 * <li>one input slot for banknotes;</li>
 * <li>one output slot for banknotes;</li>
 * <li>one input slot for coins;</li>
 * <li>one output tray for coins; and,</li>
 * <li>one speaker for audio output (note: you should directly use the
 * {@link AudioSystem} class, if you want to produce sounds).</li>
 * </ul>
 * <p>
 * In addition, these units of hardware are accessible to personnel with a key
 * to unlock the front of the station:
 * <ul>
 * <li>one banknote storage unit, with configurable capacity;</li>
 * <li>one or more banknote dispensers, one for each supported denomination of
 * banknote, as configured;</li>
 * <li>one coin storage unit, with configurable capacity; and,</li>
 * <li>one or more coin dispensers, one for each supported denomination of coin,
 * as configured.</li>
 * </ul>
 * <p>
 * And finally, there are certain, additional units of hardware that would only
 * be accessible to someone with the appropriate tools (like a screwdriver,
 * crowbar, or sledge hammer):
 * <ul>
 * <li>one banknote validator; and</li>
 * <li>one coin validator.</li>
 * </ul>
 * <p>
 * Many of these devices are interconnected, to permit coins or banknotes to
 * pass between them. Specifically:
 * <ul>
 * <li>the coin slot is connected to the coin validator (this is a
 * one-directional chain of devices);</li>
 * <li>the coin validator is connected to each of the coin dispensers (i.e., the
 * coin dispensers can be replenished with coins entered by customers), to the
 * coin storage unit (for any overflow coins that do not fit in the dispensers),
 * and to the coin tray for any rejected coins either because the coins are
 * invalid or because even the overflow storage unit is full (this is a
 * one-directional chain of devices);
 * <li>each coin dispenser is connected to the coin tray, to provide change
 * (this is a one-directional chain of devices);</li>
 * <li>the banknote input slot is connected to the banknote validator (this is a
 * <b>two</b>-directional chain of devices as any entered banknotes that are
 * rejected by the validator can be returned to the customer);</li>
 * <li>the banknote validator is connected to the banknote storage unit (this is
 * a one-directional chain of devices); and,</li>
 * <li>each banknote dispenser is connected to the output banknote slot; these
 * dispensers cannot be replenished by banknotes provided by customers (this is
 * a one-directional chain of devices).</li>
 * </ul>
 * <p>
 * All other functionality of the system must be performed in software,
 * installed on the self-checkout station through custom observer classes
 * implementing the various observer interfaces provided.
 * </p>
 * <p>
 * Note that banknote denominations are required to be positive integers, while
 * coin denominations are positive decimal values.
 */
public class CheckoutStub extends AbstractSelfCheckoutStationStub {
	/**
	 * Constructor utilizing the current, static configuration.
	 */
//	public CheckoutStub() {
//		super(new ElectronicScaleGold(), new ElectronicScaleSilver(), new ReceiptPrinterBronze(),
//			new CardReaderGold(), new BarcodeScannerGold(), new BarcodeScannerSilver(), new BanknoteInsertionSlot(),
//			new BanknoteDispensationSlot(),
//			new BanknoteValidator(Currency.getInstance(Locale.CANADA), setBDenominations()),
//			new BanknoteStorageUnit(1000), setBDenominations(),
//			new HashMap<>(), new CoinSlot(), new CoinValidator(Currency.getInstance(Locale.CANADA), setCDenominations()),
//			new CoinStorageUnit(1000), setCDenominations(), new HashMap<>(),
//			new CoinTray(25));
//
//		for(int i = 0; i < coinDenominations.size(); i++)
//			coinDispensers.put(coinDenominations.get(i), new CoinDispenserGold(100));
//
//		// Hook up everything.
//		interconnect(banknoteInput, banknoteValidator);
//		interconnect(banknoteValidator, banknoteStorage);
//
//		for(int i = 0; i < banknoteDenominations.length; i++)
//			banknoteDispensers.put(banknoteDenominations[i], new BanknoteDispenserGold());
//
//		for(IBanknoteDispenser dispenser : banknoteDispensers.values())
//			interconnect(dispenser, banknoteOutput);
//
//		interconnect(coinSlot, coinValidator);
//		interconnect(coinValidator, coinTray, coinDispensers, coinStorage);
//
//		for(ICoinDispenser coinDispenser : coinDispensers.values())
//			interconnect(coinDispenser, coinTray);
//	}
	
	public CheckoutStub() {
		super(new ElectronicScaleBronze(), new ElectronicScaleBronze(), new ReceiptPrinterBronze(),
			new CardReaderBronze(), new BarcodeScannerBronze(), new BarcodeScannerBronze(), new BanknoteInsertionSlot(),
			new BanknoteDispensationSlot(),
			new BanknoteValidator(Currency.getInstance(Locale.CANADA), setBDenominations()),
			new BanknoteStorageUnit(1000), setBDenominations(),
			new HashMap<>(), new CoinSlot(), new CoinValidator(Currency.getInstance(Locale.CANADA), setCDenominations()),
			new CoinStorageUnit(1000), setCDenominations(), new HashMap<>(),
			new CoinTray(25));

		for(int i = 0; i < banknoteDenominations.length; i++)
			banknoteDispensers.put(banknoteDenominations[i], new BanknoteDispenserBronze());

		for(int i = 0; i < coinDenominations.size(); i++)
			coinDispensers.put(coinDenominations.get(i), new CoinDispenserBronze(100));

		// Hook up everything.
		interconnect(banknoteInput, banknoteValidator);
		interconnect(banknoteValidator, banknoteStorage);

		for(IBanknoteDispenser dispenser : banknoteDispensers.values())
			interconnect(dispenser, banknoteOutput);

		interconnect(coinSlot, coinValidator);
		interconnect(coinValidator, coinTray, coinDispensers, coinStorage);

		for(ICoinDispenser coinDispenser : coinDispensers.values())
			interconnect(coinDispenser, coinTray);
	}
	
	private static List<BigDecimal> setCDenominations() {
		BigDecimal[] denominations = {new BigDecimal("0.25"),new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4")};
		return configureCoinDenomination(denominations);
		
	}
	
	private static BigDecimal[] setBDenominations() {
		BigDecimal[] denominations = {new BigDecimal("0.25"),new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4")};
		return configureBanknoteDenomination(denominations);
		
	}
	
	private void interconnect(CoinValidator validator, CoinTray tray, Map<BigDecimal, ICoinDispenser> dispensers, CoinStorageUnit storage) {
		OneWayChannelStub<Coin> rejectChannel = new OneWayChannelStub<Coin>(tray);
		Map<BigDecimal, Sink<Coin>> storageChannels = new HashMap<BigDecimal, Sink<Coin>>();
		OneWayChannelStub<Coin> storageChannel = new OneWayChannelStub<>(storage);

		for(BigDecimal denomination : dispensers.keySet()) {
			storageChannels.put(denomination, storageChannel);
		}

		OneWayChannelStub<Coin> overflowChannel = new OneWayChannelStub<Coin>(storage);

		validator.rejectionSink = rejectChannel;
		validator.standardSinks.putAll(storageChannels);
		validator.overflowSink = overflowChannel;
	}
}


