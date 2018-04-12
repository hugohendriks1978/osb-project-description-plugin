package nl.redrock.maven.plugins.servicebus;

import junit.framework.TestCase;

import org.junit.Test;

public class VersionInformationMojoTest extends TestCase {
    
    @Test
    public void testMojo() throws Exception {
        VersionInformationMojo mojo = new VersionInformationMojo();
        mojo.setSbConfigLocation("D:\\Workspace\\Java\\version-information\\target");
        mojo.setProjectName("BerichtenPortaalAdapter");
        mojo.setDescription("1.2.3.0");
        mojo.execute();
    }
    
    @Test
    public void testLongProjectNameMojo() throws Exception {
        VersionInformationMojo mojo = new VersionInformationMojo();
        mojo.setSbConfigLocation("D:\\Workspace\\Java\\version-information\\target");
        mojo.setProjectName("BerichtenPortaalAdapter111111111111111111");
        mojo.setDescription("1.2.3.0");
        mojo.execute();
    }
}
