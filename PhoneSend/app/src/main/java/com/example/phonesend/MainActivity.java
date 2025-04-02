package com.example.phonesend;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editText = findViewById(R.id.phone_text);

        if (editText != null)
            editText.setOnEditorActionListener
                    (new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_SEND) {
                                dialNumber();
                                handled = true;
                            }
                            return handled;
                        }

                        private void dialNumber() {
                            // Find the editText_main view.
                            EditText editText = findViewById(R.id.phone_text);
                            String phoneNum = null;
                            // If the editText field is not null,
                            // concatenate "tel: " with the phone number string.
                            if (editText != null) phoneNum = "tel:" +
                                    editText.getText().toString();
                            // Optional: Log the concatenated phone number for dialing.
                            Log.d(TAG, "dialNumber: " + phoneNum);
                            // Specify the intent.
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            // Set the data for the intent as the phone number.
                            intent.setData(Uri.parse(phoneNum));
                            // If the intent resolves to a package (app),
                            // start the activity with the intent.
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Log.d("ImplicitIntents", "Can't handle this!");
                            }
                        }

                    });
    }
}