(ns nac.helpers
  (:require [nac.game :refer [is-winner?]]))

(defn get-cells-with [cells contains]
  (map first (filter (fn [c] (= (:contains (second c)) contains))
                     cells)))

(defn check-for-winner [cells]
  (let [xs (get-cells-with cells :x)
        os (get-cells-with cells :o)]
    (cond
     (is-winner? xs) :x
     (is-winner? os) :o
     :else false)))
