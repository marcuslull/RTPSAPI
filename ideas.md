# Ideas  

Consolidate MVP into a single Cloud Run service with dedicated thread pools

### The Listeners (Twitter Stream Thread Pool):
A dedicated thread pool continuously listens to the Twitter filtered stream.  
Twitter API > Spring WebClient > ApplicationEventPublisher

### The Analysts (Data Processing Thread Pool):
This thread pool subscribes to the Pub/Sub topic where the Listener publishes incoming tweets.
Each thread grabs a tweet, performs sentiment analysis, and updates scores and trends.  
ApplicationEventPublisher > Spring AI/GCP Vertex AI - Gemini > ApplicationEventPublisher

### The Archivists (Data Storage Thread Pool):
This thread pool handles database interactions.
It receives processed data from the Analyst and efficiently stores it in Cloud SQL.  
ApplicationEventPublisher > Spring Data JPA  

### The Concierge (Controller Service):
Uses Spring Web to handle incoming API requests, effortlessly spinning up threads as needed.  
Spring Data JPA > Spring Web > ReST API

# Resources

Filtered stream sample code: https://github.com/xdevplatform/Twitter-API-v2-sample-code/blob/main/Filtered-Stream/FilteredStreamDemo.java  
Filtered stream rule building: https://developer.x.com/en/docs/x-api/tweets/filtered-stream/integrate/build-a-rule

