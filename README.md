Demo Codes in Network Programming Using C/Java/Python
===================================================================

## Introduction
This repository contains demo codes in network programming using C/Java/Python. For each language, two typical ways in handling multiple clients are implemented, which are multithreading and selector, respectively.

## Table of Contents
* Implementation using C
* Implementation using Java
* Implementation using Python

## Implementation using C
* Files: client.c, serverthread.c (using multithreading) and serverselect.c (using selector)
* OS: Linux
* Compilation: gcc client.c -o client, gcc -pthread serverthread.c -o serverthread, gcc serverselect.c -o serverselect
* Run: open a server (./serverthread or ./serverselect), and then open one or more clients (./client) in separate terminals; type "q" or "Q" in client programs to quit

## Implementation using Java
* Files: Client.java, ServerThread.java (using multithreading) and ServerSelect.java (using NIO selector)
* OS: Cross-platform
* Compilation: javac Client.java ServerThread.java ServerSelect.java
* Run: open a server (java ServerThread or java ServerSelect), and then open one or more clients (java Client) in separate terminals; type "q" or "Q" in client programs to quit

## Implementation using Java
* Files: client.py, serverthread.py (using multithreading) and serverselect.py (using selector)
* OS: Cross-platform
* Run: open a server (python serverthread.py or python serverselect.py), and then open one or more clients (python client.py) in separate terminals; type "q" or "Q" in client programs to quit