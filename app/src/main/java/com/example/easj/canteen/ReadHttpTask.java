package com.example.easj.canteen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by anders on 2/23/2016.
 */
public class ReadHttpTask extends AsyncTask<String, Void, CharSequence> {
    @Override
    protected CharSequence doInBackground(String... urls) {
        String urlString = urls[0];
        try {
            CharSequence result = HttpHelper.GetHttpResponse(urlString);
            return result;
        } catch (IOException ex) {
            cancel(true);
            String errorMessage = ex.getMessage() + "\n" + urlString;
            Log.e("ReadHttpTask", errorMessage);
            return errorMessage;
        }
    }
}
