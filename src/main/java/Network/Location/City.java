package Network.Location;

import Network.Graph.Node;

import java.util.List;

public class City extends Location{

    public City(String name){
        super(name, 1);
    }

    public List<House> getHouses(){
        List<? extends Node> houses = this.getLowerLocations();
        return (List<House>) houses;
    }

    public Port getPort(){
        return (Port) this.getUpperLocations().get(0);
    }
}
