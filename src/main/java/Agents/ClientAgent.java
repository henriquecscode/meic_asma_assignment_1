package Agents;

import Client.Client;
import jade.core.Agent;

public class ClientAgent extends Agent {
    private Client client;

    public ClientAgent(Client client) {
        super();
        this.client = client;
    }



}
