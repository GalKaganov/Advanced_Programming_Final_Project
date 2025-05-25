package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {

    public static class TopicManager {
        private static final TopicManager instance = new TopicManager();
        private final ConcurrentHashMap<String, Topic> topics;

        private TopicManager() {
            this.topics = new ConcurrentHashMap<>();
        }

        public Topic getTopic(String name) {
            name = name.toUpperCase(); // Normalize to uppercase
            if (topics.containsKey(name)) {
                return topics.get(name);
            } else {
                Topic topic = new Topic(name);
                topics.put(name, topic);
                return topic;
            }
        }

        public boolean containsTopic(String name) {
            name = name.toUpperCase(); // Normalize to uppercase
            return topics.containsKey(name);
        }

        public Collection<Topic> getTopics() {
            return topics.values();
        }

        public void clear() {
            topics.clear();
        }
    }

    public static TopicManager get() {
        return TopicManager.instance;
    }
}
