(ns thompson.web.handler-test
  (:use clojure.test
        thompson.web.handler))

(deftest test-index
  (let [req  {:request-method :get :uri "/"}
        resp (handle-thompson req)]
    (is (re-find #"Input" (:body resp)))))

(deftest test-about
  (let [req  {:request-method :get :uri "/about"}
        resp (handle-thompson req)]
    (is (re-find #"information" (:body resp)))))

(deftest test-help
  (let [req  {:request-method :get :uri "/help"}
        resp (handle-thompson req)]
    (is (re-find #"operations" (:body resp)))))

(deftest test-compute
  (let [req  {:request-method :post :uri "/compute"
              :params {"compute" "invert",
                       "input"   "(x_2^-3)(x_1^2)\r\n(x_1^-4)(x_1^-1)"}}
        resp (handle-thompson req)]
    (is (= 200 (:status resp)))))

(deftest test-input-error-handling)


"(x_2^-3)(x_1^2)\r\n(x_1^)(x_1^-1)"
#input error on line 2:
input error:
expected '^', got ')', rest of input is ')(x_1^-1)'

