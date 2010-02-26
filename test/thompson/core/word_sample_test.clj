(ns thompson.core.word-sample-test
  (:use clojure.test)
  (:import thompson.core.WordSample))

(deftest test-count-forest-diagrams
  (is (= [1 4 14 44 135 398]
         (seq (WordSample/numForestDiagrams 9)))))
