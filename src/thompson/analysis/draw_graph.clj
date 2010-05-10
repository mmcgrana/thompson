(ns thompson.analysis.draw-graph
  (:require [clojure.contrib.str-utils :as str])
  (:import (thompson.core Sample)))

(defn node-id [short-label #^Node node]
  (if (.isLeaf node)
    (str short-label "l" (.leafIndex node))
    (str short-label "c" (.caretIndex node))))

(defn edge-line [short-label caret child]
  (str (node-id short-label caret) " -> " (node-id short-label child) ";"))

(defn tree-lines [short-label #^Node root]
  (.indexLeaves root)
  (.indexCarets root)
  (mapcat
    (fn [#^Node caret]
      [(edge-line short-label caret (.left caret))
       (edge-line short-label caret (.right caret))])
    (.carets root)))

(defn render-tree-pair [#^TreePair tp]
  (str/str-join "\n"
    (concat
      ["digraph pair {"]
      ["subgraph minus {"]
      (tree-lines "m" (.minusRoot tp))
      ["}"]
      ["subgraph plus {"]
      (tree-lines "p" (.plusRoot tp))
      ["}"]
      ["}"])))

(defn run [l n]
  (let [ip    (str "/Users/mmcgrana/workspace/thompson-samples/" l ".txt")
        lines (streams/read-lines ip)]
    (doseq [[i line] (seq/indexed (take n lines))]
      (let [tp (.toTreePair (GenExp/fromString line))
            op (str "/Users/mmcgrana/workspace/thompson-drawings/" l "-" i "-pair.dot")
            dt (render-tree-pair tp)]
        (streams/spit op dt)))))

; (run 100 5)
; (run 100 5)
; (run 100 5)
