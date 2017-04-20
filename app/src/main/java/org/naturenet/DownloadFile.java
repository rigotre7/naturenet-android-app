package org.naturenet;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by rigot on 4/16/2017.
 */

public class DownloadFile extends AsyncTask<String, String, String>{

    private static final int MEGABYTE = 1024*1024;
    private Context mContext;
    private ProgressDialog progress;
    private String obsId;

    public DownloadFile(Context context, String id) {
        mContext = context;
        obsId = id;
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(mContext, "Download", "downloading...", true);
    }

    @Override
    protected String doInBackground(String... string) {

        try{
            URL url = new URL(string[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            //get input stream
            InputStream inputStream = connection.getInputStream();

            //get directory to store pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "NatureNet");
            folder.mkdir();

            File pdf = new File(folder, UUID.randomUUID().toString() + ".pdf");

            try{
                pdf.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }

            FileOutputStream fileOutput = new FileOutputStream(pdf);


            byte[] buffer = new byte[MEGABYTE];
            int bufferLength;

            while ((bufferLength = inputStream.read(buffer)) > 0){
                fileOutput.write(buffer, 0, bufferLength);
            }

            fileOutput.flush();
            fileOutput.close();
            inputStream.close();

            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.addCompletedDownload("NatureNet-Pdf" + obsId, "NatureNet", true, "application/pdf", pdf.toString(), pdf.length(), true);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        progress.dismiss();
    }
}
