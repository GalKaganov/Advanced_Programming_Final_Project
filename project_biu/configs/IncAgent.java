package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

public class IncAgent implements Agent {
    private final String name;
    private final String[] subs;
    private final String[] pubs;

    public IncAgent(String[] subs, String[] pubs) {
        this.name = "IncAgent";
        this.subs = subs;
        this.pubs = pubs;

        if (subs.length > 0) {
            TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
        }

        if (pubs.length > 0) {
            TopicManagerSingleton.get().getTopic(pubs[0]).addPublisher(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // Nothing to reset
    }

    @Override
    public void callback(String topic, Message msg) {
        if (subs.length > 0 && topic.equals(subs[0])) {
            double value = msg.asDouble;
            if (!Double.isNaN(value) && pubs.length > 0) {
                TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(value + 1));
            }
        }
    }

    @Override
    public void close() {
        if (subs.length > 0) {
            TopicManagerSingleton.get().getTopic(subs[0]).unsubscribe(this);
        }

        if (pubs.length > 0) {
            TopicManagerSingleton.get().getTopic(pubs[0]).removePublisher(this);
        }
    }
}
