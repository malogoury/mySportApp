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

        Intent intentWatch = new Intent (this, WearService.class);
        intentWatch.setAction(WearService.ACTION_SEND.MESSAGE.name());
        intentWatch.putExtra(WearService.MESSAGE, "Welcome "+userProfile.getUsername() + "!");
        intentWatch.putExtra(WearService.PATH, BuildConfig.W_example_path_text);
        startService(intentWatch);
    }
}
