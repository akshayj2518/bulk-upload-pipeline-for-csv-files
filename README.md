# 🚀 File Processing Pipeline with Producer-Consumer Pattern

This project demonstrates an efficient and scalable way to process large CSV files by using the **Producer-Consumer pattern** with multiple consumer threads and synchronization via a **CountDownLatch**.

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

1. The **Producer** thread reads each record from the CSV file and puts it into a fixed-size queue (`BlockingQueue`).
2. **Consumers** run in separate threads and continuously take records from the queue.
3. Each consumer accumulates records into batches and saves them in bulk to the database, improving throughput.
4. Once the producer finishes reading all records, it inserts a "poison pill" equal to the number of consumers into the queue.
5. When a consumer encounters the poison pill, it stops consuming and signals completion via `CountDownLatch.countDown()`.
6. The main processing thread waits on the `CountDownLatch.await()` until all consumers finish before marking the job as complete or failed.

---

## 💡 Benefits of This Approach

- ⚡ **Improved Performance**: Parallel consumers reduce overall processing time, especially with slow downstream operations like database writes.
- 🧠 **Controlled Memory Usage**: The bounded queue limits memory use by controlling the number of unprocessed items.
- 🧹 **Clean Shutdown**: The poison pill mechanism ensures consumers terminate gracefully once all data is processed.
- 🤝 **Thread Synchronization**: The latch ensures the main thread only completes after all consumers finish, allowing for proper status reporting.

---

## 🔧 Configuration

- 📦 **Queue Capacity**: Controls how many records can be buffered between producer and consumers (e.g., 4).
- 👥 **Number of Consumers**: Controls concurrency level for processing (e.g., 2 or 3 threads).
- 📊 **Batch Size**: Number of records saved at once to the database for efficiency (e.g., 4).

---

## 🚀 How to Use

1. Place your CSV file path in the job configuration.
2. Run the service to start processing asynchronously.
3. Monitor job status for completion or failure.
4. Adjust queue size, consumer count, and batch size as needed based on your environment and performance requirements.

---

## 📝 Summary of Processing Flow

This pipeline uses a **Producer-Consumer pattern** with multiple consumer threads and a **CountDownLatch** to efficiently process large CSV files:

- The **Producer** reads the file line-by-line, converts each record into an object, and puts it into a bounded **`BlockingQueue`**.
- Multiple **Consumer threads** concurrently take objects from the queue, process them in batches, and save them to the database.
- When the Producer finishes reading all records, it inserts a special **“poison pill”** into the queue for each consumer to signal no more data.
- Upon consuming the poison pill, each Consumer stops processing and calls `countDown()` on the **`CountDownLatch`**.
- The main thread waits on the latch with `await()`, ensuring it only proceeds once all consumers have finished.
- This design balances workload, avoids memory overload, and guarantees proper job completion status after all processing is done.

---

## ⚠️ Notes

- Adjust the number of consumer threads according to your system’s CPU cores and database capacity.
- The batch size can impact memory and database performance; tune according to your workload.
- Handle exceptions carefully within consumers to avoid silent failures.
- This pattern can be adapted for any large file processing tasks requiring asynchronous, batch-oriented persistence.

https://github.com/user-attachments/assets/9d5c95b0-7a47-4cf0-ae78-53d144ac8c41



