# RTPSAPI
Real-Time Political Sentiment API: MVP Requirements

## 1. Introduction

This document outlines the requirements for the Minimum Viable Product (MVP) of a real-time political sentiment API. This API will provide up-to-the-minute sentiment scores and trends for top politicians based on analysis of social media data.

## 2. Target Audience

- Political campaigns
- News organizations
- Public relations firms
- Market research companies
- Academics and researchers  

## 3. Problem Statement

Existing methods for gauging public sentiment towards politicians, such as traditional polls and surveys, are slow, expensive, and limited in scope. There is a need for a real-time, dynamic solution that can provide a more accurate and nuanced understanding of public opinion.

## 4. Proposed Solution

**This MVP will address this problem by providing:**

- Real-time sentiment scores for top politicians based on analysis of social media data.
- Favorability scores that go beyond simple positive/negative ratings, incorporating intensity and contextual analysis.
- Over-time trend analysis to visualize changes in sentiment.
- A simple and intuitive API with clear documentation and easy integration.  

## 5. Technical Requirements

- **Platform:** Google Cloud Platform (GCP)  
- **Services:** Cloud Functions (for social media data collection and pre-processing) Compute Engine (for core API logic and sentiment analysis) Cloud SQL (for data storage) API Gateway (for API management and serving)  
- **Programming Language:** Java (for core API) and potentially Python (for Cloud Functions) or Javascript (data connection scripting)
- **Data Source:** Various social media APIs  
- **Sentiment Analysis:** Utilize a combination of existing libraries and potentially custom-trained models.  

## 6. Monetization Strategy

- Launch on API marketplaces like RapidAPI or AWS Marketplace.
- Offer a freemium model with a limited free tier and tiered pricing plans for higher usage.
- Explore potential partnerships with political data providers or research institutions.  

## 7. Future Possibilities

- Expand to other social media platforms (e.g., Facebook, YouTube, Twitter, Reddit, ...).
- Incorporate news articles, blogs, and other online sources.
- Develop more sophisticated sentiment analysis algorithms.
- Offer advanced features like demographic breakdowns, influencer analysis, and predictive modeling.
- Create a user interface with data visualizations and reporting tools.  

## 8. Success Metrics

- Number of API calls
- User growth and retention
- Customer satisfaction
- Revenue generation

This MVP will serve as a strong foundation for a comprehensive political sentiment analysis platform, providing valuable insights to a wide range of users and establishing a foothold in a rapidly growing market.

## 9. Architecture  
Four services logically seperated by responsibility. Inter-service communication is provided by Redis Streams or Spring's ApplicationEventPublisher which are non-blocking event based services.

- ### The Listeners (Social Media Stream Thread Pool):
    A dedicated thread pool continuously listens to the social media stream and publish bundles to the Redis Stream or ApplicationEventPublisher.  
  Social media API > Intermediate connector > ApplicationEventPublisher/Redis

- ### The Analysts (Data Processing Thread Pool):
    This thread pool subscribes to the ApplicationEventPublisher topic or Redis group where the Listeners publish incoming posts.
Each thread grabs a bundle of posts, performs sentiment analysis, and updates scores and trends.  
ApplicationEventPublisher/Redis > Spring AI/GCP Vertex AI - Gemini > ApplicationEventPublisher/Redis

- ### The Archivists (Data Storage Thread Pool):
    This thread pool handles database interactions.
It receives processed data from the Analyst and efficiently stores it in Cloud SQL.  
ApplicationEventPublisher/Redis > Spring Data JPA

- ### The Concierge (Controller Service):
    Uses Spring Web to handle incoming API requests, effortlessly spinning up threads as needed.  
Spring Data JPA > Spring Web > ReST API
