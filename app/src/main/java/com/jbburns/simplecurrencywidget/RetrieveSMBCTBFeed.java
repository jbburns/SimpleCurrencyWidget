package com.jbburns.simplecurrencywidget;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jbburns on 3/18/2016.
 */

public class RetrieveSMBCTBFeed extends AsyncTask {
    private String result;
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            URL url = new URL("https://www.smbctb.co.jp/common/xml/FX_INT.xml");

            InputStream inputStream = url.openStream();
            result = IOUtils.toString(inputStream);
            IOUtils.closeQuietly(inputStream);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getResult() {
        return result;
    }

}

