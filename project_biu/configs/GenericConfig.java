package configs;

import graph.Agent;
import graph.ParallelAgent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String confFile;
    private List<ParallelAgent> parallelAgents;

    public GenericConfig() {
        this.parallelAgents = new ArrayList<>();
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    @Override
    public void create() {
        try {
            List<String> lines = readLinesFromFile(confFile);

            if (lines == null || lines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration file");
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                try {
                    Class<?> agentClass = Class.forName(className);
                    Constructor<?> constructor = agentClass.getDeclaredConstructor(String[].class, String[].class);
                    Agent agent = (Agent) constructor.newInstance(subs, pubs);
                    ParallelAgent parallelAgent = new ParallelAgent(agent, 10);
                    parallelAgents.add(parallelAgent);
                } catch (ClassNotFoundException | NoSuchMethodException |
                         InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + confFile);
            e.printStackTrace();
        }
    }

    private List<String> readLinesFromFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (ParallelAgent parallelAgent : new ArrayList<>(parallelAgents)) {
            parallelAgent.close();
        }
        parallelAgents.clear();
    }
}
