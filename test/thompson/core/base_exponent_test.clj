(ns thompson.core.base-exponent-test
  (:use clojure.test)
  (:import thompson.core.BaseExponent))

(deftest test-to-from-string
  (are [s] (= s (.toString (BaseExponent/fromString s)))
    "(x_1^2)"
    "(x_1^2)(x_3^-4)"
    "(x_1^2)(x_3^-4)(x_1^50)(x_50^1)"))

(deftest test-to-normal-form
  (are [gf nf] (= nf (.toString (.toNormalForm (BaseExponent/fromString gf))))
    "(x_1^2)"                                  "(x_1^2)"
    "(x_0^2)(x_1^4)"                           "(x_0^2)(x_1^4)"
    "(x_0^1)(x_1^-1)"                          "(x_0^1)(x_1^-1)"
    "(x_0^-2)(x_1^-2)(x_0^-1)(x_1^-1)(x_0^-2)" "(x_6^-1)(x_3^-2)(x_0^-5)"
    "(x_0^1)(x_4^-1)(x_1^-1)(x_0^1)"           "(x_0^2)(x_5^-1)(x_2^-1)"))
