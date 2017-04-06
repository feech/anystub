package org.anystub.IT;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

/**
 * Created by Kirill on 9/3/2016.
 */
@Component
public class SourceSystem {

    private final String url;

    public SourceSystem(String externalSystemUrl) {
        this.url = externalSystemUrl;
    }

    public String get() throws IOException {
        URL myURL = new URL(url);
        URLConnection myURLConnection = myURL.openConnection();
        myURLConnection.connect();


        InputStream inputStream = myURLConnection.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining());
    }

    private int random = 0;

    public Integer rand(int digit) {
        return digit + random++;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() throws URISyntaxException {
        URI uri = new URI(getUrl());
        return uri.getPath();
    }
}
