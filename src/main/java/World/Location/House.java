package World.Location;

public class House extends Location{
    public House(String name){
        super(name, 0);
    }

    public City getCity(){
        return (City) this.getUpperLocations().get(0);
    }
}
