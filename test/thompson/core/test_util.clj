(ns thompson.core.test-util
  (:import thompson.core.GenExp))

(defn- nrandom [n f]
  (take n (repeatedly f)))

(defn rand-factor [num-terms max-base max-exponent]
  (let [bases     (take num-terms (repeatedly #(rand-int max-base)))
        exponents (take num-terms
                    (remove zero?
                      (repeatedly #(- (rand-int (* 2 max-exponent)) max-exponent))))]
    (GenExp. (int-array bases) (int-array exponents))))
