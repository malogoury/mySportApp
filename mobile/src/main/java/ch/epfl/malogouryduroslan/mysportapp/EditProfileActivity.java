package ch.epfl.malogouryduroslan.mysportapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private File imageFile = null;
    private Profile userProfile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    // Callback when "done" button is clicked
    // Save all data in userProfile class
    // Starts the intent to pass userProfile to loginActivity
    public void editUser (View view) {
        TextView userName = findViewById(R.id.editUsername);
        TextView passWord = findViewById(R.id.editPassword);
        TextView height = findViewById(R.id.editWeight);
        TextView weight = findViewById(R.id.editHeight);

        userProfile = new Profile(userName.getText().toString(),
                passWord.getText().toString());

        try {
            userProfile.height_cm = Integer.valueOf(height.getText().toString());
        }
        catch (NumberFormatException e) {
            userProfile.height_cm = 0;
        }
        try {
            userProfile.weight_kg = Integer.valueOf(weight.getText().toString());
        }
        catch (NumberFormatException e) {
            userProfile.weight_kg = 0;
        }
        if (imageFile == null) {
            userProfile.photoPath = "";
        }
        else {
            userProfile.photoPath = imageFile.getPath();
        }

        // Pass userProfile back to loginActivity
        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        intent.putExtra("userProfile", userProfile);
        setResult(AppCompatActivity.RESULT_OK,intent);
        finish();
    }

    // Callback from chooseImage button from EditProfileLayout
    // Starts the activity to look for a picture
    public void chooseImage (View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
    }

    // When the Image is chosen it is saved in the imageFile variable
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageFile = new File(getExternalFilesDir(null), "profileImage");
            try {
                copyImage(imageUri, imageFile); } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream imageStream; try {
                imageStream = getContentResolver()
                        .openInputStream(Uri.fromFile(imageFile));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ImageView imageView = findViewById(R.id.userImage);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) { e.printStackTrace();
            } }
    }

    // Copy from Uri (path) to real File.
    private void copyImage(Uri uriInput, File fileOutput) throws IOException { InputStream in = null;
        OutputStream out = null;
        try {
            in = getContentResolver().openInputStream(uriInput); out = new FileOutputStream(fileOutput);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) { e.printStackTrace();
        } finally { in.close();
            out.close();
        }
    }
}
