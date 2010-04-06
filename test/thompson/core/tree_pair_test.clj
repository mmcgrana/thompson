(ns thompson.core.tree-pair-test
  (:use clojure.test
        thompson.core.test-util)
  (:import thompson.core.TreePair
           thompson.core.GenExp
           thompson.core.CaretType))

(deftest test-one-type-l0-is-disallowed
  (is (thrown? IllegalArgumentException
    (TreePair/contribution CaretType/L0 CaretType/IR))))

(deftest test-contribution
  (is (= 4 (TreePair/contribution CaretType/IR CaretType/I0))))

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
    (is (= 12 (.wordLength (.toTreePair (GenExp/fromString elem)))))))

(deftest test-invert
  (is (= (.toTreePair (GenExp/fromString "(x_4^1)(x_3^-2)(x_0^-5)"))
         (.invert (.toTreePair (GenExp/fromString "(x_0^5)(x_3^2)(x_4^-1)"))))))

(deftest test-word-length-inverse-fuzz
  (doseq [l [1 2 4 8 16]]
    (dotimes [_ 100]
      (let [e (rand-factor l 10 10)
            t (.toTreePair e)]
        (is (= (.wordLength t) (.wordLength (.invert t))))))))
