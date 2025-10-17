# Lego Auction Backend Service

## Description

This project implements a backend service for a Lego auction application. The backend supports the creation and management of **Lego Sets**, **Auctions**, **Users**, and **Comments**. It uses Azure services to deploy, manage, and scale the backend efficiently.

The backend supports the following mandatory features:
1. **Application-level caching** using **Azure Redis Cache** for performance improvement.
2. **Azure Functions** for periodic tasks, such as closing expired auctions and performing sentiment analysis to determine if Lego sets are liked or not based on user comments.

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 21**
- **Maven**
- **Azure CLI**
- **Redis** 

## Features

### Mandatory Features

#### 1. Application-Level Caching (Azure Redis Cache)
- To improve the system's performance, Redis Cache is used to store frequently accessed data and avoid unnecessary database hits.
- The system caches results of common queries, such as **user Lego Sets** and **auction details**.
- Redis is configured through the `.env` file, where you can specify your **Redis connection details**.

#### 2. Azure Functions
- Azure Functions are used for handling periodic operations such as:
  - Closing expired **Auctions** (every 5 minutes).
  - Computing the sentiment of user **comments** and determining if a Lego set is liked based on these comments.
  - The sentiment analysis is performed using **Azure Cognitive Services** and the results are cached in Redis for fast retrieval.



1. **Clone the Repository:**
   ```bash
   git clone https://github.com/martinWff/CloudComputing_LegoProject.git


## Contributors
Name                             Email                                                                          Number
Coumba Louise Mbodji Sow     c.sow@campus.fct.unl.pt                                                              75921
João Filipe Da Silva Ribeiro    jfs.ribeiro@campus.fct.unl.pt                                                       73706
Daniel José Nascimento de Castro Martin djn.martins@campus.fct.unl.pt                                                73951
