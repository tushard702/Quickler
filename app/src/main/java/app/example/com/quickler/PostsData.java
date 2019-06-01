package app.example.com.quickler;

/**
 * Created by Deep on 31-03-2018.
 */

public class PostsData {
    String name;
    String type;
    String image;//We can use this var to send URL of image stored in Database
    int likes;

    boolean isbtn = false;
    boolean ismark = false;

    //Default Constructor
    public PostsData() {
    }

    public PostsData(String name, String image, String type, int likes) {
        this.name = name;
        this.image = image;
        this.type = type;
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
