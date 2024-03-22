package com.thelocalmarketplace.software;

import com.jjjwelectronics.scale.AbstractElectronicScale;

public class mockAttendant {

   
    private Order order;
	private AbstractElectronicScale scale;
	private double weight_of_bag;
	private AddOwnBag instance;
	

	public mockAttendant(Order order, AbstractElectronicScale scale, double weight_of_bag) {
    	this.order = order;
    	this.scale = scale;
    	this.weight_of_bag = weight_of_bag;
    	
    	instance = new AddOwnBag(order,scale);
        
    }

    

    public void notifyAttendant() {
        System.out.println("Attendant come here");
        
    }

    public void Attendant_approves() {
        // here the bag is approved
    	WeightDiscrepancy.setStationBlock(false);
    	instance.addbagweight(order,scale,weight_of_bag);
    	instance.print_mess();
    	
        
    }
    
    public void Attendant_corrected_problem() {
        // here the bag is approved
    	WeightDiscrepancy.setStationBlock(false);
    	instance.addbagweight(order,scale,weight_of_bag);
    	instance.print_mess();
    }
}