(ns thompson.core.sample-test
  (:use clojure.test)
  (:import thompson.core.Sample))

(deftest test-count-forest-diagrams
  (is (= [1 4 14 44 135 398 1162]
         (seq (Sample/countForestDiagrams 10)))))

(deftest test-model-forest-diagrams
  (println (.size (Sample/modelForestDiagrams 24))))

(deftest test-choose-random-word
  (let [model (Sample/modelForestDiagrams 24)]
    (doseq [e (Sample/chooseRandomWord model 24)]
      (println e))))
