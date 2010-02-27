(ns thompson.web.prod-server
  (:use ring.util.servlet
        (clojure.contrib [def :only (defvar-)])
        (thompson.web bounce-favicon handler))
  (:gen-class :extends javax.servlet.http.HttpServlet))

(defvar- prod-app
  (-> handle-thompson
    wrap-bounce-favicon))

(defservice prod-app)
