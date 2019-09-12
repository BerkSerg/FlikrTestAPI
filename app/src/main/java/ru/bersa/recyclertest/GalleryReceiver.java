package ru.bersa.recyclertest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GalleryReceiver {
    public static String GetGalery(String gallery_id) {
        BufferedReader br = null;
        //://www.flickr.com/services/rest/?method=flickr.galleries.getPhotos&api_key=41fb89318c297e35e7010444fd56d993&gallery_id=66911286-72157647277042064&format=json&nojsoncallback=1

        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("www.flickr.com")
                .appendPath("services")
                .appendPath("rest")
                .appendQueryParameter("method","flickr.galleries.getPhotos")
                .appendQueryParameter("api_key","41fb89318c297e35e7010444fd56d993")
                .appendQueryParameter("gallery_id",gallery_id)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1")
                .build();

        try {
            URL url = new URL(uri.toString());
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            br = new BufferedReader((new InputStreamReader(con.getInputStream())));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static Bitmap getBmp(String bmpUrl){
        try {
            URL url = new URL(bmpUrl);
            InputStream is = url.openStream();
            Bitmap bmp = BitmapFactory.decodeStream(is);
            return bmp;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
