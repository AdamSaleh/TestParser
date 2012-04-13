/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser.results;

import hudson.matrix.AxisList;
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
    Map<Combination, Boolean> configurations = new HashMap<Combination, Boolean>();
    String combinationFilter;

    private MatrixBuildTestResults owner;
    public String uuid;
    private AxisList axisList;

    public Filter(String uuid, AxisList axisList){
	this.uuid = uuid;
	this.axisList = axisList;
	for(Combination c: axisList.list()){
	    configurations.put(c, false);
	}
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
    public void setConfiguration(Combination combination, boolean include) {
	this.configurations.put(combination, include);
    }
    /**
     * Remove a configuration from the filter
     * 
     * @param combination A {@link hudson.matrix.MatrixConfiguration} given
     *            as its {@link hudson.matrix.Combination}
     */
    public void removeConfiguration(Combination combination) {
	configurations.remove(combination);
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
	    return configurations.get(combination);	
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
    
    private void rebuildConfigurations(){
	
	for(Combination c: configurations.keySet()){
	    if(combinationFilter!= null && c.evalGroovyExpression(axisList, combinationFilter)){
		configurations.put(c,true);
	    } else{
		configurations.put(c, false);
	    }
	}
	
    }
    
    public void addCombinationFilter(String combinationFilter){
	this.combinationFilter = combinationFilter;
	rebuildConfigurations();
    }
    
    public void removeCombinationFilter(){
	this.combinationFilter = null;
	rebuildConfigurations();
    }
}
