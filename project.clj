(defproject thompson "0.1.0-SNAPSHOT"
  :description "A JVM library for computational algebra in Thompson's group F."
  :url "http://github.com/mmcgrana/thompson"
  :java-source-path "src"
  :clojure-source-path "src"
  :javac-fork "true"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [ring/ring-core "0.2.0-SNAPSHOT"]
                 [clj-html "0.1.0-SNAPSHOT"]]
  :dev-dependencies [[ring/ring-jetty-adapter "0.2.0-SNAPSHOT"]
                     [ring/ring-devel "0.2.0-SNAPSHOT"]
                     [lein-clojars "0.5.0-SNAPSHOT"]
                     [lein-javac "0.0.2-SNAPSHOT"]]
  :namespaces [thompson.web.handler
               thompson.web.bounce-favicon
               thompson.web.prod-serve])
