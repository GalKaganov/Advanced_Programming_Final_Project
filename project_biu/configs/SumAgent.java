package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

import java.util.HashMap;
import java.util.Map;

public class SumAgent implements Agent {
    private final String name;
    private final String[] subs;
    private final String[] pubs;
    private final Map<String, Double> inputs;

    public SumAgent(String[] subs, String[] pubs) {
        this.name = "SumAgent";
        this.subs = subs;
        this.pubs = pubs;
        this.inputs = new HashMap<>();

        for (String sub : subs) {
            TopicManagerSingleton.get().getTopic(sub).subscribe(this);
        }

        for (String pub : pubs) {
            TopicManagerSingleton.get().getTopic(pub).addPublisher(this);
        }

        reset();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        for (String sub : subs) {
            inputs.put(sub, 0.0);
        }
    }

    @Override
    public void callback(String topic, Message msg) {
        if (Double.isNaN(msg.asDouble)) {
            return;
        }

        inputs.put(topic, msg.asDouble);

        if (allInputsReceived()) {
            double sum = inputs.values().stream().mapToDouble(Double::doubleValue).sum();
            if (pubs.length > 0) {
                TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(sum));
            }
            reset();
        }
    }

    private boolean allInputsReceived() {
        return inputs.values().stream().allMatch(value -> value != 0.0);
    }

    @Override
    public void close() {
        for (String sub : subs) {
            TopicManagerSingleton.get().getTopic(sub).unsubscribe(this);
        }

        for (String pub : pubs) {
            TopicManagerSingleton.get().getTopic(pub).removePublisher(this);
        }
    }
}
