(ns thompson.stat.gnuplot
  (:require [clojure.contrib.shell-out :as shell]
            [clojure.contrib.duck-streams :as stream]
            [clojure.contrib.java-utils :as java]
            [clojure.contrib.str-utils :as str]
            [clojure.contrib.seq-utils :as seq])
  (:import java.io.PrintWriter))

(defmacro with-tmp-dir
  [tmp-dir-fn-name & body]
  `(let [tmp-path#        "/tmp/thompson-gnuplot"
         tmp-dir#         (java/file tmp-path#)
         ~tmp-dir-fn-name (fn [s#] (str tmp-path# "/" s#))]
     (java/delete-file-recursively tmp-dir# true)
     (.mkdir tmp-dir#)
     (try
       ~@body
       (finally
         (java/delete-file-recursively tmp-dir# true)))))

(defn write-data [path data]
  (stream/write-lines path
    (map (fn [[ k v]] (str (pr-str k) " " (pr-str v))) data)))

(defn plot-command [tdir data]
  (str "plot "
    (str/str-join ", "
      (map (fn [[i [nums title lines]]]
             (str "\"" (tdir i) "\""
                  " using 1:2 title \"" title "\""
                  (if lines " with lines")))
           (seq/indexed data)))))


(defn exec [commands]
  (let [e-str (str/str-join "; " commands)
        res   (shell/sh "gnuplot" "-e" e-str :out :bytes :return-map true)]
    (if-not (zero? (:exit res))
      (throw (Exception. (String. #^"[B" (:err res) "UTF-8")))
      (:out res))))

(defn spit-bytes [f bs]
  (with-open [#^PrintWriter w (stream/writer f)]
    (stream/copy bs w)))
