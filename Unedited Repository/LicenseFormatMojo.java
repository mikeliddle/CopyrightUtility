


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
