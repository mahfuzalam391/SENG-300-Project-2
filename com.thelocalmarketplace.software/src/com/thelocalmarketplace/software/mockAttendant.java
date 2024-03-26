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

import com.jjjwelectronics.scale.AbstractElectronicScale;

public class mockAttendant {

   
    private Order order;
	private AbstractElectronicScale scale;
	private double weight_of_bag;
	private AddOwnBag instance;
	
/** In the constructor pass in order scale and the bag weight and we create an instance of the addownbag class
 * @param order
 * @param scale
 * @param weight_of_bag
 */
	public mockAttendant(Order order, AbstractElectronicScale scale, double weight_of_bag) {
    	this.order = order;
    	this.scale = scale;
    	this.weight_of_bag = weight_of_bag;
    	
    	instance = new AddOwnBag(order,scale);
        
    }

    
	/** method that prints a message to notify to the attendant to some to station */
    public void notifyAttendant() {
        System.out.println("Attendant come here");
        
    }

    /** here the attendant approves the situation and set station block to false so the uesr can continue
     *  then call the the instance of add bag weight and print the message*/
    public void Attendant_approves() {
        // here the bag is approved
    	WeightDiscrepancy.setStationBlock(false);
    	instance.addbagweight(order,scale,weight_of_bag);
    	instance.print_mess();	
        
    }
    
    /** here the attendant fixes the problem, then sets the station to false and call the message 
     * so that now the user can continue 
     */
    public void Attendant_corrected_problem() {
        // here the bag is approved
    	WeightDiscrepancy.setStationBlock(false);
    	instance.addbagweight(order,scale,weight_of_bag);
    	instance.print_mess();
    }
}