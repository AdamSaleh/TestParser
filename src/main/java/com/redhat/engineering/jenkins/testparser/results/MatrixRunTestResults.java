
package com.redhat.engineering.jenkins.testparser.results;

import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import java.util.*;
import org.kohsuke.stapler.export.Exported;

/**
 * TODO: Fix whole class (it is analogue to TestResults in TestNG plugin)
 * 
 * @author Jan Rusnacko (jrusnack at redhat.com)
 * @author nullin
 * @author farshidce
 */
public class MatrixRunTestResults extends BaseResult implements TestResults{
    private List<MethodResult> passedTests = new ArrayList<MethodResult>();
    private List<MethodResult> failedTests = new ArrayList<MethodResult>();
    private List<MethodResult> skippedTests = new ArrayList<MethodResult>();
    private List<MethodResult> failedConfigurationMethods = new ArrayList<MethodResult>();
    private List<MethodResult> skippedConfigurationMethods = new ArrayList<MethodResult>();
    private List<TestResult> testList = new ArrayList<TestResult>();
    private int totalTestCount;
    private long duration;
    private int failedConfigCount;
    private int skippedConfigCount;
    private int passedTestCount;
    private int failedTestCount;
    private int skippedTestCount;
    private Map<String, PackageResult> packageMap = new HashMap<String, PackageResult>();
    private AbstractBuild<?, ?> owner;
    
    public MatrixRunTestResults(String name){
	super(name);
    }
    
    @Override
    public List<MethodResult> getFailedTests() {
	return failedTests;
    }

    @Override
    public List<MethodResult> getPassedTests() {
	return passedTests;
    }

    @Override
    public List<MethodResult> getSkippedTests() {
	return skippedTests;
    }

    @Override
    public List<TestResult> getTestList() {
	return testList;
    }

    @Exported(name = "total")
    @Override
    public int getTotalTestCount() {
	return totalTestCount;
    }

    @Exported
    public long getDuration() {
	return duration;
    }

    @Override
    public int getPassedTestCount() {
	return passedTestCount;
    }

    @Exported(name = "fail")
    @Override
    public int getFailedTestCount() {
	return failedTestCount;
    }

    @Exported(name = "skip")
    @Override
    public int getSkippedTestCount() {
	return skippedTestCount;
    }


    @Exported(name = "package")
    public Collection<PackageResult> getPackageList() {
	return packageMap.values();
    }


    /**
    * Adds only the <test>s that already aren't part of the list
    * @param classList
    */
    @Override
    public void addUniqueTests(List<TestResult> testList) {
	Set<TestResult> tmpSet = new HashSet<TestResult>(this.testList);
	tmpSet.addAll(testList);
	this.testList = new ArrayList<TestResult>(tmpSet);
    }

    @Override
    public void setOwner(AbstractBuild<?, ?> owner) throws IllegalArgumentException{
	
	if(owner instanceof MatrixRun ){
	    this.owner = owner;
	} else{
	    throw new IllegalArgumentException("Owner of MatrixRunTestResuls"+ 
		    " must be matrix run");
	}
	
	for (TestResult _test : testList) {
	    _test.setOwner(owner);
	}
	for (PackageResult pkg : packageMap.values()) {
	    pkg.setOwner(owner);
	}
    }
    
    @Override
    public String toString() {
      return String.format("TestResults {name='%s', totalTests=%d, " +
          "failedTests=%d, skippedTests=%d}", name, totalTestCount, failedTestCount,
          skippedTestCount);
    }
    
    /**
     * Updates the calculated fields
     */
    @Override
    public void tally() {
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



    @Override
    public Set<String> getPackageNames() {
	return packageMap.keySet();
    }

    @Override
    public Map<String, PackageResult> getPackageMap() {
	return packageMap;
    }

    public int getFailedConfigCount() {
	return failedConfigCount;
    }
    
    public int getSkippedConfigCount(){
	return skippedConfigCount;
    }

    @Override
    public List<MethodResult> getFailedConfigs() {
	return failedConfigurationMethods;
    }

    @Override
    public List<MethodResult> getSkippedConfigs() {
	return skippedConfigurationMethods;
    }

    @Override
    public boolean isMatrixBuildTestResult() {
	return false;
    }

    /**
     * This method is implemented here for consistency
     * 
     * @return List with self as only member
     */
    @Override
    public List<TestResults> getRunResults() {
	List<TestResults> runTestResults = new ArrayList<TestResults>();
	runTestResults.add(this);
	return runTestResults;
    }
    
    @Override
    public List<String> getRuns() {
	List<String> runs = new ArrayList<String>();
	runs.add(owner.toString());
	return runs;
    }
    
    @Override
    public boolean isRunTestResult() {
	return true;
    }

    

}
