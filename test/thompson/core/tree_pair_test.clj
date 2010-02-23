(ns thompson.core.tree-pair-test
  (:use clojure.test)
  (:import (thompson.core TreePair BaseExponent)))

(deftest test-from-term
  (are [b e s] (= s (.toString (TreePair/fromTerm b e)))
    0  1 "[(*(*(**)))|(((**)*)*)]"
    0 -1 "[(((**)*)*)|(*(*(**)))]"
    1  1 "[(*(*(*(**))))|(*(((**)*)*))]"
    1 -1 "[(*(((**)*)*))|(*(*(*(**))))]"
    2  4 "[(*(*(*(*(*(*(*(**))))))))|(*(*((((((**)*)*)*)*)*)))]"
    2 -4 "[(*(*((((((**)*)*)*)*)*)))|(*(*(*(*(*(*(*(**))))))))]"))
