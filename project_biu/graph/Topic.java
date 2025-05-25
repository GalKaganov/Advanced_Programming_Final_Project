package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Optional;

public class Topic {
    private final String name;
    private final List<Agent> subscribers;
    private final List<Agent> publishers;
    private volatile Message lastMessage;

    public Topic(String name) {
        this.name = name;
        this.subscribers = new CopyOnWriteArrayList<>();
        this.publishers = new CopyOnWriteArrayList<>();
        this.lastMessage = new Message("0.0");
    }

    public String getName() {
        return name;
    }

    public void subscribe(Agent agent) {
        if (agent != null) {
            subscribers.add(agent);
        }
    }

    public void unsubscribe(Agent agent) {
        if (agent != null) {
            subscribers.remove(agent);
        }
    }

    public void publish(Message message) {
        if (message != null) {
            subscribers.forEach(agent -> agent.callback(name, message));
            lastMessage = message;
        }
    }

    public Optional<Message> getLastMessage() {
        return Optional.ofNullable(lastMessage);
    }

    public void addPublisher(Agent agent) {
        if (agent != null) {
            publishers.add(agent);
        }
    }

    public void removePublisher(Agent agent) {
        if (agent != null) {
            publishers.remove(agent);
        }
    }

    public List<Agent> getSubscribers() {
        return Collections.unmodifiableList(subscribers);
    }

    public List<Agent> getPublishers() {
        return Collections.unmodifiableList(publishers);
    }

    @Override
    public String toString() {
        return String.format("Topic{name='%s', subscribers=%d, publishers=%d}", 
            name, subscribers.size(), publishers.size());
    }
}
