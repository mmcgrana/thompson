(ns thompson.core.sample-test
  (:use clojure.test)
  (:import thompson.core.Sample))

(deftest test-count-forest-diagrams
  (is (= [1 4 14 44 135 398]
         (seq (Sample/numForestDiagrams 9)))))
