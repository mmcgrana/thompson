(ns thompson.analysis.draw-graph
  (:import (thompson.core Sample ForestKey ForestState ForestLabel OfPointer
                          BackPointers BackPointer)))


(defn vertex-label [model vertex]
  (str vertex))
  ;(str "\"" vertex " (" (.totalBackCount #^BackPointers (get model vertex)) ")\""))

(defn edge-label [model from-vertex]
  (str (.totalBackCount #^BackPointers (get model from-vertex))))

(defn find-viable-vertices [model max-weight]
  (let [final-vertex (ForestKey. max-weight
                       (ForestState. ForestLabel/R OfPointer/RIGHT 0)
                       (ForestState. ForestLabel/R OfPointer/RIGHT 0))]
    (loop [found #{} unexplored (list final-vertex)]
      (if (empty? unexplored)
        found
        (let [[unexplored-head & unexplored-tail] unexplored]
          (if (found unexplored-head)
            (recur found unexplored-tail)
            (let [unexplored-new (map #(.backKey #^BackPointer %)
                                      (.backPointers #^BackPointers (get model unexplored-head)))]
              (recur (conj found unexplored-head)
                     (concat unexplored-new unexplored-tail)))))))))

(defn drawable-vertex? [max-weight #^ForestKey vertex]
  (or (< (.weight vertex) max-weight)
      (= vertex
        (ForestKey. max-weight
                    (ForestState. ForestLabel/R OfPointer/RIGHT 0)
                    (ForestState. ForestLabel/R OfPointer/RIGHT 0)))))

(defn run [max-weight]
  (printf "digraph model {\n  rankdir=\"LR\";\n")
  (let [model           (Sample/modelForestDiagrams max-weight)
        viable-vertices (find-viable-vertices model max-weight)]
    (doseq [from-vertex (keys model)]
      (let [to-vertices (Sample/successorKeys from-vertex)]
        (doseq [#^ForestKey to-vertex to-vertices]
          (when (viable-vertices to-vertex)
            (printf "  %s -> %s [label=%s]\n"
                    (vertex-label model from-vertex)
                    (vertex-label model to-vertex)
                    (edge-label model from-vertex)))))))
  (printf "}\n"))

; (run (Integer/parseInt (first *command-line-args*)))
