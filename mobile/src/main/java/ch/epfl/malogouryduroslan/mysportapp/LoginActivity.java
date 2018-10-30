package ch.epfl.malogouryduroslan.mysportapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    protected Profile userProfile = null;
    private static final int REGISTER_PROFILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = findViewById(R.id.RegisterButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEditProfile = new Intent(LoginActivity.this,
                        EditProfileActivity.class);
                startActivityForResult(intentEditProfile, REGISTER_PROFILE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_PROFILE && resultCode == RESULT_OK) {
            userProfile = (Profile) data.getSerializableExtra("userProfile");
            if(userProfile != null) {
                TextView userName = findViewById(R.id.UserName);
                userName.setText(userProfile.username);
                TextView password = findViewById(R.id.Password);
                password.setText(userProfile.password);
            }
        }
    }

    public void clickedLoginButtonXmlCallback(View view) {
        if (userProfile != null)
        {
            Intent intentMainActivity = new Intent(LoginActivity.this,
                    MainActivity.class);
            intentMainActivity.putExtra("userProfile", userProfile);
            startActivity(intentMainActivity);
        }
        else {
            TextView loginMessage = findViewById(R.id.LoginMessage);
            loginMessage.setText("You are not registered yet!");
            loginMessage.setTextColor(Color.RED);
        }
    }
}
