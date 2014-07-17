package com.mycila.maven.plugin.license;

import org.apache.maven.plugins.annotations.Parameter;

public class HeaderSection {

    @Parameter
    String key;

    @Parameter
    String defaultValue;

    @Parameter
    String ensureMatch;

    @Parameter
    boolean multiLineMatch = false;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getEnsureMatch() {
        return ensureMatch;
    }

    public void setEnsureMatch(String ensureMatch) {
        this.ensureMatch = ensureMatch;
    }

    public boolean isMultiLineMatch() {
        return multiLineMatch;
    }

    public void setMultiLineMatch(boolean multiLineMatch) {
        this.multiLineMatch = multiLineMatch;
    }
}
