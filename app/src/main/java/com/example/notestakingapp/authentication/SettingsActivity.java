package com.example.notestakingapp.authentication;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.notestakingapp.firebase.FirebaseAuthHandler;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import com.example.notestakingapp.R;

public class SettingsActivity extends AppCompatActivity {
    TextView backButton;
    RelativeLayout profile, editProfileButton, signInButton, signUpButton, changePasswordButton, signOutButton;
    SwitchCompat darkModeSwitch, notificationsSwitch;
    private FirebaseAuthHandler authHandler;
    boolean isNightModeOn;
    SharedPreferences sharedThemePreferences;
    SharedPreferences.Editor themeEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedUserPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedUserPreferences.getString("userEmail", "No Email");
        TextView textProfileName = findViewById(R.id.text_profile_name);
        textProfileName.setText(userEmail);

        initUI(); // initialize UI components
        authHandler = new FirebaseAuthHandler(); // initialize FirebaseAuthHandler

        // methods for buttons
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sharedThemePreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        isNightModeOn = sharedThemePreferences.getBoolean("night", false);
        darkModeSwitch.setChecked(isNightModeOn);

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    themeEditor = sharedThemePreferences.edit();
                    themeEditor.putBoolean("night", false);
                    isNightModeOn = false;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    themeEditor = sharedThemePreferences.edit();
                    themeEditor.putBoolean("night", true);
                    isNightModeOn = true;
                }
                themeEditor.apply();
                darkModeSwitch.setChecked(isNightModeOn);
                recreate();
            }
        });
        notificationsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmSignOut(SettingsActivity.this);
            }
        });
    }

    public void showConfirmSignOut(Context context) {
        final Dialog dialog = new Dialog(context);
        SharedViewModel sharedViewModel = new SharedViewModel();
        if (context instanceof MainActivity) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((MainActivity) context);
            sharedViewModel = viewModelProvider.get(SharedViewModel.class);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_confirm_sign_out);
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        RelativeLayout backButton = dialog.findViewById(R.id.sign_out_back_button);
        RelativeLayout confirmSignOutButton = dialog.findViewById(R.id.confirm_sign_out_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirmSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authHandler.signOut(SettingsActivity.this);
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // Show a toast message
                    Toast.makeText(SettingsActivity.this, "Sign out successful!", Toast.LENGTH_SHORT).show();

                    // redirect to login screen or main screen
                    Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // finish the current activity
                    finish();
                } else {
                    // Show a toast message indicating sign out failed
                    Toast.makeText(SettingsActivity.this, "Sign out failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(context instanceof MainActivity && listNoteIdChecked != null)
                    listNoteIdChecked.clear();
            }
        });
    }
    public void initUI() {
        backButton = findViewById(R.id.back_button);
        profile = findViewById(R.id.profile);
        editProfileButton = findViewById(R.id.edit_profile_button);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        signInButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        changePasswordButton = findViewById(R.id.change_password_button);
        signOutButton = findViewById(R.id.sign_out_button);
    }
}