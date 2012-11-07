/*******************************************************************************
 * Copyright (c) 2012 Sonatype Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype Inc. - initial API and implementation
 *******************************************************************************/
package com.techadvantage.tycho.extras.buildtimestamp.svnkit;

import java.util.Date;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.tycho.buildversion.BuildTimestampProvider;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Build timestamp provider that returns date of the most recent commit that touches any file under
 * project basedir. File additional flexibility, some files can be ignored using gitignore patters
 * specified in &ltjgit.ignore> element of tycho-packaging-plugin configuration block
 * 
 * <p>
 * Typical usage
 * 
 * <pre>
 * ...
 *       &lt;plugin>
 *         &lt;groupId>org.eclipse.tycho&lt;/groupId>
 *         &lt;artifactId>tycho-packaging-plugin&lt;/artifactId>
 *         &lt;version>${tycho-version}&lt;/version>
 *         &lt;dependencies>
 *           &lt;dependency>
 *             &lt;groupId>org.eclipse.tycho.extras&lt;/groupId>
 *             &lt;artifactId>tycho-buildtimestamp-jgit&lt;/artifactId>
 *             &lt;version>${tycho-version}&lt;/version>
 *           &lt;/dependency>
 *         &lt;/dependencies>
 *         &lt;configuration>
 *           &lt;timestampProvider>jgit&lt;/timestampProvider>
 *           &lt;jgit.ignore>pom.xml&lt;/jgit.ignore>
 *         &lt;/configuration>
 *       &lt;/plugin>
 * ...
 * </pre>
 */
@Component(role = BuildTimestampProvider.class, hint = "svnkit")
public class SVNKitBuildTimestampProvider implements BuildTimestampProvider {

    public Date getTimestamp(MavenSession session, MavenProject project, MojoExecution execution)
            throws MojoExecutionException {
    	

    	DAVRepositoryFactory.setup();
    	
    	SVNClientManager client = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), getLogin(execution), getPassword(execution));
    	
   
    	try {
			return client.getWCClient().doInfo(project.getBasedir(), SVNRevision.BASE).getCommittedDate();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			throw new MojoExecutionException("Could not determine svnkit commit timestamp", e);
		}		
    }

    private String getLogin(MojoExecution execution) {
        Xpp3Dom pluginConfiguration = (Xpp3Dom) execution.getPlugin().getConfiguration();
        Xpp3Dom ignoreDom = pluginConfiguration.getChild("svnkit.login");
        if (ignoreDom == null) {
            return null;
        }
        return ignoreDom.getValue();
    }

    private String getPassword(MojoExecution execution) {
        Xpp3Dom pluginConfiguration = (Xpp3Dom) execution.getPlugin().getConfiguration();
        Xpp3Dom ignoreDom = pluginConfiguration.getChild("svnkit.password");
        if (ignoreDom == null) {
            return null;
        }
        return ignoreDom.getValue();
    }
    
    
}
