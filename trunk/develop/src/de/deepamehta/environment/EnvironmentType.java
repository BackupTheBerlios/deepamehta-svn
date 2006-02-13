package de.deepamehta.environment;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.instance.InstanceType;

/**
 * This class represents a type-safe enumeration that is used to describe
 * the type of an environment instance. 
 * @author vwegert
 */public class EnvironmentType {

	 public static final EnvironmentType THIN = new EnvironmentType(1);
	 public static final EnvironmentType FAT  = new EnvironmentType(2);
    
	 private int type;
	 
	 private EnvironmentType(int type) {
		 this.type = type;
	 }
   
}
