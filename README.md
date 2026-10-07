# OOP Example: Demonstration Through Refactoring

## Day 0

There is one main method containing all the logic.

This creates nice locality.

It is also entirely unreadable.

## Day 1

The lookup table is now a dependency with proper names.

## Day 2

This is a big one, now the server is its own thing, and the main method looks much easier to understand.

## Day 3

The server no longer knows about countries.
Client handling is now delegated to a dedicated handler that takes the lookup as a dependency.
