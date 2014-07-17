
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.header.Header;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Check if the source files of the project have a valid license header
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public final class LicenseCheckMojo extends AbstractLicenseMojo {

    public final Collection<File> missingHeaders = new ConcurrentLinkedQueue<File>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Checking licenses...");
        missingHeaders.clear();

        execute(new Callback() {
            @Override
            public void onHeaderNotFound(Document document, Header header) {
                warn("Missing header in: %s", document.getFile());
                missingHeaders.add(document.getFile());
            }

            @Override
            public void onExistingHeader(Document document, Header header) {
                debug("Header OK in: %s", document.getFile());
            }
        });

        if (!missingHeaders.isEmpty()) {
            if (failIfMissing) {
                throw new MojoExecutionException("Some files do not have the expected license header");
            }
            getLog().warn("Some files do not have the expected license header");
        }
    }

}
