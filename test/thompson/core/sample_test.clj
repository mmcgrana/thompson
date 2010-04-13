(ns thompson.core.sample-test
  (:use clojure.test)
  (:import (thompson.core Sample ForestKey ForestState ForestLabel OfPointer)))

(deftest test-count-forest-diagrams
  (is (= [1 4 14 44 135 398 1162]
         (seq (Sample/countForestDiagrams 10)))))

(deftest test-model-forest-diagrams
  (Sample/modelForestDiagrams 24))

(deftest test-choose-random-forest-pair-fuzz
  (let [model (Sample/modelForestDiagrams 46)]
    (dotimes [_ 1000]
      (let [t (Sample/chooseTreePair model 42)]
        (is (= 42 (.wordLength t)))))))

(def forest-labels
  {:I ForestLabel/I
   :N ForestLabel/N
   :L ForestLabel/L
   :R ForestLabel/R
   :X ForestLabel/X})

(def of-pointers
  {:l OfPointer/LEFT
   :r OfPointer/RIGHT})

(defn- create-path [chain]
  (map
    (fn [[[u-label u-of-pointer u-excess] [l-label l-of-pointer l-excess]]]
      (ForestKey. 0
        (ForestState. (forest-labels u-label) (of-pointers u-of-pointer) u-excess)
        (ForestState. (forest-labels l-label) (of-pointers l-of-pointer) l-excess)))
    chain))

(def cases
  [["(x_0^-1)"
   `([[:R :r 0] [:L :l 0]])]
   ["(x_1^1)"
   `([[:I :r 0] [:R :r 0]])]
   ["(x_2^1)(x_4^1)(x_3^-2)"
   `([[:X :r 0] [:R :r 0]]
     [[:I :r 0] [:X :r 0]]
     [[:X :r 0] [:I :r 0]]
     [[:I :r 0] [:I :r 0]])]])

(deftest test-reify-tree-pair
  (doseq [[w chain] cases]
    (let [path (create-path chain)
          tp   (Sample/reifyTreePair path)
          nf   (.toNormalForm tp)]
    (is (= w (str nf))))))
