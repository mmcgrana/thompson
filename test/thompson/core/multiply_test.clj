(ns thompson.core.multiply-test
  (:use clojure.test)
  (:import (thompson.core TreePair BaseExponent)))

(deftest test-tree-pair-multiply
  (let [ft (.toTreePair (BaseExponent/fromString "(x_0^1)(x_4^-1)(x_1^-1)"))
        gt (.toTreePair (BaseExponent/fromString "(x_0^1)"))
        pt (TreePair/multiply ft gt)]
    (is (= "(x_0^2)(x_5^-1)(x_2^-1)" (.toString (.toNormalForm pt))))))

(deftest test-tree-pair-product
  (is (= "(x_0^2)(x_5^-1)(x_2^-1)"
         (.toString (.toNormalForm (TreePair/product
           (into-array (map #(.toTreePair (BaseExponent/fromString %))
                            ["(x_0^1)" "(x_4^-1)" "(x_1^-1)" "(x_0^1)"]))))))))

; TODO: fix

(deftest test-base-exponent-multiply
  )

(deftest test-base-exponent-product
  )

(deftest test-random-multiplications
  )

; TODO: finish
