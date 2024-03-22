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

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.Sink;

/**
 * There seems to be an issue with the hardware of the self-checkout stations
 * In all three tiers, they use the super constructor of AbstractSelfCheckoutStation
 * However, they use the configuration field values from the AbstractSelfCheckoutStation class it extends
 * These values are always left null during creation of AbstractSelfCheckoutStation
 * So, there is an error thrown during creation of the sub-parts, such as BanknoteValidtor throwing an exception due to null currency
 * Therefore, when testing, an instance of a self-checkout machine is not able to be created
 * So, we have created an altered copy to use for testing purposes.
 * Because the channels are private these must be copied for use in the copied checkout stations.
 */

/**
 * Represents a simple device (like, say, a tube) that allows things to move in
 * one direction between other devices.
 * 
 * @param <T>
 *            The type of the things to be transported.
 */
final class OneWayChannelStub<T> implements Sink<T> {
	private Sink<T> sink;

	/**
	 * Constructs a new coin channel whose output is connected to the indicated
	 * sink.
	 * 
	 * @param sink
	 *            The device at the output end of the channel.
	 */
	public OneWayChannelStub(Sink<T> sink) {
		this.sink = sink;
	}

	/**
	 * Moves the indicated thing to the sink. This method should be called by the
	 * source device, and not by an external application.
	 * 
	 * @param thing
	 *            The thing to transport via the channel.
	 * @throws CashOverloadException
	 *             If the sink has no space for the thing.
	 * @throws DisabledException
	 *             If the sink is currently disabled.
	 */
	public synchronized void receive(T thing) throws CashOverloadException, DisabledException {
		sink.receive(thing);
	}

	/**
	 * Returns whether the sink has space for at least one more thing.
	 * 
	 * @return true if the channel can accept a thing; false otherwise.
	 */
	public synchronized boolean hasSpace() {
		return sink.hasSpace();
	}
}
