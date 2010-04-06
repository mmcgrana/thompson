(ns thompson.core.parser-test
  (:use clojure.test)
  (:import thompson.core.Parser))

(deftest test-parser
  (let [p (Parser. "foo bar bat")]
    (is (= "foo" (.next p #"\w+")))
    (is (nil? (.next p #"\w+")))
    (.next p #"\s")
    (is (= "bar" (.next p #"\w+")))
    (is (= " bat" (.rest p)))
    (is (not (.isEnd p)))
    (.next p #"\s\w+")
    (is (.isEnd p))))
