package com.example.group8_hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group8_hw07.databinding.FragmentNewShoppingListBinding;
import com.example.group8_hw07.databinding.FragmentShoppingListsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewShoppingListFragment extends Fragment {

    private static final String TAG = "NEW LIST FRAGMENT";
    NewShoppingListFragment.NewShoppingListFragmentListener mListener;
    FragmentNewShoppingListBinding binding;

    private FirebaseAuth mAuth;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewShoppingListFragment() {
        // Required empty public constructor
    }

    public static NewShoppingListFragment newInstance(String param1, String param2) {
        NewShoppingListFragment fragment = new NewShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewShoppingListBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (NewShoppingListFragment.NewShoppingListFragmentListener) context;
    }

    interface NewShoppingListFragmentListener {

    }
}