(ns thompson.analysis.commute
  (:import thompson.core.GenExp
           thompson.core.TreePair
           thompson.core.Sample))

(defn run [num-pairs lengths]
  (let [m (Sample/modelForestDiagrams (+ (apply max lengths) 4))]
    (doseq [l lengths]
      (loop [num-tried 0 num-comms 0]
        (if (< num-tried num-pairs)
          (let [a     (Sample/chooseTreePair m l)
                b     (Sample/chooseTreePair m l)
                ab    (TreePair/multiply a b)
                ba    (TreePair/multiply b a)
                comm? (= ab ba)]
            (recur (inc num-tried) (if comm? (inc num-comms) num-comms)))
          (println l num-comms))))))

; (run 1000000 [1 2 4 8 12 16 20 24 28 32 36 40])
