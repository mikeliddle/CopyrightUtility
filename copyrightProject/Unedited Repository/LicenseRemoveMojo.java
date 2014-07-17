



package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.header.Header;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * Remove the specified header from source files
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@Mojo(name = "remove", threadSafe = true)
public final class LicenseRemoveMojo extends AbstractLicenseMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Removing license headers...");

        execute(new Callback() {
            @Override
            public void onHeaderNotFound(Document document, Header header) {
                debug("Header was not found in: %s (But keep trying to find another header to remove)", document.getFile());
                remove(document);
            }

            @Override
            public void onExistingHeader(Document document, Header header) {
                info("Removing license header from: %s", document.getFile());
                remove(document);
            }
        });
    }

    private void remove(Document document) {
        document.parseHeader();
        if (document.headerDetected())
            document.removeHeader();
        if (!dryRun) {
            document.save();
        } else {
            String name = document.getFile().getName() + ".licensed";
            File copy = new File(document.getFile().getParentFile(), name);
            info("Result saved to: %s", copy);
            document.saveTo(copy);
        }
    }

}
