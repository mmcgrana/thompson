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

(deftest test-base-exponent-multiply
  (let [fbe (BaseExponent/fromString "(x_0^1)(x_4^-1)(x_1^-1)")
        gbe (BaseExponent/fromString "(x_0^1)")
        pbe (BaseExponent/multiply fbe gbe)]
    (is (= "(x_0^2)(x_5^-1)(x_2^-1)" (.toString pbe)))))

(deftest test-base-exponent-product
  (is (= "(x_0^2)(x_5^-1)(x_2^-1)"
         (.toString (BaseExponent/product
           (into-array (map #(BaseExponent/fromString %)
                            ["(x_0^1)" "(x_4^-1)" "(x_1^-1)" "(x_0^1)"])))))))

(deftest test-random-multiplications
  )

; TODO: finish
