# Dungeon

## Build

### Build

```bash
./gradlew build
```

### Generate Map
Before running the server a map must be generated

```bash
./gradlew :map-generator:run
```

### Run

```bash  
./gradlew :server:run
```

Then connect with chrome at http://localhost:8080

### Example Map
Three way example of a small generated map. Top left is a full print. Bottom center is a generated bitmap of the map. Right is server rendering of player perspective.

![image](https://user-images.githubusercontent.com/1614281/201038852-c42c4dcf-d1ca-4b36-8b5b-e3b0572061b1.png) 
