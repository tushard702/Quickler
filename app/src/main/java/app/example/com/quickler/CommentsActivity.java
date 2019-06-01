package app.example.com.quickler;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentsRecycler;
    private CircleImageView userImageView;
    private Button postCommentBtn;
    private TextInputLayout commentTextInput;

    private String postID, username, userimage;

    private DatabaseReference postCommentsRef;
    private DatabaseReference commentsDatabaseRef;
    private FirebaseRecyclerAdapter<CommentsData, CommentsViewHolder> CommentsRecyclerAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentsRecycler = findViewById(R.id.comments_Recycler);
        userImageView = findViewById(R.id.comments_userimage);
        postCommentBtn = findViewById(R.id.post_commentBtn);
        commentTextInput = findViewById(R.id.comment_TextInput);

        postID = getIntent().getStringExtra("POST ID");
        username = getIntent().getStringExtra("USERNAME");
        userimage = getIntent().getStringExtra("USERIMAGE");
        Picasso.get().load(userimage).placeholder(R.drawable.new_profile).into(userImageView);

        postCommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID).push();
        commentsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentTextInput.getEditText().getText().toString();
                postCommentsRef.child("username").setValue(username);
                postCommentsRef.child("userimage").setValue(userimage);
                postCommentsRef.child("comment").setValue(commentText);
                commentTextInput.getEditText().setText("");
            }
        });

        FirebaseRecyclerOptions<CommentsData> commentOptions = new
                FirebaseRecyclerOptions.Builder<CommentsData>()
                .setQuery(commentsDatabaseRef, CommentsData.class)
                .build();

        CommentsRecyclerAdap = new FirebaseRecyclerAdapter<CommentsData, CommentsViewHolder>(commentOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull CommentsData model) {
                holder.setDetails(model.getUsername(), model.getUserimage(), model.getComment());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_comment_layout, parent, false);
                return new CommentsViewHolder(view);
            }
        };

        CommentsRecyclerAdap.startListening();
        commentsRecycler.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        commentsRecycler.setAdapter(CommentsRecyclerAdap);

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(String username, String userimage, String comment){
            CircleImageView userImageView = mView.findViewById(R.id.single_comment_userimage);
            TextView userNameTextView = mView.findViewById(R.id.single_comment_username);
            TextView commentTextView = mView.findViewById(R.id.single_comment_textView);

            Picasso.get().load(userimage).placeholder(R.drawable.new_profile).into(userImageView);
            userNameTextView.setText(username);
            commentTextView.setText(comment);
        }
    }
}
