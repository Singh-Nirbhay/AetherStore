# AetherStore

> **An experimental in-memory concurrent key-value database built from scratch in pure Java. Inspired by Redis, extended with bounded Multi-Version Concurrency Control (MVCC) for historical state retrieval.**



---

## Why AetherStore?

Modern frameworks make it easy to build applications, but they also hide many of the mechanisms that make databases fast, reliable, and concurrent.

I built **AetherStore** to understand those mechanisms from the ground up.

Instead of relying on networking libraries or storage frameworks, every layer—from accepting TCP connections to parsing network packets and managing concurrent memory—was implemented using only the Java Standard Library.

The goal wasn't simply to recreate Redis commands. It was to explore the engineering decisions behind an in-memory database and experiment with an idea that Redis intentionally doesn't optimize for:

> **What if an in-memory database could remember its past?**

That question eventually led to the implementation of a bounded **Multi-Version Concurrency Control (MVCC)** engine capable of serving historical queries while keeping memory usage predictable.

---

# Preview

<p align="center">
<img  height="500" alt="image" src="https://github.com/user-attachments/assets/c9e76622-e1fb-43a2-b3e1-c46485c6031e" />

</p>



---

# Architecture Overview

<p align="center">
<img  height="900" alt="image" src="https://github.com/user-attachments/assets/d45d60a6-13bf-4210-81cb-3cd8c47978fc" />
</p>





The request lifecycle follows a straightforward pipeline:

```
Client
   │
   ▼
TCP ServerSocket
   │
   ▼
Fixed Thread Pool
   │
   ▼
RESP Parser
   │
   ▼
Command Dispatcher
   │
   ▼
Concurrent Storage Engine
   │
   ├────────► TTL Manager
   │
   ▼
Temporal Ring Buffers
   │
   ▼
RESP Response
```

Every layer has a single responsibility, making the system easier to reason about and extend.

---

# What Makes AetherStore Different?

Most in-memory databases optimize for retrieving the **latest** value.

AetherStore also remembers **previous** values.

Instead of replacing data permanently, every update creates a new version inside a bounded history buffer. This makes it possible to ask questions like:

```text
GETAT user 1718383271000
```

and retrieve the value that existed at that exact moment.

The history is intentionally capped, ensuring that memory consumption remains constant regardless of how many updates a key receives.

---

# Core Engineering Concepts

## Network Layer

AetherStore communicates directly over raw TCP using Java's `ServerSocket`.

Incoming client connections are delegated to a fixed-size worker thread pool, preventing unbounded thread creation while allowing multiple clients to interact with the database simultaneously.

---

## Protocol Parsing

TCP is a continuous stream of bytes—it has no concept of individual messages.

To reconstruct requests correctly, AetherStore implements its own parser for the **Redis Serialization Protocol (RESP)**.

The parser:

- Processes requests at CRLF (`\r\n`) boundaries
- Tracks payload sizes using UTF-8 byte lengths
- Correctly handles multi-byte Unicode characters
- Prevents malformed packet corruption

---

## Concurrent Storage

The storage engine is backed by `ConcurrentHashMap`.

Rather than relying on a single global lock, it benefits from **lock striping**, allowing unrelated keys to be modified simultaneously while reducing contention under concurrent workloads.

This keeps reads and writes responsive even with many active client connections.

---

## Memory Management

Keeping memory predictable is just as important as storing data.

AetherStore combines two complementary eviction strategies.

### Passive Eviction

Expired keys are removed lazily whenever they're accessed.

### Active Eviction

A background daemon continuously scans for expired keys and removes them proactively.

This hybrid approach minimizes unnecessary work while preventing stale data from accumulating in memory.

---

## Temporal Versioning (MVCC)

<p align="center">


</p>



Every key maintains a bounded history inside a synchronized temporal buffer.

Each mutation creates a new version while older versions shift down the buffer.

The history remains fixed in size, providing:

- Historical state retrieval
- Predictable memory usage
- Thread-safe updates
- Constant memory overhead per key

---

# Request Flow

<p align="center">

</p>



A typical request follows this path:

```text
Client

↓

TCP Connection

↓

RESP Parser

↓

Command Dispatcher

↓

Storage Engine

↓

TTL Validation

↓

MVCC Buffer

↓

Response
```

---

# Supported Commands

| Command | Description |
|----------|-------------|
| `PING` | Health check |
| `ECHO <value>` | Returns the provided value |
| `SET <key> <value>` | Stores a value |
| `GET <key>` | Retrieves the latest value |
| `DEL <key>` | Removes a key and its history |
| `EXPIRE <key> <seconds>` | Adds a TTL |
| `TIMELINE <key>` | Displays all stored versions |
| `GETAT <key> <timestamp>` | Retrieves a historical value |

---

# Project Structure

```text
AetherStore
│
├── bin/                                  # Compiled .class files
│
├── src/
│   └── com/
│       └── nirbhay/
│           └── aetherstore/
│               │
│               ├── Server.java           # Entry point, ServerSocket, Fixed Thread Pool
│               ├── ClientHandler.java    # Runnable thread, Network I/O, RESP Parsing
│               │
│               ├── command/
│               │   └── CommandEngine.java      # Command routing, RESP array formatting
│               │
│               └── storage/
│                   ├── StorageEngine.java      # Singleton vault, ConcurrentHashMap, TTL Janitor
│                   ├── TemporalRingBuffer.java # MVCC bounded LinkedList (max size 10)
│                   └── TemporalNode.java       # Immutable historical state and epoch timestamp
│
├── docs/                                 # (Optional) For your architecture diagrams
│   └── architecture.png
│
└── README.md                             # Project documentation
```

---

# Design Decisions

Some decisions were intentionally made to keep the project focused.

- Fixed thread pool instead of spawning unlimited threads
- Raw TCP instead of HTTP abstractions
- RESP implementation instead of custom protocols
- Lock-striping rather than global synchronization
- Bounded MVCC history instead of unlimited version storage
- Passive + active TTL eviction for predictable memory usage

These choices prioritize understanding core systems concepts over adding features.

---

# What I Learned

Building AetherStore reinforced something that books often can't teach:

Most complexity in systems programming doesn't come from algorithms—it comes from correctly managing state under concurrency.

Writing the networking layer, protocol parser, storage engine, and eviction mechanisms from scratch made concepts like race conditions, synchronization, protocol framing, and memory management much more tangible than simply using existing libraries.

---

# Future Improvements

There are several directions I'd like to explore next.

- Append Only File (AOF) persistence
- Snapshotting
- LRU/LFU eviction policies
- Transaction support
- Pub/Sub messaging
- Replica synchronization
- Cluster mode
- Non-blocking Java NIO implementation
- Benchmark suite
- Bloom Filters

---



## Connect

If you're interested in backend engineering, distributed systems, concurrency, or database internals, I'd love to hear your thoughts or suggestions.

⭐ If you found the project interesting, consider giving it a star.
