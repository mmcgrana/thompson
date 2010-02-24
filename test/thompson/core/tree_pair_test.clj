(ns thompson.core.tree-pair-test
  (:use clojure.test)
  (:import (thompson.core TreePair BaseExponent)))

(deftest test-from-term
  (are [b e s] (= s (.toString (TreePair/fromTerm b e)))
    0  0 "[*|*]"
    10 0 "[*|*]"
    0  1 "[(*(**))|((**)*)]"
    0 -1 "[((**)*)|(*(**))]"
    1  1 "[(*(*(**)))|(*((**)*))]"
    1 -1 "[(*((**)*))|(*(*(**)))]"
    2  4 "[(*(*(*(*(*(*(**)))))))|(*(*(((((**)*)*)*)*)))]"
    2 -4 "[(*(*(((((**)*)*)*)*)))|(*(*(*(*(*(*(**)))))))]"))

(deftest test-word-length
  (let [elem "(x_1^1)(x_3^1)(x_5^-1)(x_4^-2)(x_0^-1)"]
    (is (= 12 (.wordLength (.toTreePair (BaseExponent/fromString elem)))))))

; TODO: random element inversion-preserves-length test

(deftest test-invert
  (is (= (.toTreePair (BaseExponent/fromString "(x_4^1)(x_3^-2)(x_0^-5)"))
         (.invert (.toTreePair (BaseExponent/fromString "(x_0^5)(x_3^2)(x_4^-1)"))))))
