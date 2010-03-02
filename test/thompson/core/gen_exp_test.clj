(ns thompson.core.gen-exp-test
  (:use clojure.test
        thompson.core.test-util)
  (:import thompson.core.GenExp))

(deftest test-to-from-string
  (are [s] (= s (.toString (GenExp/fromString s)))
    ""
    "(x_1^2)"
    "(x_1^2)(x_3^-4)"
    "(x_1^2)(x_3^-4)(x_1^50)(x_50^1)"))

(deftest test-to-normal-form
  (are [gf nf] (= nf (.toString (.toNormalForm (GenExp/fromString gf))))
    "(x_1^2)"                                  "(x_1^2)"
    "(x_0^2)(x_0^-2)"                          ""
    "(x_0^-2)(x_1^1)"                          "(x_3^1)(x_0^-2)"
    "(x_0^2)(x_1^4)"                           "(x_0^2)(x_1^4)"
    "(x_0^1)(x_1^-1)"                          "(x_0^1)(x_1^-1)"
    "(x_0^-2)(x_1^-2)(x_0^-1)(x_1^-1)(x_0^-2)" "(x_6^-1)(x_3^-2)(x_0^-5)"))

(deftest test-is-normal-form
  (are [f normal] (= normal (.isNormalForm (GenExp/fromString f)))
    "(x_1^2)(x_2^1)"                 true
    "(x_4^-1)(x_3^-2)"               true
    "(x_1^2)(x_2^1)(x_4^-1)(x_3^-2)" true
    "(x_2^1)(x_1^2)"                 false
    "(x_3^-2)(x_4^-1)"               false
    "(x_1^2)(x_2^1)(x_3^-2)(x_4^-1)" false))

(deftest test-normal-form-fuzz
  (doseq [l [1 2 4 8 16]]
    (dotimes [_ 100]
      (let [f (rand-factor l 10 10)]
        (is (.isNormalForm (.toNormalForm f)))))))

(deftest test-to-unique-normal-form
  (are [normal unique]
    (= unique
       (.toString (.toUniqueNormalForm
                    (.toNormalForm (GenExp/fromString normal)))))
    "(x_1^2)(x_4^-3)(x_1^-5)"                 "(x_2^-3)(x_1^-3)"
    "(x_74^41)(x_117^-24)(x_74^-12)(x_9^-45)" "(x_74^29)(x_105^-24)(x_9^-45)"))

(deftest test-is-unique-normal-form
  (are [f unique] (= unique (.isUniqueNormalForm (GenExp/fromString f)))
    "(x_1^2)(x_4^-3)(x_1^-5)"                 false
    "(x_2^-3)(x_1^-3)"                        true
    "(x_74^41)(x_117^-24)(x_74^-12)(x_9^-45)" false
    "(x_74^29)(x_105^-24)(x_9^-45)"           true
    "(x_3^1)(x_4^-2)(x_3^-4)"                 true))

(deftest test-invert
  (is (= (GenExp/fromString "(x_4^1)(x_3^-2)(x_0^-5)")
         (.invert (GenExp/fromString "(x_0^5)(x_3^2)(x_4^-1)")))))
