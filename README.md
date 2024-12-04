# Finance Tracker

## Version 1.0.0 by Skye Goetz

This is a non-serious project I built to familiarize myself with Clojure and use a SQLite database to track my finances which I've been meaning to do for a while. I expect to maintain this occasionally and as needed. This is only meant to give me some quick spending analytics based on what I add to the database.

### Usage (Unix)

```bash
lein run <command> [parameters]
```

## Finance Tracker Commands

### Analysis

```bash
lein run analysis
```

### Income

```bash
lein run income <date> <ammount> <what_for> <category>
```

### Expense

```bash
lein run expense <date> <ammount> <what_for> <category>
```

### Theorhetical

```bash
lein run theorhetical <command> <data> <ammount> <what_for> <category>
```
