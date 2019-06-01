package app.example.com.quickler;

public class CommentsData {

    String username, userimage, comment;

    public CommentsData() {
    }

    public CommentsData(String username, String userimage, String comment) {
        this.username = username;
        this.userimage = userimage;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
