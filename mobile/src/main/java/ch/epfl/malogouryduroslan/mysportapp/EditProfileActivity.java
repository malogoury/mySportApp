package ch.epfl.malogouryduroslan.mysportapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditProfileActivity extends AppCompatActivity {



    private static final int PICK_IMAGE = 1;
    private File imageFile;
    private Profile userProfile;
    private String userID;
    private String USER_PROFILE = "USER_PROFILE";

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference(Profile.FB_tag);
    private static DatabaseReference profileRef = profileGetRef.push();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        userID = intent.getStringExtra(MyProfileFragment.USER_ID);
        if (userID != null) {
            fetchDataFromFirebase();
        }
    }

    private void fetchDataFromFirebase() {
        final TextView usernameTextView = findViewById(R.id.editUsername);
        final TextView passwordTextView = findViewById(R.id.editPassword);
        final TextView heightTextView = findViewById(R.id.editHeight);
        final TextView weightTextView = findViewById(R.id.editWeight);

        profileGetRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_db = dataSnapshot.child("username").getValue(String.class);
                String password_db = dataSnapshot.child("password").getValue(String.class);
                int height_db = dataSnapshot.child("height").getValue(int.class);
                float weight_db = dataSnapshot.child("weight").getValue(float.class);
                String photo = dataSnapshot.child("photo").getValue(String.class);

                usernameTextView.setText(user_db);
                passwordTextView.setText(password_db);
                heightTextView.setText(String.valueOf(height_db));
                weightTextView.setText(String.valueOf(weight_db));

                //  Reference to an image file in Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl
                        (photo);
                storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        final Bitmap selectedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);
                        ImageView imageView = findViewById(R.id.userImage);
                        imageView.setImageBitmap(selectedImage);
                    }
                });


                profileRef = profileGetRef.child(userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileToEdit() {
        ImageView userImageView = findViewById(R.id.userImage);
        TextView usernameTextView = findViewById(R.id.editUsername);
        TextView passwordTextView = findViewById(R.id.editPassword);
        TextView heightTextView = findViewById(R.id.editHeight);
        TextView weightTextView = findViewById(R.id.editWeight);

        userImageView.setImageBitmap(userProfile.getPhotoBitmap());
        usernameTextView.setText(userProfile.username);
        passwordTextView.setText(userProfile.password);
        heightTextView.setText(String.valueOf(userProfile.height_cm));
        weightTextView.setText(String.valueOf(userProfile.weight_kg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                clearUser();
                break;
            case R.id.action_validate:
                editUser();
                addProfileToFirebaseDB();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Callback when "done" button is clicked
    // Save all data in userProfile class
    // Starts the intent to pass userProfile to loginActivity
    public void editUser () {
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
    }

    private void addProfileToFirebaseDB() {
        // Store image into byte [] array
        if (userProfile.photoPath == null)
        {
            Toast.makeText(EditProfileActivity.this, R.string.missing_picture,Toast.LENGTH_SHORT).show();
        }
        Bitmap bitmap_img = userProfile.getPhotoBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap_img.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte [] data_img = baos.toByteArray();

        // Upload byte_img []
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = storageRef.child("photos").child(profileRef.getKey() + ".jpg");
        UploadTask uploadTask = photoRef.putBytes(data_img);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this,
                        R.string.upload_pic_notok, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new PhotoUploadSuccessListener());
    }

    // Called when the "clear" button from the action bar is pressed
    private void clearUser() {
        ImageView userImageView = findViewById(R.id.userImage);
        TextView usernameTextView = findViewById(R.id.editUsername);
        TextView passwordTextView = findViewById(R.id.editPassword);
        TextView heightTextView = findViewById(R.id.editHeight);
        TextView weightTextView = findViewById(R.id.editWeight);

        userImageView.setImageDrawable(null);
        usernameTextView.setText("");
        passwordTextView.setText("");
        heightTextView.setText("");
        weightTextView.setText("");
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

    private class PhotoUploadSuccessListener implements
            OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    userProfile.photoPath = uri.toString();
                    profileRef.runTransaction(new ProfileDataUploadHandler());
                }
            });
        }
    }

    private class ProfileDataUploadHandler implements Transaction.Handler {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            mutableData.child("username").setValue(userProfile.username);
            mutableData.child("password").setValue(userProfile.password);
            mutableData.child("height").setValue(userProfile.height_cm);
            mutableData.child("weight").setValue(userProfile.weight_kg);
            mutableData.child("photo").setValue(userProfile.photoPath);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                               @Nullable DataSnapshot dataSnapshot) {
            if (b) {
                Toast.makeText(EditProfileActivity.this, R.string.toast_upload_FB_ok, Toast.LENGTH_SHORT).show();
                // Pass userProfile back to loginActivity
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.putExtra(USER_PROFILE, userProfile);
                setResult(AppCompatActivity.RESULT_OK,intent);
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, R.string.toast_upload_FB_notok, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
