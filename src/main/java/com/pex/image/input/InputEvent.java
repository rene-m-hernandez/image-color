package com.pex.image.input;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by rhernandez on 11/29/17.
 */
public class InputEvent {
    private InputStream inputStream;
    private String imageUrl;
    private Map contextMap;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map contextMap) {
        this.contextMap = contextMap;
    }
}
