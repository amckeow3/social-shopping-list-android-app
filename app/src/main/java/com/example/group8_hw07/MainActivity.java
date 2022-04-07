package com.example.group8_hw07;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegistrationFragment.RegistrationFragmentListener, ShoppingListsFragment.ShoppingListsFragmentListener,
        NewShoppingListFragment.NewShoppingListFragmentListener {
    private static final String TAG = "Main Activity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment(), "login-fragment")
                    .commit();
        } else {
            Log.d(TAG, "Logged in user on create MainActivity: id = " + user.getUid() + " name = " + user.getDisplayName());
            String name = user.getDisplayName();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new ShoppingListsFragment(), "shopping-lists-fragment")
                    .commit();
        }
    }

    @Override
    public void cancelRegistration() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToShoppingLists() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new ShoppingListsFragment(), "shopping-lists-fragment")
                .commit();
    }

    @Override
    public void goToAccountRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new RegistrationFragment(), "registration-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void goToCreateNewShoppingList() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new NewShoppingListFragment())
                .addToBackStack(null)
                .commit();
    }

}