(defproject thompson "0.1.0-SNAPSHOT"
  :description "A JVM library for performing computational experiments in Thompson's group F."
  :url "http://github.com/mmcgrana/thompson"
  :java-source-path "src"
  :clojure-source-path "src"
  :javac-fork "true"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]]
  :dev-dependencies [[lein-clojars "0.5.0-SNAPSHOT"]
                     [lein-javac "0.0.2-SNAPSHOT"]])
