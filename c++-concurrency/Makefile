CCFLAGS=-Wall -pthread -std=c++17
CC=clang++

.PHONY: build

build: concurrency
	./concurrency

concurrency: main.cpp
	$(CC) $(CCFLAGS) -o concurrency main.cpp
