package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelAgent implements Agent, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(ParallelAgent.class.getName());
    private static final String DIVIDER = "::";
    private static final int QUEUE_TIMEOUT_SECONDS = 5;

    private final BlockingQueue<Message> messageQueue;
    private final Agent delegateAgent;
    private final Thread processingThread;
    private volatile boolean isRunning;

    public ParallelAgent(Agent delegateAgent, int queueCapacity) {
        if (delegateAgent == null) {
            throw new IllegalArgumentException("Delegate agent cannot be null");
        }
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive");
        }

        this.delegateAgent = delegateAgent;
        this.messageQueue = new ArrayBlockingQueue<>(queueCapacity);
        this.isRunning = true;
        this.processingThread = new Thread(this::processMessages);
        this.processingThread.setName("MessageProcessor-" + delegateAgent.getName());
        this.processingThread.setDaemon(true);
        this.processingThread.start();
    }

    @Override
    public String getName() {
        return delegateAgent.getName();
    }

    @Override
    public void reset() {
        messageQueue.clear();
        delegateAgent.reset();
    }

    @Override
    public void callback(String topic, Message message) {
        try {
            if (topic == null || message == null) {
                LOGGER.warning("Received null topic or message");
                return;
            }

            String normalizedTopic = topic.toUpperCase();
            Message mergedMessage = new Message(normalizedTopic + DIVIDER + message.asText);
            
            if (!messageQueue.offer(mergedMessage, QUEUE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOGGER.warning(() -> String.format("Queue full, dropped message for topic: %s", topic));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Interrupted while queuing message", e);
        }
    }

    @Override
    public void close() {
        isRunning = false;
        processingThread.interrupt();
        try {
            processingThread.join(TimeUnit.SECONDS.toMillis(QUEUE_TIMEOUT_SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Interrupted while closing agent", e);
        }
        delegateAgent.close();
    }

    private void processMessages() {
        while (isRunning) {
            try {
                Message message = messageQueue.poll(QUEUE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (message != null) {
                    String[] parts = message.asText.split(DIVIDER, 2);
                    if (parts.length == 2) {
                        delegateAgent.callback(parts[0], new Message(parts[1]));
                    } else {
                        LOGGER.warning(() -> String.format("Invalid message format: %s", message.asText));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.INFO, "Message processing interrupted", e);
                break;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing message", e);
            }
        }
    }
}
