package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.header.Header;

/**
 * <b>Date:</b> 18-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public interface Callback {
    void onHeaderNotFound(Document document, Header header);

    void onExistingHeader(Document document, Header header);
}
