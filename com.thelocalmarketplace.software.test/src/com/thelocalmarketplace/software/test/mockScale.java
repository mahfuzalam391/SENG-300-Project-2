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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

public class mockScale extends AbstractElectronicScale {
	protected mockScale(Mass limit, Mass sensitivityLimit) {
		super(limit, sensitivityLimit);
		this.massLimit = limit;  
		// TODO Auto-generated constructor stub
	}

	private List<Item> items = new ArrayList<>();
    private Mass currentMass = Mass.ZERO;
    private Mass massLimit;
    private Mass sensitivityLimit;



    @Override
    public Mass getMassLimit() {
        return massLimit;
    }

    @Override
    public Mass getSensitivityLimit() {
        return sensitivityLimit;
    }

    @Override
    public synchronized void addAnItem(Item item) {
        currentMass = currentMass.sum(item.getMass());
        items.add(item);
        
    }

    @Override
    public synchronized void removeAnItem(Item item) {
        items.remove(item);
        currentMass = calculateCurrentMass();
    }

    private Mass calculateCurrentMass() {
        Mass newMass = Mass.ZERO;
        for (Item item : items) {
            newMass = newMass.sum(item.getMass());
        }
        return newMass;
    }
    @Override
    public Mass getCurrentMassOnTheScale() {
        return currentMass;
    }

	public void plugIn(PowerGrid grid) {
		// TODO Auto-generated method stub
		
	}
	
	public void turnOn() {}
	public void enable() {}
}
