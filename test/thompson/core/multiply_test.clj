(ns thompson.core.multiply-test
  (:use clojure.test
        thompson.core.test-util)
  (:import (thompson.core TreePair GenExp)))

(deftest test-tree-pair-multiply
  (let [ft (.toTreePair (GenExp/fromString "(x_0^1)(x_4^-1)(x_1^-1)"))
        gt (.toTreePair (GenExp/fromString "(x_0^1)"))
        pt (TreePair/multiply ft gt)]
    (is (= "(x_0^2)(x_5^-1)(x_2^-1)" (.toString (.toNormalForm pt))))))

(deftest test-tree-pair-product
  (is (= "(x_0^2)(x_5^-1)(x_2^-1)"
         (.toString (.toNormalForm (TreePair/product
           (into-array (map #(.toTreePair (GenExp/fromString %))
                            ["(x_0^1)" "(x_4^-1)" "(x_1^-1)" "(x_0^1)"]))))))))

(deftest test-base-exponent-multiply
  (are [fs gs ps]
    (= (GenExp/fromString ps)
       (GenExp/multiply (GenExp/fromString fs)
                              (GenExp/fromString gs)))
    "(x_0^1)(x_4^-1)(x_1^-1)" "(x_0^1)"          "(x_0^2)(x_5^-1)(x_2^-1)"
    "(x_2^-3)(x_1^2)"         "(x_1^-4)(x_1^-1)" "(x_2^-3)(x_1^-3)"))

(deftest test-base-exponent-product
  (is (= "(x_0^2)(x_5^-1)(x_2^-1)"
         (.toString (GenExp/product
           (into-array (map #(GenExp/fromString %)
                            ["(x_0^1)" "(x_4^-1)" "(x_1^-1)" "(x_0^1)"])))))))

(deftest test-random-multiplications
  (doseq [l [1 2 4 8 16]]
    (dotimes [i 100]
      (let [f (rand-factor l 10 10)
            g (rand-factor l 10 10)]
        (let [p (GenExp/multiply f g)
              t (.toNormalForm (TreePair/multiply (.toTreePair f) (.toTreePair g)))]
          (is (= p t)))))))
