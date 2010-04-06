(ns thompson.core.sample-test
  (:use clojure.test)
  (:import thompson.core.Sample))

(deftest test-count-forest-diagrams
  (is (= [1 4 14 44 135 398 1162]
         (seq (Sample/countForestDiagrams 10)))))

(deftest test-model-forest-diagrams
  (Sample/modelForestDiagrams 24))

(deftest test-choose-random-path
  (let [model (Sample/modelForestDiagrams 24)]
    (Sample/chooseRandomPath model 24)))

(deftest test-choose-random-forest-pair
  (let [model (Sample/modelForestDiagrams 46)]
    (Sample/chooseRandomForestPair model 42)))
