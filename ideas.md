# Ideas  

Consolidate MVP into a single Cloud Run service with dedicated thread pools

### The Concierge (Main Service):
Uses Spring Web to handle incoming API requests, effortlessly spinning up threads as needed.  

### The Listeners (Twitter Stream Thread Pool):
A dedicated thread pool continuously listens to the Twitter filtered stream.

### The Analysts (Data Processing Thread Pool):
This thread pool subscribes to the Pub/Sub topic where the Listener publishes incoming tweets.
Each thread grabs a tweet, performs sentiment analysis, and updates scores and trends.

### The Archivists (Data Storage Thread Pool):
This thread pool handles database interactions.
It receives processed data from the Analyst and efficiently stores it in Cloud SQL.
