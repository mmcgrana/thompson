(ns thompson.core.caret-type-test
  (:use clojure.test)
  (:import thompson.core.CaretType))

(deftest one-type-l0-is-disallowed
  (is (thrown? IllegalArgumentException
        (CaretType/contribution CaretType/L0 CaretType/IR))))

(deftest contribution-is-determined
  (is (= 4 (CaretType/contribution CaretType/IR CaretType/I0))))
