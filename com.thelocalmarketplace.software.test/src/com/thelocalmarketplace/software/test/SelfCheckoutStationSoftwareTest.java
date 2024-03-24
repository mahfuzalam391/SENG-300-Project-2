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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;


public class SelfCheckoutStationSoftwareTest {
	
	private SelfCheckoutStationSoftware software;
	private Scanner input;
	
	@Before
	public void setup() {
		software = new SelfCheckoutStationSoftware();
		input = new Scanner(System.in);
		SelfCheckoutStationSoftware.setStationActive(false);
	}
	
	@Test (expected = InvalidStateSimulationException.class)
	public void testStartSessionActiveUsingSetter() {
		SelfCheckoutStationSoftware.setStationActive(true);
		String inputData = "user input data";
		System.setIn(new java.io.ByteArrayInputStream(inputData.getBytes()));
		Scanner testInput = new Scanner(System.in);
		software.startSession(testInput);
	}
	
	@Test (expected = InvalidStateSimulationException.class)
	public void testStartSessionTwice() {
		String inputData = "user input data";
		System.setIn(new java.io.ByteArrayInputStream(inputData.getBytes()));
		Scanner testInput = new Scanner(System.in);
		software.startSession(testInput);

		software.startSession(testInput);
	}

	@Test
	public void testStartSessionNotActive() {
		String inputData = "user input data";
		System.setIn(new java.io.ByteArrayInputStream(inputData.getBytes()));
		Scanner testInput = new Scanner(System.in);
		software.startSession(testInput);

		assertTrue(SelfCheckoutStationSoftware.getStationActive());
	}
	
	@Test
	public void testStartSessionBlockGetter() {
		String inputData = "user input data";
		System.setIn(new java.io.ByteArrayInputStream(inputData.getBytes()));
		Scanner testInput = new Scanner(System.in);
		software.startSession(testInput);

		assertFalse(SelfCheckoutStationSoftware.getStationBlock());
		SelfCheckoutStationSoftware.setStationBlock(true);
		assertTrue(SelfCheckoutStationSoftware.getStationBlock());
		SelfCheckoutStationSoftware.setStationBlock(false);
		assertFalse(SelfCheckoutStationSoftware.getStationBlock());
	}
}