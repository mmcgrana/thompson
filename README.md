# thompson

A JVM library for performing computational experiments in Thompson's group F.

Includes routines for ...

These routines are implemented in Java and easily accessible from any JVM language.

This distribution also includes the source for a web interface to these algorithms, implemented in Clojure on the Ring library. This interface is accessible at [...](http://google.com).

## Development

First, get [Leiningen](http://github.com/technomancy/leiningen) 1.1.0. Then compile and test the project as follows.

    $ lein clean
    $ lein deps
    $ lein compile-java
    $ lein test

