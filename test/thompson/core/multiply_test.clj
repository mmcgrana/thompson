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

; (deftest test-base-exponent-multiply
;   (are [fs gs ps]
;     (= (BaseExponent/fromString ps)
;        (BaseExponent/multiply (BaseExponent/fromString fs)
;                               (BaseExponent/fromString gs)))
;     "(x_0^1)(x_4^-1)(x_1^-1)" "(x_0^1)"          "(x_0^2)(x_5^-1)(x_2^-1)"
;     "(x_2^-3)(x_1^2)"         "(x_1^-4)(x_1^-1)" "(x_2^-3)(x_1^-3)"))

(deftest test-base-exponent-product
  (is (= "(x_0^2)(x_5^-1)(x_2^-1)"
         (.toString (BaseExponent/product
           (into-array (map #(BaseExponent/fromString %)
                            ["(x_0^1)" "(x_4^-1)" "(x_1^-1)" "(x_0^1)"])))))))

(def num-trials 100)
(def num-terms 2)
(def max-base  4)
(def max-abs-exponent 4)

(defn nrandom [n f]
  (take n (repeatedly f)))

(defn rand-factor []
  (let [bases     (take num-terms (repeatedly #(rand-int max-base)))
        exponents (take num-terms
                    (remove #{0}
                      (repeatedly #(- (rand-int (* 2 max-abs-exponent)) max-abs-exponent))))]
    (let [factor
      (BaseExponent. (int-array bases) (int-array exponents))]
      (println factor)
      factor)))

;(deftest test-random-multiplications
;  (dotimes [_ num-trials]
;    (Thread/sleep 100)
;    (println ".")
;    (let [f (rand-factor)
;          g (rand-factor)]
;      (is (= (BaseExponent/multiply f g)
;             (.toNormalForm (TreePair/multiply (.toTreePair f) (.toTreePair g))))))))
