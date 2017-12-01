package com.pex.image.output;

import java.util.Map;

/**
 * Created by rhernandez on 11/30/17.
 */
public class OutputEvent {
    private String output;
    private Map contextMap;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Map getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map contextMap) {
        this.contextMap = contextMap;
    }
}
