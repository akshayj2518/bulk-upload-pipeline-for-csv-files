# 🚀 File Processing Pipeline with Producer-Consumer Pattern

This project demonstrates an efficient and scalable way to process large CSV files using the **Producer-Consumer** pattern with multiple consumer threads and synchronization via a **CountDownLatch**.

---

## 📋 Overview

Processing large CSV files directly in a single thread can cause performance bottlenecks and long processing times. This pipeline uses concurrent consumers to speed up processing by distributing work efficiently.

---

## 🔑 Key Components

- 🏭 **Producer**: Reads CSV records line-by-line, validates them, and enqueues them into a bounded `BlockingQueue`.
- 👥 **Consumers**: Multiple consumer threads take records from the queue, process them in batches, and persist them to the database.
- 💀 **Poison Pill**: A special object inserted by the producer after reading all data to signal consumers to stop processing.
- ⏳ **CountDownLatch**: Used to synchronize and ensure the main thread waits until all consumers have finished processing.

---

## ⚙️ How It Works

1. The **Producer** thread reads each record from the CSV file and puts it into a fixed-size `BlockingQueue`.
2. **Consumers** run in separate threads and continuously take records from the queue.
3. Each consumer accumulates records into batches and saves them in bulk to the database.
4. Once the producer finishes, it inserts a *poison pill* equal to the number of consumers.
5. When a consumer sees a poison pill, it stops and signals completion using `CountDownLatch.countDown()`.
6. The main thread blocks on `CountDownLatch.await()` until all consumers are done.

---

## 💡 Benefits of This Approach

- ⚡ **Improved Performance**: Parallel consumers reduce overall processing time.
- 🧠 **Controlled Memory Usage**: The bounded queue prevents memory overload.
- 🧹 **Clean Shutdown**: Consumers terminate gracefully using poison pill.
- 🤝 **Thread Synchronization**: The latch ensures the job completes only after all consumers finish.

---

## 🔧 Configuration Options

- **Queue Capacity**: Max number of records buffered between producer and consumers (e.g., `4`).
- **Number of Consumers**: Level of parallelism (e.g., `2` or `3` threads).
- **Batch Size**: Number of records to save per DB operation (e.g., `4`).

---

## 🧪 Local Development & Testing

### 🔨 Prerequisites

- Java 17
- Maven
- Docker (if running with container)

---

### 🚀 Run Locally (Without Docker)


# Build the JAR
mvn clean package

# Run the JAR
java -jar target/bulkuploadpipeline-0.0.1-SNAPSHOT.jar




https://github.com/user-attachments/assets/df2e7082-e46e-4469-84c9-b27c3151b10c


