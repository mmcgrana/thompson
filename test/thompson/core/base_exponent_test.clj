(ns thompson.core.base-exponent-test
  (:use clojure.test)
  (:import thompson.core.BaseExponent))

(deftest test-to-from-string
  (are [s] (= s (.toString (BaseExponent/fromString s)))
    "(x_1^2)"
    "(x_1^2)(x_3^-4)"
    "(x_1^2)(x_3^-4)(x_1^50)(x_50^1)"))

