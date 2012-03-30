/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser.results;

import java.util.Date;
import java.util.List;
import org.kohsuke.stapler.export.Exported;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class MethodResult extends BaseResult {
    private final String status;
    private final String description;
    private final String testInstanceName;
    private final String parentTestName;
    private final String parentSuiteName;
    private String testUuid;
    private final Date startedAt;
    private long duration;    
    private boolean isConfig;
    
    /**
    * unique id for this tests's run (helps associate the test method with
    * related configuration methods)
    */
    private final String testRunId;
    private List<String> groups;
    private List<String> parameters;
    private MethodResultException exception;
    
    public MethodResult(String name,
            String status,
            String description,
            String duration,
            Date startedAt,
            String isConfig,
            String testRunId,
            String parentTestName,
            String parentSuiteName,
            String testInstanceName)
   {
      super(name);
      this.status = status;
      this.description = description;
      // this uuid is used later to group the tests and config-methods together
      this.testRunId = testRunId;
      this.testInstanceName = testInstanceName;
      this.parentTestName = parentTestName;
      this.parentSuiteName = parentSuiteName;
      this.startedAt = startedAt;

      try {
         this.duration = Long.parseLong(duration);
      } catch (NumberFormatException e) {
         System.err.println("Unable to parse duration value: " + duration);
      }

      if (isConfig != null) {
         /*
          * If is-config attribute is present on test-method,
          * it's always set to true
          */
         this.isConfig = true;
      }
   }
    
    public boolean isConfig() {
	return isConfig;
    }

	public String getTestInstanceName() {
	return testInstanceName;
    }

    public String getParentTestName() {
	return parentTestName;
    }

    public String getParentSuiteName() {
	return parentSuiteName;
    }

    public String getTestRunId() {
	return testRunId;
    }

    @Exported
    public Date getStartedAt() {
	return startedAt;
    }
    
    @Exported
    public long getDuration() {
	return duration;
    }

    @Exported(visibility = 9)
    public String getStatus() {
	return status;
    }
    
    @Exported
    public String getDescription() {
	return description;
    }
    
    public String getTestUuid() {
	return testUuid;
    }

    public void setTestUuid(String testUuid) {
	this.testUuid = testUuid;
    }
    
    @Exported
    public List<String> getGroups() {
	return groups;
    }

    @Exported
    public List<String> getParameters() {
	return parameters;
    }
    
    public void setGroups(List<String> groups) {
	this.groups = groups;
    }

    public void setParameters(List<String> parameters) {
	this.parameters = parameters;
    }
    
    public void setException(MethodResultException exception) {
	this.exception = exception;
    }
    
}
