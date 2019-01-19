# react-ssr-sample-java-nashorn

[![CircleCI](https://circleci.com/gh/daves125125/react-ssr-sample-java-nashorn.svg?style=svg&circle-token=74232108964cd9f2ad9b4f00cf530880f65bc483)](https://circleci.com/gh/daves125125/react-ssr-sample-java-nashorn)

This project demonstrates rendering Javascript code on the server side using Java. 


## TL;DR

The client side code consists of a small React app that uses some popular libraries such as react-router, bootstrap etc. It 
features a page that has dynamic data with state inserted from the server side which can then also be later updated on the client side. 

The server side code consists of a simple Spring Boot application that renders JS using Nashorn. The server side app fetches and caches
some basic postcode data available from a third party, open API - https://api.postcodes.io.

See the Run section on how to start the app locally.

This sample is heavily inspired by:

- [https://patrickgrimard.io/2016/11/24/server-side-rendering-with-spring-boot-and-react](https://patrickgrimard.io/2016/11/24/server-side-rendering-with-spring-boot-and-react)
- [https://github.com/pgrimard/spring-boot-react](https://github.com/pgrimard/spring-boot-react)

- [https://github.com/sdeleuze/spring-react-isomorphic](https://github.com/sdeleuze/spring-react-isomorphic)
- [https://www.youtube.com/watch?v=cPxq66nXsf4](https://www.youtube.com/watch?v=cPxq66nXsf4)


## Run

This sample has been packaged as a docker container and can be ran by executing: 

```
docker run -p8080:8080 daves125125/react-ssr-sample-java-nashorn
```

Navigate to `localhost:8080/` to see the sample running.

NOTE: Initial load times of pages will be extremely slow - see the [Initial server side rendering speed](#initial-server-side-rendering-speed) section below.


## Build / Run from source

```
yarn install && yarn build && ./gradlew clean bootRun
```

Or, via Docker:

```
yarn install && yarn build
./gradlew clean build && docker build -t test .
docker run -p8080:8080 test
```


## How this works / Areas of interest

The JS code is split into two main bundles, the client.js and server.js. These are built as independent source sets 
by Webpack. Both the server.js and client.js depend upon the the main React App itself with the only difference being 
that the client side component includes client side specific code such as browser routing, and the server side code includes
server side routing and injection of initial state.

The Server side uses Nashorn to render only the server.js bundle which gets packaged as part of the build process. Some 
Spring framework code largely abstracts the handling of the MVC layer as well as the configuration of the Nashorn templating 
engine.

Regarding SSR, the main files of interest are:

- react-src/client.js
- react-src/server.js
- src/main/java/com/djh/SSRController.java
- src/main/java/com/djh/SSRConfiguration.java


## Performance

The below have been collected from repeated runs using the AB testing tool. This has been ran on a MacBook Pro (Retina, 13-inch, Early 2015)

|                     | At Rest / Startup                   | Under Load  |
| ------------------- |:-----------------------------------:| -----------:|
| Render Speed (ms)   | (Very slow on first hit 1min?) ~40  | ~15         |
| Throughput (msgs/s) | ~20                                 | ~60         |
| Memory Usage (Mb)   | ~250                                | ~650        |


## Tradeoffs

### Initial server side rendering speed:

The app will take quite some time to render pages on the first invocation (of each thread - up to 1min!?). This seems to be due
to the time it takes Nashorn to "compile" the server.js script (it presumably has to translate each JS function etc to Java
classes / methods). Each subsequent call (per thread) will be reasonably fast.

It should be noted that there is a fair amount of JS code present in this project once all of the dependent libraries
that are in use, i.e react, react-router, bootstrap, jquery, popper, some polyfills etc are taken into consideration. Had this been
a vanilla html / JS application, the performance would be drastically improved - but wouldn't, of course, demonstrate a real world
application.

After warm-up though (using ab to fire 5k requests w/ concurrency of 3), the average time to return a rendered page can drop to a very
reasonable ~15ms (running in a container locally on a MacBook Pro).

### Memory consumption

From some light testing, total memory usage (when ran within a containerised process) tends to sit around 700mb. For a relatively 
simple application this is obviously quite high.

Memory consumption of heap is relatively low and stable, and as expected the majority of memory is proportional to the number of threads
using dedicated instances of Nashorn for the rendering. This will reside in Metaspace - presumably from the number of classes created 
from "compiling" the JS.


## Known TODOs

- Warm-up is a real problem, can we warm a pool of nashorn instances up?
- Investigate J2V8 for faster initial render times?
- Could use netty and non blocking IO, async response with handoff to a dedicated executor with nashorn?
- SSR and Streaming nodes w/ React 16
