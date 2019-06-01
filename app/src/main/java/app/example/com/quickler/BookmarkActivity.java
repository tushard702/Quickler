package app.example.com.quickler;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView bookmarkRecycler;
    private BottomNavigationView bottomNavigationView;

    private DatabaseReference bookmarkRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bookmarkRecycler = findViewById(R.id.bookmark_Recycler);
        bookmarkRecycler.setLayoutManager(new LinearLayoutManager(this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(currentUser.getUid());

        bottomNavigationView = findViewById(R.id.bottomNavView_BarB);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user:
                        Intent intent = new Intent(BookmarkActivity.this, FeedsActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.bookmark:
                        break;

                    case R.id.upload:
                        Intent intent3 = new Intent(BookmarkActivity.this, UploadActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostsData> options =
                new FirebaseRecyclerOptions.Builder<PostsData>()
                        .setQuery(bookmarkRef, PostsData.class)
                        .build();

        FirebaseRecyclerAdapter<PostsData, FeedsActivity.CustomViewHolder> FBRA = new FirebaseRecyclerAdapter<PostsData, FeedsActivity.CustomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FeedsActivity.CustomViewHolder holder, final int position, @NonNull final PostsData model) {
                final String type = model.getType();

                if(type.equals("image")){

                    holder.setTitle(model.getName());
                    holder.setImage(model.getImage());

                }else {

                    holder.post_image.setVisibility(View.GONE);
                    holder.text_image.setVisibility(View.VISIBLE);
                    holder.setTextPost(model.getName());
                }
            }

            @NonNull
            @Override
            public FeedsActivity.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

                return new FeedsActivity.CustomViewHolder(view);
            }
        };

        FBRA.startListening();
        bookmarkRecycler.setAdapter(FBRA);
    }

}
