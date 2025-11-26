# Dragons of Mugloar

This project is a client for the Dragons of Mugloar game. It provides a simple way to play the game, automate missions, and purchase items from the shop.

## How to Run with Docker

### Prerequisites

- Docker
- Docker Compose

### Running the Application

1.  **Build and run the container:**

    ```bash
    docker-compose up --build
    ```

    This command will build the Docker image and start the application. The application will be available at `http://localhost:8080`.

2.  **Play the game:**

    Once the application is running, you can play the game by sending a POST request to the `/api/game/play` endpoint.

    ```bash
    curl -X POST http://localhost:8080/api/game/play
    ```

    This will start a new game and automatically play it until the game is over. The response will contain the final score and other game statistics.

## The 1000+ Point Strategy

The strategy for scoring over 1000 points is based on a few key principles:

1.  **Mission Selection:**

    The client prioritizes missions with a high probability of success. It prefers "Sure Thing" missions, as these are guaranteed to succeed. Other missions are evaluated based on their risk and potential reward.

2.  **Purchasing Healing Potions:**

    When the player's lives are running low, the client will automatically purchase healing potions from the shop. This allows the player to continue playing for longer and complete more missions.

3.  **Avoiding Risky Missions:**

    The client is configured to avoid missions that are too risky. This is especially important in the early stages of the game when the player has limited resources.

By following this strategy, the client is able to consistently score over 1000 points. The final score will vary depending on the missions that are available and the player's luck, but the strategy is designed to maximize the chances of success.
