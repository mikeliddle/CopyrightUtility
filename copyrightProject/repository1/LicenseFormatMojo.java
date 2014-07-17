//=======================LicenseFormatMojo.java=======================//
//  Author:    Connor Group Software Development team 2014            //
//  Copyright: Copyright (C) 2014 Connor Group. All rights reserved.  //
//  Email:     it@connorgp.com                                        //
//  Website:   http://www.connorgp.com                                //
//                                                                    //
//  The copyright to the source code and computer program(s)          //
//  herein is the property of Connor Group.The source code            //
//  and program(s) may be used and/or copied only with the            //
//  written permission of Connor Group or in accordance with          //
//  the terms and conditions stipulated in the                        //
//  agreement/contract under which the source code and                //
//  program(s) have been supplied.                                    //
//                                                                    //
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.header.Header;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * Reformat files with a missing header to add it
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@Mojo(name = "format", threadSafe = true)
public final class LicenseFormatMojo extends AbstractLicenseMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Updating license headers...");

        execute(new Callback() {
            @Override
            public void onHeaderNotFound(Document document, Header header) {
                document.parseHeader();
                if (document.headerDetected()) {
                    if (skipExistingHeaders) {
                        debug("Keeping license header in: %s", document.getFile());
                        return;
                    }
                    document.removeHeader();
                }
                info("Updating license header in: %s", document.getFile());
                document.updateHeader(header);
                if (!dryRun) {
                    document.save();
                } else {
                    String name = document.getFile().getName() + ".licensed";
                    File copy = new File(document.getFile().getParentFile(), name);
                    info("Result saved to: %s", copy);
                    document.saveTo(copy);
                }
            }

            @Override
            public void onExistingHeader(Document document, Header header) {
                debug("Header OK in: %s", document.getFile());
            }
        });
    }

}
