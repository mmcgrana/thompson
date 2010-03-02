(ns thompson.web.dev-server
  (:use ring.adapter.jetty
        (ring.middleware reload stacktrace)
        (thompson.web handler bounce-favicon)))

(def dev-app
  (-> #'handle-thompson
    (wrap-reload '(thompson.web.handler))
    wrap-stacktrace
    wrap-bounce-favicon))

(run-jetty dev-app {:port 8080})
