(ns thompson.web.handler
  (:use (clj-html core helpers)
        (clojure.contrib [def :only (defvar-)])
        ring.handler.dump
        ring.middleware.params)
  (:require (clojure.contrib [str-utils :as str]))
  (:import (thompson.core BaseExponent TreePair)))

(defvar- sample-input
  "(x_2^-3)(x_1^2)\n(x_1^-4)(x_1^-1)")

(defn- parse-elems [input]
  (map #(BaseExponent/fromString %) (str/re-split #"\s+" input)))

(defn- unparse-elems [elems]
  (str/str-join "\n" (map str elems)))

(defn- normalize [elems]
  (map #(.toNormalForm #^BaseExponent %) elems))

(defn- invert [elems]
  (map #(.invert #^BaseExponent %) elems))

(defn- product [elems]
  (list (BaseExponent/product (into-array elems))))

(defn- word-length [elems]
  (map #(.wordLength (.toTreePair #^BaseExponent %)) elems))

(defvar- operation-fns
  {"normalize"   normalize
   "invert"      invert
   "product"     product
   "word length" word-length})

(defn- compute [operation input]
  (try
    (let [in-elems     (parse-elems input)
          operation-fn (operation-fns operation)
          out-elems    (operation-fn in-elems)]
      (unparse-elems out-elems))
    (catch Exception e
      (let [m (.getMessage e)]
        (if (and m (re-find #"expected" m))
          (str/str-join "\n"
            ["input error:"
             (re-find #"expected.*" m)])
          (throw e))))))

(defn- not-found-view []
  (html
    [:p "404 Not Found"]))

(defn- layout-view [subtitle & content]
  (html
    (doctype :xhtml-transitional)
    [:html {:xmlns "http://www.w3.org/1999/xhtml"}
      [:head
        [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
        [:title (str "thompson: " subtitle)]]
      [:body
        content]]))

(defn- multi-submit [name values]
  (for [value values]
    [:input {:type "submit" :name name :value value}]))

(defn- compute-view [& [input output]]
  (layout-view "compute"
    (form {:to [:post "/compute"]}
      "Input:" [:br]
      (text-area "input" input
        {:rows 10 :cols 140 :spellcheck false}) [:br]
      (multi-submit "compute"
        `("normalize" "invert" "product" "word length")))
    [:br] [:br]
    "Output:" [:br]
    (text-area "output" output
      {:rows 10 :cols 140 :spellcheck false}) [:br]
    [:br]
    (link "about" "/about") " " (link "help" "/help")))

(defn- help-view []
  (layout-view "help"
    [:p "Enter one element from Thompson's group F on each line of the input
         box. Elements must in the form (x_i^a)(x_j^b)(x_k^c) etc."]
    [:p "Then select one of the available operations:"]
    [:p [:code "normalize"]
        ": Find the unique normal form for each given element."]
    [:p [:code "invert"]
        ": Find the inverse for each given element."]
    [:p [:code "product"]
        ": Compute the product of the given elements and show the result in
         unique normal form."]
    [:p [:code "word length"]
        ": Calculate the word length for each given element with respect  to the
        {x_0, x_1} generating set."]))

(defn- about-view []
  (layout-view "about"
    [:p "This is the web interface to " [:code "thompson"] ", a JVM library for
         computational algebra in " (link "Thompson's group F"
         "http://en.wikipedia.org/wiki/Thompson_groups") "."]
    [:p "Usage information is available on the " (link "help page" "/help") "."]
    [:p "Please send comments, questions, and bug reports to "
         (link "Mark McGranaghan" "http://markmcgranaghan.com") " at "
         (link "mmcgrana@gmail.com" "mailto:mmcgrana@gmail.com")]
    [:p "The source code for this application is available on "
        (link "GitHub" "http://github.com/mmcgrana/thompson") "."]))

(defn- respond [body & [opts]]
  {:status  (:status opts 200)
   :headers {"Content-Type" "text/html"}
   :body    body})

(defn- core [req]
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

(def handle-thompson
  (-> core
    wrap-params))
