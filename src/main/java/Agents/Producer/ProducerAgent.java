package Agents.Producer;

import Producer.Producer;
import jade.core.Agent;

public class ProducerAgent extends Agent {
    private Producer producer;

    public ProducerAgent(Producer producer) {
        super();
        this.producer = producer;
    }
}
