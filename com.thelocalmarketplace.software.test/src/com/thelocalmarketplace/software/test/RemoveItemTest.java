package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.thelocalmarketplace.software.BaggingAreaListener;
import com.thelocalmarketplace.software.Order;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;

public class RemoveItemTest {
	PowerGrid grid;
	private Order orderBronze;
	private Order orderGold;
	private Order orderSilver;
	private ElectronicScaleBronze scaleBronze;
	private ElectronicScaleGold scaleGold;
	private ElectronicScaleSilver scaleSilver;
	WeightDiscrepancy weightDiscrepancyBronze;
	WeightDiscrepancy weightDiscrepancyGold;
	WeightDiscrepancy weightDiscrepancySilver;
	BaggingAreaListener baggingAreaListenerBronze;
	BaggingAreaListener baggingAreaListenerGold;
	BaggingAreaListener baggingAreaListenerSilver;
	
	@Before
	public void setup() throws OverloadedDevice{
		// create a power grid
		grid = PowerGrid.instance();
		// to avoid power outages when there is a power surge
		PowerGrid.engageUninterruptiblePowerSource();
		grid.forcePowerRestore();
		
		// set up the scales
		scaleBronze = new ElectronicScaleBronze();
		scaleBronze.plugIn(grid);
		scaleBronze.turnOn();
		scaleBronze.enable();
		
		scaleGold = new ElectronicScaleGold();
		scaleGold.plugIn(grid);
		scaleGold.turnOn();
		scaleGold.enable();
		
		scaleSilver = new ElectronicScaleSilver();
		scaleSilver.plugIn(grid);
		scaleSilver.turnOn();
		scaleSilver.enable();
		
		// initializing orders
		orderBronze = new Order(scaleBronze);
		orderGold = new Order(scaleGold);
		orderSilver = new Order(scaleSilver);
		
		// initialize WeightDiscrepancy
		weightDiscrepancyBronze = new WeightDiscrepancy(orderBronze, scaleBronze);
		weightDiscrepancyGold = new WeightDiscrepancy(orderGold, scaleGold);
		weightDiscrepancySilver = new WeightDiscrepancy(orderSilver, scaleSilver);
		
		// initialize BaggingAreaListeners and make it listen to the scale objects
		baggingAreaListenerBronze = new BaggingAreaListener(orderBronze);
		scaleBronze.register(baggingAreaListenerBronze);
		
		baggingAreaListenerGold = new BaggingAreaListener(orderGold);
		scaleGold.register(baggingAreaListenerGold);
		
		baggingAreaListenerSilver = new BaggingAreaListener(orderSilver);
		scaleBronze.register(baggingAreaListenerSilver);
	}

}