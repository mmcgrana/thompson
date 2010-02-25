(ns thompson.web.bounce-favicon)

(defn wrap-bounce-favicon [handler]
  (fn [req]
    (if (= "/favicon.ico" (:uri req))
      {:status 404 :headers {"Content-Type" "text/html"} :body ""}
      (handler req))))
