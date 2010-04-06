(ns thompson.core.forest-pair-test
  (:use clojure.test)
  (:import thompson.core.ForestLabel
           thompson.core.ForestPair
           thompson.core.Sample))

(def label-consts
  {"I" ForestLabel/I
   "N" ForestLabel/N
   "L" ForestLabel/L
   "R" ForestLabel/R
   "X" ForestLabel/X})

(defn forest-pair [upper-labels lower-labels upper-num lower-num]
  (ForestPair.
    (into-array (map label-consts (re-seq #"." upper-labels)))
    (into-array (map label-consts (re-seq #"." lower-labels)))
    upper-num lower-num))

(defn test-case [s u-lab l-lab u-num l-num]
  (is (= s
    (str (.toNormalForm (.toTreePair (forest-pair u-lab l-lab u-num l-num)))))))

(deftest test-to-tree-pair
  (test-case "(x_0^-1)"
    "R" "L" 0 1)
  (test-case "(x_1^1)"
    "I" "R" 0 0)
  (test-case "(x_2^1)(x_4^1)(x_3^-2)"
    "XIXI" "RXII" 0 0)
  (test-case "(x_0^4)(x_2^2)(x_1^-1)(x_0^-1)"
    "LIILLL" "ILRRRR" 6 2)
  (test-case "(x_1^1)(x_2^1)(x_3^2)(x_5^2)(x_0^-2)"
    "NNINII" "LLRRRR" 0 2))

(deftest test-word-length-fuzz
  (let [m (Sample/modelForestDiagrams 46)]
    (dotimes [_ 100]
      (let [f (Sample/chooseRandomForestPair m 42)]
        (is (= 42 (.wordLength (.toTreePair f))))))))
