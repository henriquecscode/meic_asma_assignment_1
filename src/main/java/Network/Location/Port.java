package Network.Location;

import Network.Graph.Node;

import java.util.List;

public class Port extends Location{
    public Port(String name){
        super(name, 2);
    }

    public List<City> getCities(){
        List<? extends Node> cities = this.getLowerLocations();
        return (List<City>) cities;
    }

    public List<Port> getPorts(){
        List<? extends Node> ports = this.getUpperLocations();
        return (List<Port>) ports;
    }
}
