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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FeedsActivity extends AppCompatActivity {

    private RecyclerView feedsRecycler;
    private BottomNavigationView bottomNavigationView;

    private DatabaseReference postsDatabase;
    private DatabaseReference bookmarkDatabase;
    private DatabaseReference userDatabaseRef;
    private FirebaseUser currentUser;

    private String username, userimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        feedsRecycler = findViewById(R.id.recycler_feeds);
        feedsRecycler.setLayoutManager(new LinearLayoutManager(this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        postsDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        bookmarkDatabase = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(currentUser.getUid());
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("Name").getValue(String.class);
                userimage = dataSnapshot.child("Image").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user:
                        break;

                    case R.id.bookmark:
                        Intent intent1 = new Intent(FeedsActivity.this, BookmarkActivity.class);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.upload:
                        Intent intent3 = new Intent(FeedsActivity.this, UploadActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.toolbar_logout :
                logOutAccount();
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostsData> options =
                new FirebaseRecyclerOptions.Builder<PostsData>()
                .setQuery(postsDatabase, PostsData.class)
                .build();

        FirebaseRecyclerAdapter<PostsData, CustomViewHolder> FBRA = new FirebaseRecyclerAdapter<PostsData, CustomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position, @NonNull final PostsData model) {
                final String type = model.getType();
                final String postID = getRef(position).getKey();
                final boolean[] bookmarked = new boolean[1];
                final boolean[] liked = new boolean[1];
                final int likes = model.getLikes();
                final DatabaseReference currentRef = getRef(position);

                if(type.equals("image")){

                    holder.setTitle(model.getName());
                    holder.setImage(model.getImage());

                }else {

                    holder.post_image.setVisibility(View.GONE);
                    holder.text_image.setVisibility(View.VISIBLE);
                    holder.setTextPost(model.getName());
                }

                bookmarkDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(postID)){
                            bookmarked[0] = true;
                            holder.markbtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                        }else {
                            bookmarked[0] = false;
                            holder.markbtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                currentRef.child("Liked by").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(currentUser.getUid())){
                            liked[0] = true;
                            holder.bulbbtn.setBackgroundResource(R.drawable.bulbstateon);
                        }else{
                            liked[0] = false;
                            holder.bulbbtn.setBackgroundResource(R.drawable.bulbstateoff);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.markbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Add image to Bookmark Node
                        if(!bookmarked[0]){
//                            holder.markbtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);

                            if(type.equals("image")){
                                bookmarkDatabase.child(postID).child("image").setValue(model.getImage());
                                bookmarkDatabase.child(postID).child("name").setValue(model.getName());
                                bookmarkDatabase.child(postID).child("type").setValue("image");
                            }else{
                                bookmarkDatabase.child(postID).child("name").setValue(model.getName());
                                bookmarkDatabase.child(postID).child("type").setValue("text");
                            }
                        }else {
                            bookmarkDatabase.child(postID).removeValue();
                        }
                    }
                });

                holder.bulbbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(liked[0]){

                            currentRef.child("likes").setValue(likes-1);
                            currentRef.child("Liked by").child(currentUser.getUid()).removeValue();
                        }else {

                            currentRef.child("likes").setValue(likes+1);
                            currentRef.child("Liked by").child(currentUser.getUid()).child("date").setValue("date");
                        }
                    }
                });

                holder.commentsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(FeedsActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("POST ID", getRef(position).getKey());
                        commentsIntent.putExtra("USERNAME", username);
                        commentsIntent.putExtra("USERIMAGE", userimage);
                        startActivity(commentsIntent);
                    }
                });
            }

            @NonNull
            @Override
            public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

                return new CustomViewHolder(view);
            }
        };

        FBRA.startListening();
        feedsRecycler.setAdapter(FBRA);
    }

    public void logOutBtnClicked(View view) {
       /* FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(FeedsActivity.this, LoginActivity.class));
        finish();*/
        startActivity(new Intent(FeedsActivity.this, UploadActivity.class));
    }

    private void logOutAccount(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(FeedsActivity.this, LoginActivity.class));
        finish();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        Button bulbbtn = itemView.findViewById(R.id.bulbbtn);
        Button markbtn = itemView.findViewById(R.id.btnmark);
        Button commentsBtn = itemView.findViewById(R.id.btn_comments);
        LinearLayout post_image, text_image;

        public CustomViewHolder(View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.imageLayout);
            text_image = itemView.findViewById(R.id.text_posts);
        }

        public void setTitle(String text)
        {
            TextView postTitle = itemView.findViewById(R.id.postTitle);
            postTitle.setText(text);
        }

        public void setImage(String image){
            ImageView imageView = itemView.findViewById(R.id.image_posts);
            Picasso.get().load(image).into(imageView);
        }

        public void setTextPost(String text){
            TextView textView = itemView.findViewById(R.id.text_image_textView);
            textView.setText(text);
        }
    }
}
