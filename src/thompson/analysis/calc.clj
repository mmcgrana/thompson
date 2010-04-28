(ns thompson.analysis.calc
  (:require [clojure.contrib.duck-streams :as stream]
            [clojure.contrib.seq-utils :as seq]
            [clojure.contrib.str-utils :as str]
            [thompson.analysis.gnuplot :as gnuplot])
  (:import thompson.core.GenExp
           org.apache.commons.math.analysis.descriptive.DescriptiveStatistics))

(defn words-path [l]
  (str "/tmp/thompson-samples/" l ".txt"))

(defn results-path [flabel ext]
  (str "/tmp/thompson-results/" flabel "." ext))

(defn words-seq [l]
  (map #(GenExp/fromString %) (take 100000 (stream/read-lines (words-path l)))))

(defn gen-exps-seq [l]
  (map
    (fn [#^GenExp word]
      (map list (.gens word) (.exps word)))
    (words-seq l)))

(defn signed-gen-exps-seq [l sign]
  (cond
    (nil? sign)
      (gen-exps-seq l)
    (= :pos sign)
      (map
        (fn [gen-exps] (filter (fn [[gen exp]] (> exp 0)) gen-exps))
        (gen-exps-seq l))
    (= :neg sign)
      (map
        (fn [gen-exps] (filter (fn [[gen exp]] (< exp 0)) gen-exps))
        (gen-exps-seq l))))

(defn assert-sign [sign]
  (assert (#{:pos :neg} sign)))

(defn sum [nums]
  (reduce + nums))

(defn num-terms* [gen-exp]
  (count gen-exp))

(defn num-terms [l sign]
  (let [gen-exps (signed-gen-exps-seq l sign)]
    (map num-terms* gen-exps)))

(defn generators* [gen-exp]
  (map
    (fn [[gen exp]]
      (if (pos? exp) gen (- gen)))
    gen-exp))

(defn generators [l]
  (let [gen-exps (signed-gen-exps-seq l nil)]
    (apply concat (map generators* gen-exps))))

(defn difference [[a b]]
  (- b a))

(defn generator-gaps* [gen-exp]
  (map difference (partition 2 1 (generators* gen-exp))))

(defn generator-gaps [l sign]
  (assert (#{:pos :neg} sign))
  (let [gen-exps (signed-gen-exps-seq l sign)]
    (apply concat (map generator-gaps* gen-exps))))

(defn exponents* [gen-exp]
  (map #(Math/abs #^Integer (second %)) gen-exp))

(defn exponents [l sign]
  (let [gen-exps (signed-gen-exps-seq l sign)]
    (apply concat (map exponents* gen-exps))))

(defn exponents-sum* [gen-exp]
  (sum (exponents* gen-exp)))

(defn exponents-sum [l sign]
  (let [gen-exps (signed-gen-exps-seq l sign)]
    (map exponents-sum* gen-exps)))

(defn exponents-xzero* [gen-exp]
  (if-let [[gen-zero exp] (first (filter #(= 0 (first %)) gen-exp))]
    (Math/abs #^Integer exp)
    0))

(defn exponents-xzero [l sign]
  (assert-sign sign)
  (let [gen-exps (signed-gen-exps-seq l sign)]
    (map exponents-xzero* gen-exps)))

(defn caret-types* [#^GenExp gen-exp]
  (let [tree-pair (.toTreePair gen-exp)
        neg-types (.caretTypes (.minusRoot tree-pair))
        pos-types (.caretTypes (.plusRoot  tree-pair))]
    (concat (map str neg-types) (map str pos-types))))

(defn caret-types [l]
  (apply concat (map caret-types* (words-seq l))))

(defn caret-type-pairs* [#^GenExp gen-exp]
  (let [tree-pair (.toTreePair gen-exp)
        neg-types (.caretTypes (.minusRoot tree-pair))
        pos-types (.caretTypes (.plusRoot  tree-pair))]
    (map
      (fn [types]
        (let [[t1 t2] (sort types)]
          (str t1 "-" t2)))
      (map list neg-types pos-types))))

(defn caret-type-pairs [l]
  (apply concat (map caret-type-pairs* (words-seq l))))

(defn calc-freqs [vals]
  (sort-by key (seq/frequencies vals)))

(defn compress-freqs [limit freqs]
  (if (<= (count freqs) limit)
    freqs
    (->> freqs
      (seq/partition-all 2)
      (map
        (fn [[[k1 n1] [k2 n2]]]
          (if k2
            [k1 (+ n1 n2)]
            [k1 n1])))
      (compress-freqs limit))))

(defn calc-stats [vals]
  (let [dstats (DescriptiveStatistics.)]
    (doseq [v vals]
      (.addValue dstats (double v)))
    {:mean  (.getMean dstats)
     :min   (.getMin  dstats)
     :10pct (.getPercentile dstats 10.0)
     :25pct (.getPercentile dstats 25.0)
     :50pct (.getPercentile dstats 50.0)
     :75pct (.getPercentile dstats 75.0)
     :90pct (.getPercentile dstats 90.0)
     :max   (.getMax dstats)}))

(defn print-cat-summary [label vals]
  (let [flabel   (str/re-gsub #"[^a-z0-9]+" "-" label)
        path-raw (results-path flabel "txt")
        path-dat (results-path flabel "dat")
        path-eps (results-path flabel "eps")
        freqs    (calc-freqs vals)]
    (println label)
    (doseq [[k c] freqs]
      (println k c))
    (gnuplot/write-data path-raw freqs)
    (gnuplot/write-data path-dat freqs)
    (gnuplot/spit-bytes path-eps
      (gnuplot/exec
        ["set term postscript eps enhanced"
         "set boxwidth 0.75 absolute"
         "set style histogram rowstacked"
         "set style data histograms"
         "set key off"
         "set xtics rotate by 90"
         (str "plot '" path-dat "' using 2:xtic(1)")]))
    (println)))

(defn print-summary [label vals]
  (let [flabel   (str/re-gsub #"[^a-z0-9]+" "-" label)
        path-raw (results-path flabel "txt")
        path-dat (results-path flabel "dat")
        path-eps (results-path flabel "eps")
        _        (println label)
        freqs    (calc-freqs vals)
        stats    (calc-stats vals)]
    (doseq [[k c] freqs]
      (println k c))
    (println stats)
    (gnuplot/write-data path-raw (concat stats freqs))
    (gnuplot/write-data path-dat (compress-freqs 30 freqs))
    (gnuplot/spit-bytes path-eps
      (gnuplot/exec
        ["set term postscript eps enhanced"
         "set boxwidth 1.0 relative"
         "set style data histogram"
         "set style histogram cluster gap 0"
         "set key off"
         (str "plot '" path-dat "' using 2:xtic(1)")]))
    (println)))

(defn run [len]
  (print-summary (str len " num-terms all")        (num-terms        len nil))
  (print-summary (str len " num-terms pos")        (num-terms        len :pos))
  (print-summary (str len " generators")           (generators       len))
  (print-summary (str len " generator-gaps pos")   (generator-gaps   len :pos))
  (print-summary (str len " exponents all")        (exponents        len nil))
  (print-summary (str len " exponents pos")        (exponents        len :pos))
  (print-summary (str len " exponents-sum all")    (exponents-sum    len nil))
  (print-summary (str len " exponents-sum pos")    (exponents-sum    len :pos))
  (print-summary (str len " exponents-xzero pos")  (exponents-xzero  len :pos))
  (print-cat-summary (str len " caret-types")      (caret-types      len))
  (print-cat-summary (str len " caret-type-pairs") (caret-type-pairs len)))

(run 100)
(run 200)
(run 300)
