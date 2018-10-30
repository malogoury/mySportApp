package ch.epfl.malogouryduroslan.mysportapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    protected Profile userProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentLoginActivity = getIntent();
        userProfile = (Profile) intentLoginActivity.getSerializableExtra("userProfile");

        TextView userName = findViewById(R.id.userName);
        userName.setText("Welcome " + userProfile.getUsername() + "!");
        ImageView userPic = findViewById(R.id.userPic);
        userPic.setImageBitmap(userProfile.getPhotoBitmap());

        sendProfileToWatch();
    }

    private void sendProfileToWatch() {
        Intent intentWear = new Intent(MainActivity.this,WearService.class);
        intentWear.setAction(WearService.ACTION_SEND.PROFILE_SEND.name());
        intentWear.putExtra(WearService.PROFILE,userProfile);
        startService(intentWear);
    }
}
