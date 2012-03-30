/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser.results;

import hudson.model.AbstractBuild;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.stapler.export.Exported;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class PackageResult extends BaseResult{
    private int fail;
    private long duration;
    private int skip;
    private int total;
    private List<ClassResult> classList = new ArrayList<ClassResult>();
    
    public PackageResult(String name){
	super(name);
    }
    
    public void setOwner(AbstractBuild<?, ?> owner) {
	super.setOwner(owner);
	for (ClassResult _class : classList) {
	    _class.setOwner(owner);
	}
    }
    
    @Exported(name = "classs") // because stapler notices suffix 's' and remove it
    public List<ClassResult> getClassList() {
	return classList;
    }
    
    @Exported
   public long getDuration() {
      return duration;
   }

   @Exported(visibility = 9)
   public int getFail() {
      return fail;
   }

   @Exported(visibility = 9)
   public int getSkip() {
      return skip;
   }

   @Exported(visibility = 9)
   public int getTotal() {
      return total;
   }
   
   
   
   public void tally() {
      duration = 0;
      fail = 0;
      skip = 0;
      total = 0;
      for (ClassResult _c : classList) {
          _c.setParent(this);
          _c.tally();
         duration += _c.getDuration();
         fail += _c.getFail();
         skip += _c.getSkip();
         total += _c.getTotal();
      }
   }
   
   
}
