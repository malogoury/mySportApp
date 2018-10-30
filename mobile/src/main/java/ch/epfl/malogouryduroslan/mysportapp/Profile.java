package ch.epfl.malogouryduroslan.mysportapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Profile implements Serializable {
    private static final String TAG = "Profile";

    protected String username;
    protected String password;
    protected int height_cm;
    protected float weight_kg;
    protected String photoPath;

    public Profile(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername () {
        return this.username;
    }


    public Bitmap getPhotoBitmap () {
        FileInputStream imageStream = null;
        try {
            imageStream = new FileInputStream(this.photoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap userPhotoBitmap = BitmapFactory.decodeStream(imageStream);
        return userPhotoBitmap;
    }
}
