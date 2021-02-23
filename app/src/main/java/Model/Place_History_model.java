package Model;

public class Place_History_model {

    String id;
    String title;
    String name;
    public CharSequence placeId;
    public CharSequence description;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Place_History_model() {

    }

    @Override
    public String toString() {
        return description.toString();
    }
}
