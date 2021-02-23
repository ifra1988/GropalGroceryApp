package Model;

public class PlaceAutocomplete {

    public CharSequence placeId;
    public CharSequence placeName;

    public PlaceAutocomplete(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.placeName = description;
    }

    public String getName()
    {
        return placeName.toString();
    }

    @Override
    public String toString() {
        return placeName.toString();
    }
}
