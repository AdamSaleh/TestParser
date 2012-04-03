/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser.results;

import hudson.matrix.Combination;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds information about which configurations should be present 
 * in report and which should not. After this class is fed to MatrixBuildTestResults,
 * we can fetch results from only certain subset of matrix runs (correspoding to 
 * build) controlled by this class.
 * 
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class Filter {
    /**
     * Determines whether a configuration should be used in reports or not
     */
    Map<String, Boolean> configurations = new HashMap<String, Boolean>();

    private MatrixBuildTestResults owner;
    public String uuid;

    public Filter(String uuid){
	this.uuid = uuid;
    }

    public void setOwner(MatrixBuildTestResults owner){
	this.owner = owner;
    }
    
    public MatrixBuildTestResults getOwner(){
	return owner;
    }
    
    /**
     * Add a configuration to the filter, if already exists, it`s rewritten
     * 
     * @param config A String representing the
     *            {@link hudson.matrix.MatrixConfiguration} given as its
     *            {@link hudson.matrix.Combination}
     * @param include A boolean to determine whether to include the
     *            {@link hudson.model.Run} or not
     */
    public void addConfiguration(Combination combination, boolean include) {
	this.configurations.put(combination.toString(), include);
    }
    /**
     * Remove a configuration from the filter
     * 
     * @param combination A {@link hudson.matrix.MatrixConfiguration} given
     *            as its {@link hudson.matrix.Combination}
     */
    public void removeConfiguration(Combination combination) {
	configurations.remove(combination.toString());
    }
    
    /**
     * Returns whether or not to include the {@link hudson.model.Run}
     * If the combination is not in the database, the method returns false,
     * meaning the run will not be included in report.
     * 
     * @param combination A {@link hudson.matrix.MatrixConfiguration} given
     *            as its {@link hudson.matrix.Combination}
     * @return A boolean determining whether or nor to include the
     *         {@link hudson.model.Run}
     */
    public boolean getConfiguration(Combination combination) {
	if (configurations.containsKey(combination.toString())) {
	    return configurations.get(combination.toString());
	}
	return false;
    }
    
    /**
     * Alias for {@getConfiguratiion}
     * 
     * @param combination
     * @return 
     */
    public boolean isIncluded(Combination combination){
	return getConfiguration(combination);
    }
    
}
