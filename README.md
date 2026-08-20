# gRPC Book Catalog

A Java 25 project built to learn gRPC from both a backend developer and Senior Test Automation Engineer perspective.

## Tech Stack

- Java 25
- Gradle 9
- gRPC Java
- Protocol Buffers
- JUnit 5

## Goals

- Learn Protocol Buffers
- Build a gRPC server
- Build a reusable Java client
- Write automated API tests
- Explore unary and streaming RPCs
- Learn authentication, deadlines and interceptors

## 🚀 Progress

### 🏗️ Project Foundation

- ✅ Project setup
- ✅ Initial Protocol Buffers schema
- ✅ Code generation
- ✅ API design document
- ✅ Pull request review process
- ⬜ Project architecture document
- ⬜ GitHub Actions (CI)

### 📚 Book Catalog Features

- ✅ Unary GetBook RPC
- ✅ Java client
- ✅ Integration tests
- ✅ Repository abstraction
- ✅ In-memory repository
- ✅ gRPC server bootstrap
- ✅ GetBook RPC
- ✅ AddBook RPC
- ⬜ DeleteBook RPC
- ⬜ ListBooks RPC
- ⬜ SearchBooks RPC
- ⬜ Pagination
- ⬜ Validation
- ⬜ API versioning and backward compatibility

### ⚡ Advanced gRPC

- ⬜ Metadata
- ⬜ Deadlines
- ⬜ Server streaming
- ⬜ Client streaming
- ⬜ Bidirectional streaming
- ⬜ Interceptors
- ⬜ Authentication

## ▶️ Running the Project

### Prerequisites

- Java 25
- Git

The project includes the Gradle Wrapper, so a local Gradle installation is not required.

### Clone the repository

```bash
git clone https://github.com/ivanchintov/grpc-book-catalog
cd grpc-book-catalog
```

### Build the project

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

### Run the tests

```bash
./gradlew test
```

### Run the application

The application entry point is:

```
src/main/java/Application.java
```

The gRPC server starts on:

```
localhost:9090
```

The application can be started directly from the IDE by running `Application`.

## 🧪 Testing

The project includes both unit and integration tests.

- **Unit tests** — `BookCatalogService` tested in isolation with Mockito.
- **Integration tests** — the actual gRPC client, server, service, validator, and in-memory repository are exercised together.
- Tests cover successful operations, validation failures, persistence, and duplicate ISBN handling.
