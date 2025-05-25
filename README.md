# Java HTTP Server – Agent & Topic Graph Simulation

## 1. Overview

This project, developed by Gal Kaganovsky & Dana Bakshe for an advanced programming course, implements a custom Java HTTP server. This server simulates a distributed agent-based system where agents communicate and process data via a topic-based publish-subscribe mechanism.

Users can:
- Define the system's architecture by uploading a configuration file that specifies agents and their topic subscriptions/publications.
- Visualize the agent-topic relationships as an interactive graph.
- Send messages into the system and observe how agents react and how topic values change in real-time.
- Monitor the live state of all topics.

This system is a practical demonstration of concurrent programming, server-client interaction, and dynamic web-based visualization of a messaging system.

## 2. Features

-   **Dynamic Configuration:** Upload text-based configuration files to define agents, their types (e.g., sum, plus, inc), and the topics they interact with.
-   **Interactive Graph Visualization (`graph.html`):**
    -   Agents are displayed as circles.
    -   Topics are displayed as rectangles.
    -   Connections (edges) show publish/subscribe relationships.
    -   Input values to agents are displayed on the connecting arrows, updating dynamically.
-   **Message Passing:** Users can send numerical messages to specified topics via a web form (`form.html`).
-   **Agent Processing:** Agents subscribed to topics receive messages, perform predefined operations, and publish results to other topics.
-   **Live Topic Monitoring (`table.html`):** A real-time table displays the current value (last message) for each active topic.
-   **Custom HTTP Server:** Built from scratch, handling GET and POST requests to serve HTML content and manage system interactions.

## 3. How to Run

### Prerequisites

-   Java SE Development Kit (JDK) 8 or higher.
-   A modern web browser (e.g., Chrome, Firefox, Edge).
-   An Integrated Development Environment (IDE) like IntelliJ IDEA or Eclipse (recommended for ease of use).

### Setup & Execution

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/GalKaganov/project_biu.git Advanced_Programming_Final_Project
    ```
    This command will create a directory named `Advanced_Programming_Final_Project` containing the repository content.
    Navigate into this newly created directory:
    ```bash
    cd Advanced_Programming_Final_Project
    ```
    All subsequent instructions assume your command line is operating from this `Advanced_Programming_Final_Project` directory (the root of the cloned repository).

2.  **Open in IDE:**
    -   **IntelliJ IDEA:**
        1.  Open the `Advanced_Programming_Final_Project` directory (the one you just `cd`-ed into) as a new project in IntelliJ IDEA.
        2.  Wait for the IDE to index the files.
        3.  The main class to run is located at `project_biu/Main.java` (relative to the project root `Advanced_Programming_Final_Project`). Locate this file and run its `main` method.
    -   **Eclipse:**
        1.  Select `File` -> `Open Projects from File System...`.
        2.  Choose the `Advanced_Programming_Final_Project` directory as the directory to import.
        3.  Ensure the `project_biu` subfolder (located within `Advanced_Programming_Final_Project`, containing the `src`-like structure, e.g., where `Main.java` resides) is correctly identified or configured as a source folder if necessary.
        4.  Locate and run the `main` method in `project_biu.Main.java`.

3.  **Access the Web Interface:**
    Once the server is running (you should see output in the IDE's console indicating the server has started), open your web browser and navigate to:
    [http://localhost:8080/app/index.html](http://localhost:8080/app/index.html)

    This is the main entry point for interacting with the system.

## 4. Usage Guide

1.  **Start Page (`index.html`):**
    -   This page provides an interface to upload your agent configuration file.
    -   Click "Choose File", select your configuration `.txt` file, and then click "Load Configuration".

2.  **Configuration File Format:**
    The configuration file defines the agents in the system. Each agent definition consists of three lines:
    -   Line 1: The fully qualified class name of the agent (e.g., `configs.PlusAgent`, `configs.IncAgent`).
    -   Line 2: A comma-separated list of topic names the agent subscribes to.
    -   Line 3: A comma-separated list of topic names the agent publishes to.

    **Example (`conf.txt`):**
    ```
    configs.PlusAgent
    T1,T2
    T3
    configs.IncAgent
    T3
    T4
    ```
    *In this example, `PlusAgent` subscribes to `T1` and `T2`, and publishes its sum to `T3`. `IncAgent` subscribes to `T3` and publishes an incremented value to `T4`.*
    *Example configuration files can be found in the `example_config_files` directory.*

3.  **Interacting with the System:**
    After loading a configuration, the main page will display three frames:
    -   **Graph View (`graph.html`):** Shows the live visualization of topics and agents. Values on arrows indicate the last message passed from a topic to a subscribing agent.
    -   **Topic Input Form (`form.html`):** Allows you to select a topic and send a numerical message to it. Click "Send" to publish the message.
    -   **Topics Table (`table.html`):** Displays all active topics and their current (last published) numerical values.

    Both the graph and the table update dynamically to reflect changes in the system.

## 5. Project Structure

The project is organized as follows within the `Advanced_Programming_Final_Project/` root directory:

-   `project_biu/`: Contains the core Java source code for the server and agent simulation.
    -   `configs/`: Contains Java classes for agent implementations (e.g., `PlusAgent.java`, `SumAgent.java`) and specific configuration parsing logic directly related to agent behavior (e.g., `GenericConfig.java`, `Graph.java`).
    -   `graph/`: Holds core Java classes for managing topics (`Topic.java`), messages (`Message.java`), and agent interfaces/abstractions (`Agent.java`, `TopicManagerSingleton.java`).
    -   `server/`: Contains the Java implementation of the custom HTTP server (`MyHTTPServer.java`, `RequestParser.java`).
    -   `servlets/`: Includes Java servlets that handle specific HTTP requests, such as configuration loading (`ConfLoader.java`), topic data display (`TopicDisplayer.java`), and graph visualization (`GraphDisplayer.java`).
    -   `views/`: Stores Java classes responsible for dynamically generating HTML content for the web interface (e.g., `HtmlGraphWriter.java`, `HtmlTableWriter.java`).
    -   `Main.java`: The main Java entry point to start the HTTP server application.
-   `html_files/`: Contains all static front-end files, including HTML (`index.html`, `graph.html`, `form.html`, `table.html`), CSS, and client-side JavaScript for the user interface.
-   `config_files/`: This directory is intended for storing user-provided configuration files (e.g., `.txt` files defining agents and topics) that the application will load and use.
-   `example_config_files/`: Provides example agent configuration files (e.g., `conf.txt`) to help users understand the expected format.
-   `README.md`: This documentation file.
-   `Project_Presentation.mp4`: Video showcasing the project presentation.
-   `System_Demonstration.mp4`: Video demonstrating the system's functionality.
-   `Final_Project_Presentation.pptx`: Presentation slides for the project.

## 6. Presentation Materials

This project includes two video demonstrations and a presentation:

-   **Project Presentation Video:** [Project_Presentation.mp4](./Project_Presentation.mp4)
-   **System Demonstration Video:** [System_Demonstration.mp4](./System_Demonstration.mp4)
-   **Presentation Slides:** [Final_Project_Presentation.pptx](./Final_Project_Presentation.pptx)

