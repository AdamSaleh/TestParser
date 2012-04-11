/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser;

import com.redhat.engineering.jenkins.testparser.results.PackageResult;
import com.redhat.engineering.jenkins.testparser.results.TestResults;
import hudson.FilePath;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class ParserTest {
    
    public ParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
   public void testTestngXmlWithExistingResultXml() {
      String filename = "sample-testng-results.xml";
      URL resource = ParserTest.class.getClassLoader().getResource(filename);
      assertNotNull(resource);
      TestResults results = (TestResults)getResults(resource.getFile());
      assertFalse("Collection shouldn't have been empty", results.getTestList().isEmpty());
      results.tally();
      assertEquals(1,results.getFailedTestCount());
      assertEquals(1,results.getFailedConfigs().size());

      assertEquals("testSetUp", results.getFailedConfigs().get(0).getName());
      assertEquals("test", results.getFailedTests().get(0).getName());
      assertEquals("Test all FT specific API\'s on BAT setup. This test calls FT APIs createSecondary, disableSecondary, enableSecondary and removeSecondary on a VM in powered off state and powers on the VM after calling each of these APIs to verify FT functionality",
                   results.getFailedTests().get(0).getDescription());

      
    }

   @Test
   public void testTestngXmlWithSameTestNameDiffSuites() {
      String filename = "testng-results-same-test.xml";
      URL resource = ParserTest.class.getClassLoader().getResource(filename);
      junit.framework.Assert.assertNotNull(resource);
      TestResults results = (TestResults)getResults(resource.getFile());
      junit.framework.Assert.assertFalse("Collection shouldn't have been empty", results.getTestList().isEmpty());
      junit.framework.Assert.assertEquals(2, results.getTestList().size());
      results.tally();
      junit.framework.Assert.assertEquals(1, results.getPackageNames().size());
      junit.framework.Assert.assertEquals(3, results.getPackageMap().values().iterator().next().getClassList().size());
      junit.framework.Assert.assertEquals(4, results.getPassedTestCount());
      junit.framework.Assert.assertEquals(4, results.getPassedTests().size());
   }

   @Test
   public void testTestngXmlWithExistingResultXmlGetsTheRightDurations() {
      String filename = "sample-testng-dp-result.xml";
      URL resource = ParserTest.class.getClassLoader().getResource(filename);
      junit.framework.Assert.assertNotNull(resource);
      TestResults results = (TestResults)getResults(resource.getFile());
      junit.framework.Assert.assertFalse("Collection shouldn't have been empty", results.getTestList().isEmpty());

      // This test assumes that there is only 1 package in
      // sample-testng-dp-result that contains tests that add to 12 ms
      results.tally();
      Map<String, PackageResult> packageResults = results.getPackageMap();
      for(PackageResult result: packageResults.values()) {
        junit.framework.Assert.assertEquals("org.farshid", result.getName());
        junit.framework.Assert.assertEquals(12, result.getDuration());
      }
   }

   @Test
   public void testTestngXmlWithNonExistingResultXml() {
      String filename = "/invalid/path/to/file/new-test-result.xml";
      TestResults results = (TestResults)getResults(filename);
      junit.framework.Assert.assertTrue("Collection should have been empty. Number of results : "
               + results.getTestList().size(), results.getTestList().isEmpty());
   }

   @Test
   public void parseTestNG() {
      ClassLoader cl = ParserTest.class.getClassLoader();
      TestResults results = (TestResults)getResults(cl.getResource("testng-results-testng.xml").getFile());
      results.tally();
   }

   @Test
   public void testParseEmptyException() {
      ClassLoader cl = ParserTest.class.getClassLoader();
      TestResults results = (TestResults)getResults(cl.getResource("sample-testng-empty-exp.xml").getFile());
      results.tally();
      junit.framework.Assert.assertEquals(1, results.getPassedTestCount());
   }

   private TestResults getResults(String filename) {
      Parser parser = new Parser();
      FilePath[] filePaths = new FilePath[1];
      filePaths[0] = new FilePath(new File(filename));
      return parser.parse(filePaths, true);
   }

   @Test
   public void testDateParser() throws ParseException {
      //example of date format used in testng report
      String dateString = "2010-07-20T11:49:17Z";
      SimpleDateFormat sdf = new SimpleDateFormat(Parser.DATE_FORMAT);
      sdf.parse(dateString);
   }
   
   /**
     * Test of locateReports method, of class ReportPluginPublisher.
     * 
     * @author nullin
     */
    @Test
    public void testLocateReports() throws Exception {
	
	// Create a temporary workspace in the system
	File w = File.createTempFile("workspace", ".test");
	w.delete();
	w.mkdir();
	w.deleteOnExit();
	FilePath workspace = new FilePath(w);
	// Create 4 files in the workspace
	File f1 = File.createTempFile("testng-results", ".xml", w);
	f1.deleteOnExit();
	File f2 = File.createTempFile("anyname", ".xml", w);
	f2.deleteOnExit();
	File f3 = File.createTempFile("testng-results", ".xml", w);
	f3.deleteOnExit();
	File f4 = File.createTempFile("anyname", ".xml", w);
	f4.deleteOnExit();
	// Create a folder and move there 2 files
	File d1 = new File(workspace.child("subdir").getRemote());
	d1.mkdir();
	d1.deleteOnExit();
	File f5 = new File(workspace.child(d1.getName()).child(f3.getName()).getRemote());
	File f6 = new File(workspace.child(d1.getName()).child(f4.getName()).getRemote());
	f3.renameTo(f5);
	f4.renameTo(f6);
	f5.deleteOnExit();
	f6.deleteOnExit();
	// Look for files in the entire workspace recursively without providing
	// the includes parameter
	FilePath[] reports = Parser.locateReports(workspace, "**/testng*.xml");
	junit.framework.Assert.assertEquals(2, reports.length);
	// Generate a includes string and look for files
	String includes = f1.getName() + "; " + f2.getName() + "; " + d1.getName();
	reports = Parser.locateReports(workspace, includes);
	junit.framework.Assert.assertEquals(3, reports.length);
	// Save files in local workspace
	FilePath local = workspace.child("coverage_localfolder");
	boolean saved = Parser.saveReports(local, reports, System.out, "test");
	junit.framework.Assert.assertTrue(saved);
	junit.framework.Assert.assertEquals(3, local.list().size());
	local.deleteRecursive();
    }
    
    @Test
	public void testSaveReports() throws Exception{
	    // Create a temporary workspace in the system
	    File w = File.createTempFile("workspace", ".test");
	    w.delete();
	    w.mkdir();
	    w.deleteOnExit();
	    FilePath workspace = new FilePath(w);
	    // Create file in the workspace
	    File f = File.createTempFile("testng-results", ".xml", w);
	    f.deleteOnExit();
	    FilePath path = new FilePath(f);
	    // Save files in local workspace
	    FilePath local = workspace.child("localfolder");

	    FilePath[] files = new FilePath[1];
	    files[0]= path;
	    boolean saved = Parser.saveReports(local, files, System.out, "test");
	    junit.framework.Assert.assertTrue(saved);
	    local.deleteRecursive();
	}
   
}
