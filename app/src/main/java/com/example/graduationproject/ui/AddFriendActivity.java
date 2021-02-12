package com.example.graduationproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.Converter;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.UserFriendsAdapter;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendActivity extends AppCompatActivity implements UserFriendsAdapter.OnItemClick {

    @BindView(R.id.display_name_search)
    EditText displayNameSearch;
    @BindView(R.id.add_friend)
    Button addFriend;
    @BindView(R.id.searched_friend_recycler)
    RecyclerView searchedFriendRecycler;

    public static final String ADDED_FRIEND_INTENT_EXTRA = "addedFriend";
    private static final String TAG = "AddFriendActivity";


    private ArrayList<UserPublicInfo> friendsArrayList;
    private UserFriendsAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        initFirebase();
        init();
        initAdapter();


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAdapter();
                getSearchedFriends(displayNameSearch.getText().toString().trim());
            }
        });
    }

    public void init() {
        friendsArrayList = new ArrayList<>();
    }

    public void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public void initAdapter() {
        adapter = new UserFriendsAdapter(this, friendsArrayList, this);
        searchedFriendRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchedFriendRecycler.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .build());
        searchedFriendRecycler.setAdapter(adapter);
    }

    public void clearAdapter() {
        friendsArrayList.clear();
        adapter.notifyDataSetChanged();
    }

    public void insertToAdapter(@NonNull UserPublicInfo searchedFriend) {
        friendsArrayList.add(searchedFriend);
        int lastItem = friendsArrayList.size() - 1;
        adapter.notifyItemChanged(lastItem);
    }

    @Override
    public void onItemClick(int position) {
        setResult(RESULT_OK
                , new Intent()
                        .putExtra(ADDED_FRIEND_INTENT_EXTRA, friendsArrayList.get(position)));
        finish();
    }

    public void getSearchedFriends(String displayName) {
        //path of users Collection
        //like: "users"
        String pathOfFriendOfUserCollection = "users";
        db.collection(pathOfFriendOfUserCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //get Map(public-info) from every user
                            @SuppressWarnings("unchecked")
                            Map<String, Object> currentUser = (HashMap<String, Object>) document.get("public-info");

                            //convert map to UserPublicInfo
                            UserPublicInfo userPublicInfo = Converter.ConvertMapToUserPublicInfo(currentUser);

                            //check if matched display name
                            if (userPublicInfo.getUserDisplayName().equals(displayName)) {
                                insertToAdapter(userPublicInfo);
                            }
                        }
                    }
                });


    }

}