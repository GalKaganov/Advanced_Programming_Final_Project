package graph;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {

    private final String name;
    private final String firstTopic;
    private final String secondTopic;
    private final String resultTopic;
    private final BinaryOperator<Double> operator;
    private Double firstMessage;
    private Double secondMessage;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public BinOpAgent(String name, String firstTopic, String secondTopic, String resultTopic, BinaryOperator<Double> operator) {
        this.name = name;
        this.firstTopic = firstTopic.toUpperCase(); // Normalize to uppercase for consistent topic matching
        this.secondTopic = secondTopic.toUpperCase();
        this.resultTopic = resultTopic.toUpperCase();
        this.operator = operator;
        reset();
        topicManager.getTopic(this.firstTopic).subscribe(this);
        topicManager.getTopic(this.secondTopic).subscribe(this);
        topicManager.getTopic(this.resultTopic).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        firstMessage = 0.0;
        secondMessage = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        String normalizedTopic = topic.toUpperCase();

        if (Double.isNaN(msg.asDouble)) return; // Ignore invalid values

        if (normalizedTopic.equals(firstTopic)) {
            firstMessage = msg.asDouble;
        } else if (normalizedTopic.equals(secondTopic)) {
            secondMessage = msg.asDouble;
        }

        // Only publish when both messages are received and non-zero
        if (firstMessage != 0.0 && secondMessage != 0.0) {
            topicManager.getTopic(resultTopic).publish(new Message(operator.apply(firstMessage, secondMessage)));
            reset();
        }
    }

    @Override
    public void close() {
        topicManager.getTopic(firstTopic).unsubscribe(this);
        topicManager.getTopic(secondTopic).unsubscribe(this);
        topicManager.getTopic(resultTopic).removePublisher(this);
    }
}
