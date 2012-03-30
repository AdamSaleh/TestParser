/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser.results;

import hudson.model.AbstractBuild;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.stapler.export.Exported;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class ClassResult extends BaseResult{
    
    private List<MethodResult> testMethodList = new ArrayList<MethodResult>();
    
    private long duration;
    private int skip;
    private int fail;
    private int total;
    
    public ClassResult(String name) {
	super(name);
    }
    
    public void setOwner(AbstractBuild<?, ?> owner) {
	super.setOwner(owner);
	for (MethodResult _m : this.testMethodList) {
	    _m.setOwner(owner);
	}
    }
    
    @Exported
    public long getDuration() {
	return this.duration;
    }

    @Exported(visibility = 9)
    public int getFail() {
	return this.fail;
    }

    @Exported(visibility = 9)
    public int getSkip() {
	return skip;
    }

    @Exported(visibility = 9)
    public int getTotal() {
	return total;
    }
    public List<MethodResult> getTestMethodList() {
	return this.testMethodList;
    }

    public void addTestMethods(List<MethodResult> list) {
	this.testMethodList.addAll(list);
    }

    public void addTestMethod(MethodResult testMethod) {
	this.testMethodList.add(testMethod);
    }

    public void tally() {
	this.duration = 0;
	this.fail = 0;
	this.skip = 0;
	this.total = 0;
	Map<String, Integer> methodInstanceMap = new HashMap<String, Integer>();
	for (MethodResult methodResult : this.testMethodList) {
	    if (!methodResult.isConfig()) {
		this.duration += methodResult.getDuration();
		this.total++;
		if ("FAIL".equals(methodResult.getStatus())) {
		this.fail++;
		} else {
		if ("SKIP".equals(methodResult.getStatus())) {
		    this.skip++;
		}
		}
	    }
	    methodResult.setParent(this);
	    
	    /*
	     * Setup testUuids to ensure that methods with same names can be
	     * reached using unique urls
	     */
	    String methodName = methodResult.getName();
	    if (methodInstanceMap.containsKey(methodName)) {
		int currIdx = methodInstanceMap.get(methodName);
		methodResult.setTestUuid(String.valueOf(++currIdx));
		methodInstanceMap.put(methodName, currIdx);
	    } else {
		methodInstanceMap.put(methodName, 0);
	    }
	}
    }
    
}
