(ns thompson.analysis.gen
  (:require [clojure.contrib.duck-streams :as streams])
  (:import thompson.core.Sample))

(def word-lengths [100 200 300])
(def word-count 100000)

(defn words-path [l]
  (str "/tmp/thompson-samples/" l ".txt"))

(defn run []
  (let [s (+ 4 (apply max word-lengths))
        m (Sample/modelForestDiagrams s)]
    (doseq [l word-lengths]
      (println l)
      (streams/write-lines (words-path l)
        (take word-count
          (repeatedly
            #(str (.toNormalForm (Sample/chooseTreePair m l)))))))))

; (run)
