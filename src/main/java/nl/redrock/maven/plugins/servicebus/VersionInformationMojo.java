package nl.redrock.maven.plugins.servicebus;

import java.io.File;
import nl.redrock.maven.plugins.misc.FileUtils;
import nl.redrock.maven.plugins.misc.XMLUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * This unzips the sbconfig.sbar, updates the <proj:description> in the
 * _projectdata.LocationData file and the re-zips it again.
 *
 * @goal version
 *
 * @requiresProject=false
 */
public class VersionInformationMojo extends AbstractMojo {

    /**
     * @parameter property="description" default-value="${project.version}"
     */
    private String description;
    /**
     * @parameter property="projectName" default-value="${project.artifactId}"
     */
    private String projectName;
    /**
     * @parameter property="sbConfigLocation" default-value="${project.basedir}/.data/maven"
     */
    private String sbConfigLocation;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        //Added because the Oracle Maven Plugin truncates the projectname to max 40 chars if it is longer.
        if(this.projectName.length() >= 40){
            this.projectName = this.projectName.substring(0, 40);
        }
        
        String sbConfigJar = sbConfigLocation + "/sbconfig.sbar";
        String extractFolder = sbConfigLocation + "/sbconfig-extract";

        //unzip the sbconfig
        FileUtils.unZipIt(sbConfigJar, new File(extractFolder));

        //update the _projectdata.LocationData file with the version in the description field
        XMLUtils.updateDescription(extractFolder + "/" + projectName + "/_projectdata.LocationData", this.description);
        //zip the file again
        FileUtils.zipIt(sbConfigJar, new File(extractFolder));

        //delete the temp extract directory
        FileUtils.delete(new File(extractFolder));

    }

    /**
     * For testing purpose
     *
     * @param aVersion
     */
    public void setDescription(String aDescription) {
        this.description = aDescription;
    }

    /**
     * For testing purpose
     *
     * @param aProjectName
     */
    public void setProjectName(String aName) {
        this.projectName = aName;
    }

    /**
     * For testing purpose
     *
     * @param sbConfigLocation the sbConfigLocation to set
     */
    public void setSbConfigLocation(String aSBConfigLocation) {
        this.sbConfigLocation = aSBConfigLocation;
    }

}
