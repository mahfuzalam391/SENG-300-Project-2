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
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.Sink;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.CoinTray;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

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
 * Abstract base class for self-checkout stations.
 * 
 * @author TheLocalMarketplace
 */
public abstract class AbstractSelfCheckoutStationStub extends AbstractSelfCheckoutStation{
	protected static int reusableBagDispenserCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the reusable-bag dispenser.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureReusableBagDispenserCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		reusableBagDispenserCapacityConfiguration = count;
	}

	protected static int coinDispenserCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin dispensers.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinDispenserCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinDispenserCapacityConfiguration = count;
	}

	protected static int banknoteStorageUnitCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the banknote storage unit.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureBanknoteStorageUnitCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		banknoteStorageUnitCapacityConfiguration = count;
	}

	protected static int coinStorageUnitCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin storage unit.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinStorageUnitCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinStorageUnitCapacityConfiguration = count;
	}

	protected static int coinTrayCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin tray.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinTrayCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinTrayCapacityConfiguration = count;
	}

	protected static Currency currencyConfiguration;

	/**
	 * Configures the currency to be supported.
	 * 
	 * @param curr
	 *            The currency to be supported.
	 */
	public static void configureCurrency(Currency curr) {
		if(curr == null)
			throw new NullPointerSimulationException("currency");
		currencyConfiguration = curr;
	}

	protected static BigDecimal[] banknoteDenominationsConfiguration;

	/**
	 * Configures the set of banknote denominations.
	 * 
	 * @param denominations
	 *            The denominations to use for banknotes.
	 */
	public static BigDecimal[] configureBanknoteDenomination(BigDecimal[] denominations) {
		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		HashSet<BigDecimal> set = new HashSet<>();
		for(BigDecimal denomination : denominations) {
			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException("Each denomination must be positive.");

			set.add(denomination);
		}

		if(set.size() != denominations.length)
			throw new InvalidArgumentSimulationException("The denominations must all be unique.");

		// Copy the array to avoid the potential for a security hole
		banknoteDenominationsConfiguration = Arrays.copyOf(denominations, denominations.length);
		return banknoteDenominationsConfiguration;
	}

	protected static List<BigDecimal> coinDenominationsConfiguration;

	/**
	 * Configures the set of coin denominations.
	 * 
	 * @param denominations
	 *            The denominations to use for coins.
	 * @return 
	 */
	public static List<BigDecimal> configureCoinDenomination(BigDecimal[] denominations) {
		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		HashSet<BigDecimal> set = new HashSet<>();
		for(BigDecimal denomination : denominations) {
			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException("Each denomination must be positive.");

			set.add(denomination);
		}

		if(set.size() != denominations.length)
			throw new InvalidArgumentSimulationException("The denominations must all be unique.");

		// Copy the array to avoid the potential for a security hole
		coinDenominationsConfiguration = new ArrayList<BigDecimal>();
		for(BigDecimal denomination : denominations)
			coinDenominationsConfiguration.add(denomination);
		return coinDenominationsConfiguration;
	}

	protected static double scaleMaximumWeightConfiguration;

	/**
	 * Configures the maximum weight permitted for the scales.
	 * 
	 * @param weight
	 *            The maximum weight permitted for the scales.
	 */
	public static void configureScaleMaximumWeight(double weight) {
		if(weight <= 0.0)
			throw new InvalidArgumentSimulationException("The maximum weight must be positive.");

		scaleMaximumWeightConfiguration = weight;
	}

	protected static double scaleSensitivityConfiguration;

	/**
	 * Configures the sensitivity of the scales.
	 * 
	 * @param sensitivity
	 *            The sensitivity of the scales.
	 */
	public static void configureScaleSensitivity(double sensitivity) {
		if(sensitivity <= 0.0)
			throw new InvalidArgumentSimulationException("The sensitivity must be positive.");

		scaleSensitivityConfiguration = sensitivity;
	}

	/**
	 * Resets the configuration to the default values.
	 */
	public static void resetConfigurationToDefaults() {
		banknoteDenominationsConfiguration = new BigDecimal[] { BigDecimal.ONE };
		banknoteStorageUnitCapacityConfiguration = 1000;
		coinDenominationsConfiguration = new ArrayList<>();
		coinDenominationsConfiguration.add(BigDecimal.valueOf(1L));
		coinDispenserCapacityConfiguration = 100;
		coinStorageUnitCapacityConfiguration = 1000;
		coinTrayCapacityConfiguration = 25;
		currencyConfiguration = Currency.getInstance(Locale.CANADA);
		reusableBagDispenserCapacityConfiguration = 100;
		scaleMaximumWeightConfiguration = 5000.0;
		scaleSensitivityConfiguration = 0.5;
	}

	/**
	 * Represents the large scale where items are to be placed once they have been
	 * scanned or otherwise entered.
	 */
	public final IElectronicScale baggingArea;
	/**
	 * Represents the small scale used to weigh items that are sold by weight.
	 */
	public final IElectronicScale scanningArea;
	/**
	 * Represents a printer for receipts.
	 */
	public final IReceiptPrinter printer;
	/**
	 * Represents a device that can read electronic cards, through one or more input
	 * modes according to the setup of the card.
	 */
	public final ICardReader cardReader;
	/**
	 * Represents a large, central barcode scanner.
	 */
	public final IBarcodeScanner mainScanner;
	/**
	 * Represents a handheld, secondary barcode scanner.
	 */
	public final IBarcodeScanner handheldScanner;
	/**
	 * Represents a device that permits banknotes to be entered.
	 */
	public final BanknoteInsertionSlot banknoteInput;
	/**
	 * Represents a device that permits banknotes to be given to the customer.
	 */
	public final BanknoteDispensationSlot banknoteOutput;
	/**
	 * Represents a device that checks the validity of a banknote, and determines
	 * its denomination.
	 */
	public final BanknoteValidator banknoteValidator;
	/**
	 * Represents a device that stores banknotes.
	 */
	public final BanknoteStorageUnit banknoteStorage;
	/**
	 * Represents the set of denominations supported by the self-checkout system.
	 */
	public final BigDecimal[] banknoteDenominations;
	/**
	 * Represents the set of banknote dispensers, indexed by the denomination that
	 * each contains. Note that nothing prevents banknotes of the wrong denomination
	 * to be loaded into a given dispenser.
	 */
	public final Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
	/**
	 * Represents a device that permits coins to be entered.
	 */
	public final CoinSlot coinSlot;
	/**
	 * Represents a device that checks the validity of a coin, and determines its
	 * denomination.
	 */
	public final CoinValidator coinValidator;
	/**
	 * Represents a device that stores coins that have been entered by customers.
	 */
	public final CoinStorageUnit coinStorage;
	/**
	 * Represents the set of denominations of coins supported by this self-checkout
	 * system.
	 */
	public final List<BigDecimal> coinDenominations;
	/**
	 * Represents the set of coin dispensers, indexed by the denomination of coins
	 * contained by each.
	 */
	public final Map<BigDecimal, ICoinDispenser> coinDispensers;
	/**
	 * Represents a device that receives coins to return to the customer.
	 */
	public final CoinTray coinTray;

	
	
	protected AbstractSelfCheckoutStationStub(IElectronicScale baggingArea, IElectronicScale scanningArea,
		IReceiptPrinter printer, ICardReader cardReader, IBarcodeScanner mainScanner, IBarcodeScanner handheldScanner,
		BanknoteInsertionSlot banknoteInput, BanknoteDispensationSlot banknoteOutput,
		BanknoteValidator banknoteValidator, BanknoteStorageUnit banknoteStorage, BigDecimal[] banknoteDenominations,
		Map<BigDecimal, IBanknoteDispenser> banknoteDispensers, CoinSlot coinSlot, CoinValidator coinValidator,
		CoinStorageUnit coinStorage, List<BigDecimal> coinDenominations, Map<BigDecimal, ICoinDispenser> coinDispensers,
		CoinTray coinTray) {
		super(baggingArea, baggingArea, printer, cardReader, handheldScanner, handheldScanner, banknoteInput, banknoteOutput, banknoteValidator, banknoteStorage, banknoteDenominations, banknoteDispensers, coinSlot, coinValidator, coinStorage, coinDenominations, coinDispensers, coinTray);
		this.baggingArea = baggingArea;
		this.scanningArea = scanningArea;
		this.printer = printer;
		this.cardReader = cardReader;
		this.mainScanner = mainScanner;
		this.handheldScanner = handheldScanner;
		this.banknoteInput = banknoteInput;
		this.banknoteOutput = banknoteOutput;
		this.banknoteValidator = banknoteValidator;
		this.banknoteStorage = banknoteStorage;
		this.banknoteDenominations = banknoteDenominations;
		this.banknoteDispensers = banknoteDispensers;
		this.coinSlot = coinSlot;
		this.coinValidator = coinValidator;
		this.coinStorage = coinStorage;
		this.coinDenominations = coinDenominations;
		this.coinDispensers = coinDispensers;
		this.coinTray = coinTray;
		
	}

	/**
	 * Plugs in all the devices in the station.
	 * 
	 * @param grid
	 *            The power grid to plug into. Cannot be null.
	 */
	public void plugIn(PowerGrid grid) {
		baggingArea.plugIn(grid);
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.connect(grid);
		banknoteInput.connect(grid);
		banknoteOutput.connect(grid);
		banknoteStorage.connect(grid);
		banknoteValidator.connect(grid);
		cardReader.plugIn(grid);
		for(ICoinDispenser cd : coinDispensers.values())
			cd.connect(grid);
		coinSlot.connect(grid);
		coinStorage.connect(grid);
		// Don't turn on the coin tray
		coinValidator.connect(grid);
		handheldScanner.plugIn(grid);
		mainScanner.plugIn(grid);
		printer.plugIn(grid);
		scanningArea.plugIn(grid);
	}

	/**
	 * Unplugs all the devices in the station.
	 */
	public void unplug() {
		baggingArea.unplug();
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.disconnect();
		banknoteInput.disconnect();
		banknoteOutput.disconnect();
		banknoteStorage.disconnect();
		banknoteValidator.disconnect();
		cardReader.unplug();
		for(ICoinDispenser cd : coinDispensers.values())
			cd.disconnect();
		coinSlot.disconnect();
		coinStorage.disconnect();
		// Don't turn on the coin tray
		coinValidator.disconnect();
		handheldScanner.unplug();
		mainScanner.unplug();
		printer.unplug();
		scanningArea.unplug();
	}

	/**
	 * Turns on all the devices in the station.
	 */
	public void turnOn() {
		baggingArea.turnOn();
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.activate();
		banknoteInput.activate();
		banknoteOutput.activate();
		banknoteStorage.activate();
		banknoteValidator.activate();
		cardReader.turnOn();
		for(ICoinDispenser cd : coinDispensers.values())
			cd.activate();
		coinSlot.activate();
		coinStorage.activate();
		// Don't turn on the coin tray
		coinValidator.activate();
		handheldScanner.turnOn();
		mainScanner.turnOn();
		printer.turnOn();
		scanningArea.turnOn();
	}

	/**
	 * Turns off all the devices in the station.
	 */
	public void turnOff() {
		baggingArea.turnOff();
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.disactivate();
		banknoteInput.disactivate();
		banknoteOutput.disactivate();
		banknoteStorage.disactivate();
		banknoteValidator.disactivate();
		cardReader.turnOff();
		for(ICoinDispenser cd : coinDispensers.values())
			cd.disactivate();
		coinSlot.disactivate();
		coinStorage.disactivate();
		// Don't turn on the coin tray
		coinValidator.disactivate();
		handheldScanner.turnOff();
		mainScanner.turnOff();
		printer.turnOff();
		scanningArea.turnOff();
	}

	protected void interconnect(BanknoteInsertionSlot slot, BanknoteValidator validator) {
		TwoWayChannelStub<Banknote> channel = new TwoWayChannelStub<Banknote>(slot, validator);
		slot.sink = channel;
		validator.source = channel;
	}

	protected void interconnect(BanknoteValidator validator, BanknoteStorageUnit storage) {
		OneWayChannelStub<Banknote> channel = new OneWayChannelStub<Banknote>(storage);
		validator.sink = channel;
	}

	protected void interconnect(IBanknoteDispenser dispenser, BanknoteDispensationSlot slot) {
		OneWayChannelStub<Banknote> channel = new OneWayChannelStub<Banknote>(slot);
		((AbstractBanknoteDispenser)dispenser).sink = channel;
	}

	protected void interconnect(CoinSlot slot, CoinValidator validator) {
		OneWayChannelStub<Coin> channel = new OneWayChannelStub<Coin>(validator);
		slot.sink = channel;
	}

	protected void interconnect(ICoinDispenser dispenser, CoinTray tray) {
		OneWayChannelStub<Coin> channel = new OneWayChannelStub<Coin>(tray);
		((AbstractCoinDispenser)dispenser).sink = channel;
	}
}
