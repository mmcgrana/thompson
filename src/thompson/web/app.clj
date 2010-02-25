(ns thompson.web.app
  (:use (ring.middleware params reload stacktrace)
        (ring.handler dump)
        (thompson.web bounce-favicon)
        (clj-html core helpers))
  (:require (clojure.contrib [str-utils :as str]))
  (:import (thompson.core BaseExponent TreePair)))

(def sample-input
  "(x_2^-3)(x_1^2)\n(x_1^-4)(x_1^-1)")

(defn parse-elems [input]
  (map #(BaseExponent/fromString %) (str/re-split #"\s+" input)))

(defn unparse-elems [elems]
  (str/str-join "\n" (map str elems)))

(defn relator-normalize [elems]
  (map #(.toNormalForm #^BaseExponent %) elems))

(defn tree-normalize [elems]
  (map #(.toNormalForm (.toTreePair #^BaseExponent %)) elems))

(defn relator-invert [elems]
  (map #(.invert #^BaseExponent %) elems))

(defn tree-invert [elems]
  (map #(.toNormalForm (.invert (.toTreePair #^BaseExponent %))) elems))

(defn relator-product [elems]
  (list (BaseExponent/product (into-array elems))))

(defn tree-product [elems]
  (list (.toNormalForm (TreePair/product
          (into-array (map #(.toTreePair #^BaseExponent %) elems))))))

(defn word-length [elems]
  (map #(.wordLength (.toTreePair #^BaseExponent %)) elems))

(defn trees [elems]
  (map #(str (.toTreePair #^BaseExponent %)) elems))

(def operation-fns
  {"relator normalize" relator-normalize
   "tree normalize"    tree-normalize
   "relator invert"    relator-invert
   "tree invert"       tree-invert
   "relator product"   relator-product
   "tree product"      tree-product
   "word length"       word-length
   "trees"             trees})

(defn compute [operation input]
  (let [in-elems     (parse-elems input)
        operation-fn (operation-fns operation)
        out-elems    (operation-fn in-elems)]
    (unparse-elems out-elems)))

(defhtml not-found-view []
  [:p "404 Not Found"])

(defhtml layout-view [subtitle & content]
  (doctype :xhtml-transitional)
  [:html {:xmlns "http://www.w3.org/1999/xhtml"}
    [:head
      [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
      [:title (str "thompson: " subtitle)]]
    [:body
      content]])

(defn multi-submit [name values]
  (for [value values]
    [:input {:type "submit" :name name :value value}]))

(defn compute-view [& [input output]]
  (layout-view "compute"
    (html
      (form {:to [:post "/compute"]}
        "Input:" [:br]
        (text-area "input" input
          {:rows 10 :cols 140 :spellcheck false}) [:br]
        (multi-submit "compute"
          `("relator normalize" "tree normalize"
            "relator invert"    "tree invert"
            "relator product"   "tree product"
            "word length"       "trees")))
      [:br] [:br]
      "Output:" [:br]
      (text-area "output" output
        {:rows 10 :cols 140 :spellcheck false}) [:br]
      [:br]
      (link "help" "/help") " " (link "about" "/about"))))

(defn help-view []
  (layout-view "help"
    [:p "TODO: write help docs"]))

(defn about-view []
  (layout-view "about"
    [:p "A web interface to " [:code "thompson"] ", a JVM library for
         computational algebra in " (link "Thompson's group F"
         "http://en.wikipedia.org/wiki/Thompson_groups") "."]
    [:p "Source code is available on "
        (link "GitHub" "http://github.com/mmcgrana/thompson")
        " under an MIT license."]))

(defn respond [body & [opts]]
  {:status  (:status opts 200)
   :headers {"Content-Type" "text/html"}
   :body    body})

(defn core [req]
  (condp = [(:request-method req) (:uri req)]
    [:get "/"]
      (respond (compute-view sample-input))
    [:post "/compute"]
      (let [input     (get (:params req) "input")
            operation (get (:params req) "compute")
            output    (compute operation input)]
        (respond (compute-view input output)))
    [:get "/help"]
      (respond (help-view))
    [:get "/about"]
      (respond (about-view))
    (respond (not-found-view) {:status 404})))

(def app
  (-> #'core
    wrap-params
    (wrap-reload '(thompson.web.app))
    wrap-stacktrace
    wrap-bounce-favicon))
