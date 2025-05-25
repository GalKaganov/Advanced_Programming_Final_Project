package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

public class DivAgent implements Agent {
    private final String name;
    private final String[] subs;
    private final String[] pubs;
    private volatile double x;
    private volatile double y;
    private boolean gotX = false;
    private boolean gotY = false;

    public DivAgent(String[] subs, String[] pubs) {
        this.name = "DivAgent";
        this.subs = subs;
        this.pubs = pubs;
        this.x = 0.0;
        this.y = 0.0;

        if (subs.length >= 2) {
            TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
            TopicManagerSingleton.get().getTopic(subs[1]).subscribe(this);
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
        x = 0.0;
        y = 0.0;
        gotX = false;
        gotY = false;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (Double.isNaN(msg.asDouble)) {
            return;
        }

        if (topic.equals(subs[0])) {
            x = msg.asDouble;
            gotX = true;
        } else if (topic.equals(subs[1])) {
            y = msg.asDouble;
            gotY = true;
        }

        if (gotX && gotY && pubs.length > 0) {
            if (y != 0.0) {
                TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(x / y));
            } else {
                TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(Double.NaN));
            }
            reset();
        }
    }

    @Override
    public void close() {
        for (String s : subs) {
            TopicManagerSingleton.get().getTopic(s).unsubscribe(this);
        }

        for (String s : pubs) {
            TopicManagerSingleton.get().getTopic(s).removePublisher(this);
        }
    }
}
