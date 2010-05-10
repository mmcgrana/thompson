(ns thompson.analysis.commute
  (:import thompson.core.GenExp
           thompson.core.TreePair
           thompson.core.Sample))

(defn commute? [a b]
  (= (TreePair/multiply a b) (TreePair/multiply b a)))

(defn run2 [num-pairs lengths]
  (let [m (Sample/modelForestDiagrams (+ (apply max lengths) 4))]
    (doseq [l lengths]
      (loop [num-tried 0 num-comms 0]
        (if (< num-tried num-pairs)
          (let [a     (Sample/chooseTreePair m l)
                b     (Sample/chooseTreePair m l)]
            (recur (inc num-tried)
                   (if (commute? a b) (inc num-comms) num-comms)))
          (println l num-comms))))))

(defn run3 [num-trips lengths]
  (let [m (Sample/modelForestDiagrams (+ (apply max lengths) 4))]
    (doseq [l lengths]
      (loop [num-tried 0 num-comms 0]
        (if (< num-tried num-trips)
          (let [a     (Sample/chooseTreePair m l)
                b     (Sample/chooseTreePair m l)
                c     (Sample/chooseTreePair m l)]
            (recur (inc num-tried)
                   (if (and (commute? a b) (commute? b c) (commute? a c))
                     (inc num-comms)
                     num-comms)))
          (println l num-comms))))))

; (run2 1000000 [1 2 4 8 12 16 20 24 28 32 36 40])
; (run3 1000000 [1 2 3 4 5 6 7 8 9 10])
