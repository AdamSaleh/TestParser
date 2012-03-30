
package com.redhat.engineering.jenkins.testparser.results;

import hudson.matrix.MatrixRun;
import hudson.model.Result;
import java.util.*;

/**
 * This is class that stores mapping Matrix Run -> Test Results
 * 
 * TODO: handle change of configuration (add/remove axis ...)
 * 
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class MatrixBuildTestResults extends BaseResult implements TestResults {
    
    private List<MethodResult> passedTests = new ArrayList<MethodResult>();
    private List<MethodResult> failedTests = new ArrayList<MethodResult>();
    private List<MethodResult> skippedTests = new ArrayList<MethodResult>();
    private int passedTestCount;
    private int failedTestCount;
    private int skippedTestCount;
    private int totalTestCount;
    private long duration;
    private Map<String, MatrixRunTestResults> results = new HashMap<String, MatrixRunTestResults>();
    private int failedConfigCount;
    private int skippedConfigCount;
    private List<MethodResult> failedConfigurationMethods = new ArrayList<MethodResult>();
    private List<MethodResult> skippedConfigurationMethods = new ArrayList<MethodResult>();
    
    private Map<String, PackageResult> packageMap = new HashMap<String, PackageResult>();
    
    // stores list of all tests performed 
    private List<TestResult> testList = new ArrayList<TestResult>();
    
    // stores list of all runs: only one for freestyle project, multiple for matrix
    private List<TestResults> runs = new ArrayList<TestResults>();
    private List<TestResults> runTestResults = new ArrayList<TestResults>();

    public MatrixBuildTestResults(String name) {
	super(name);
	failedConfigCount = 0;
	skippedConfigCount = 0;
    }
    
    /**
     * Add test results of child matrix run to this build`s results. 
     * Duplicates are not added.
     * 
     * @param mrun	matrix run to which results correspond to
     * @param results	
     * @return		false if this run is already mapped to results
     */
    public boolean addMatrixRunTestResults(MatrixRun mrun, MatrixRunTestResults results){
	
	// test if already added
	if(this.results.get(mrun.toString()) == null){
	    this.results.put(mrun.getDisplayName(), results);
	    // FIXME: update getFailedConfigCount
	    // FIXME: is owner really the one we should set stability?
	    if (results.getFailedTestCount() > 0){
		owner.setResult(Result.UNSTABLE);
	    } else {
		owner.setResult(Result.SUCCESS);
	    }
	    return true;
	}
	return false;
    }

    public int getFailedConfigCount() {
	return failedConfigCount;
    }
    
    public int getSkippedConfigCount(){
	return skippedConfigCount;
    }

    @Override
    public boolean isMatrixBuildTestResult() {
	return true;
    }

    public List<MethodResult> getFailedConfigs() {
	return failedConfigurationMethods;
    }

    public List<MethodResult> getSkippedConfigs() {
	return skippedConfigurationMethods;
    }
    
    public List<MethodResult> getFailedTests(){
	return failedTests;
    }

    public List<MethodResult> getSkippedTests(){
	return skippedTests;
    }

    public List<MethodResult> getPassedTests(){
	return passedTests;
    }
    
    public List<TestResult> getTestList(){
	return testList;
    }
    
    public int getTotalTestCount(){
	return totalTestCount;
    }
	    
    public int getPassedTestCount(){
	return passedTestCount;
    }
    
    public int getFailedTestCount(){
	return failedTestCount;
    }
    
    public int getSkippedTestCount(){
	return skippedTestCount;
    }
     
    
    public List<TestResults> getRuns(){
	    return runTestResults;
    }
    
    public String toString(){
	return String.format("TestResults {name='%s', totalTests=%d, " +
          "failedTests=%d, skippedTests=%d}", name, totalTestCount, failedTestCount,
          skippedTestCount);
    }

    public Set<String>  getPackageNames() {
	return packageMap.keySet();
    }

    public Map<String, PackageResult> getPackageMap() {
	return packageMap;
    }


    public void addUniqueTests(List<TestResult> testList) {
	Set<TestResult> tmpSet = new HashSet<TestResult>(this.testList);
	tmpSet.addAll(testList);
	this.testList = new ArrayList<TestResult>(tmpSet);
    }

    public void tally() {
	failedConfigCount = failedConfigurationMethods.size();
	skippedConfigCount = skippedConfigurationMethods.size();
	failedTestCount = failedTests.size();
	passedTestCount = passedTests.size();
	skippedTestCount = skippedTests.size();
	totalTestCount = passedTestCount + failedTestCount + skippedTestCount;
	packageMap.clear();
	for (TestResult _test : testList) {
	    for (ClassResult _class : _test.getClassList()) {
		String pkg = _class.getName();
		int lastDot = pkg.lastIndexOf('.');
		if (lastDot == -1) {
		pkg = "No Package";
		} else {
		pkg = pkg.substring(0, lastDot);
		}
		if (packageMap.containsKey(pkg)) {
		List<ClassResult> classResults = packageMap.get(pkg).getClassList();
		if (!classResults.contains(_class)) {
		    classResults.add(_class);
		}
		} else {
		PackageResult tpkg = new PackageResult(pkg);
		tpkg.getClassList().add(_class);
		tpkg.setParent(this);
		packageMap.put(pkg, tpkg);
		}
	    }
	}
	duration = 0;
	for (PackageResult pkgResult : packageMap.values()) {
	    pkgResult.tally();
	    duration += pkgResult.getDuration();
	}
    }

    public boolean isRunTestResult() {
	return false;
    }
    
}
