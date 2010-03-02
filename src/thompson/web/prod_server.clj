(ns thompson.web.prod-server
  (:use ring.util.servlet
        (thompson.web bounce-favicon handler))
  (:gen-class :extends javax.servlet.http.HttpServlet))

(def prod-app
  (-> handle-thompson
    wrap-bounce-favicon))

(defservice prod-app)
