package com.example.group8_hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group8_hw07.databinding.FragmentShoppingListsBinding;
import com.example.group8_hw07.databinding.ListLineItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShoppingListsFragment extends Fragment {
    private static final String TAG = "SHOPPING LISTS FRAGMENT";
    ShoppingListsFragment.ShoppingListsFragmentListener mListener;
    FragmentShoppingListsBinding binding;
    private FirebaseAuth mAuth;
    ArrayList<List> lists = new ArrayList<>();
    ArrayList<Item> itemsList = new ArrayList<>();
    ArrayList<List> userLists = new ArrayList<>();

    ShoppingListAdapter shoppingListAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerViewUserLists, recyclerViewSharedLists;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ShoppingListsFragment() {
        // Required empty public constructor
    }

    public static ShoppingListsFragment newInstance(String param1, String param2) {
        ShoppingListsFragment fragment = new ShoppingListsFragment();
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
        Log.d(TAG, "Current User: id = " + user.getUid() + " name = " + user.getDisplayName());
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingListsBinding.inflate(inflater, container, false);

        getData();

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mListener.goToLogin();
            }
        });

        binding.buttonNewShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateNewShoppingList();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Shopping Lists");
    }

    class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
        ArrayList<List> mLists;

        public ShoppingListAdapter(ArrayList<List> data) {
            this.mLists = data;
        }

        @NonNull
        @Override
        public ShoppingListAdapter.ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListLineItemBinding binding = ListLineItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ShoppingListViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListViewHolder holder, int position) {
            List list = mLists.get(position);
            holder.setupUI(list);
        }

        @Override
        public int getItemCount() {
            return this.mLists.size();
        }

        public class ShoppingListViewHolder extends RecyclerView.ViewHolder {
            ListLineItemBinding mBinding;
            List mList;
            int position;


            public ShoppingListViewHolder(@NonNull ListLineItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(List list) {
                mList = list;
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                Log.d(TAG, "user id: " + userId);
                Log.d(TAG, "creator id:  " + mList.creatorId);

                mBinding.textViewListName.setText(mList.listName);

                if (userId.matches(mList.creatorId)) {
                    mBinding.imageFilterViewTrash.setImageResource(R.drawable.trash_bin);
                    mBinding.imageFilterViewTrash.setVisibility(View.VISIBLE);
                    mBinding.imageFilterViewTrash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("lists")
                                    .document(mList.listId)
                                    .delete();
                            getData();
                        }
                    });
                } else {
                    mBinding.imageFilterViewTrash.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lists")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        lists.clear();
                        itemsList.clear();
                        userLists.clear();
                        for (QueryDocumentSnapshot document: value) {
                            List list = new List();
                            String docId = document.getId();
                            mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            list.setListId(document.getId());
                            list.setListName(document.getString("name"));
                            list.setCreatorId(document.getString("creatorId"));
                            list.setCreator(document.getString("creator"));
                            ArrayList<Item> items = document.toObject(List.class).items;
                            for (Item item : items) {
                                itemsList.add(item);
                            }

                            Log.d(TAG, "Items in Item List Array => " + itemsList);
                            list.items.addAll(itemsList);
                            Log.d(TAG, "List added ************* " + list.items);
                            lists.add(list);

                            if (userId.matches(list.creatorId)) {
                                userLists.add(list);
                            }
                            Log.d(TAG, "User Lists " + userLists);
                        }
                        Log.d(TAG, "List Array Items ---------> " + lists);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewUserLists = binding.recyclerViewUserLists;
                                    recyclerViewUserLists.setHasFixedSize(true);
                                    linearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerViewUserLists.setLayoutManager(linearLayoutManager);
                                    shoppingListAdapter = new ShoppingListAdapter(userLists);
                                    recyclerViewUserLists.setAdapter(shoppingListAdapter);

                                    recyclerViewSharedLists = binding.recyclerViewSharedLists;
                                    recyclerViewSharedLists.setHasFixedSize(true);
                                    linearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerViewSharedLists.setLayoutManager(linearLayoutManager);
                                    shoppingListAdapter = new ShoppingListAdapter(lists);
                                    recyclerViewSharedLists.setAdapter(shoppingListAdapter);

                                }
                            });
                        }

                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ShoppingListsFragment.ShoppingListsFragmentListener) context;
    }

    interface ShoppingListsFragmentListener {
        void goToLogin();
        void goToCreateNewShoppingList();
    }
}