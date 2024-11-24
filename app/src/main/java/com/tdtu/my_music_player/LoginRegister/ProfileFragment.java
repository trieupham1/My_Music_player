package com.tdtu.my_music_player.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdtu.my_music_player.R;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private TextView emailTextView;
    private EditText passwordEditText;
    private Button updatePasswordButton, logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // If user is not logged in, redirect to AuthActivity
        if (user == null) {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // Initialize UI elements
        emailTextView = view.findViewById(R.id.emailTextView);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        updatePasswordButton = view.findViewById(R.id.updatePasswordButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Set email in TextView
        emailTextView.setText("Email: " + user.getEmail());

        // Handle Update Password button click
        updatePasswordButton.setOnClickListener(v -> {
            String newPassword = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(getActivity(), "Please enter a new password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update password in Firebase
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle Logout button click
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}
