(ns thompson.web.boot
  (:use ring.adapter.jetty
        thompson.web.app))

(run-jetty app {:port 8080})
