(defproject thompson "0.1.0-SNAPSHOT"
  :description "A JVM library for computational algebra in Thompson's group F."
  :url "http://github.com/mmcgrana/thompson"
  :java-source-path "src"
  :clojure-source-path "src"
  :javac-fork "true"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [org.apache.commons/commons-math "2.1"]]
  :dev-dependencies [[org.clojars.mmcgrana/lein-javac "0.1.0"]])
