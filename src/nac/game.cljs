(ns nac.game)

;; Initializers========================================
(defn blank-board []
  (let [xys (for [x [1 2 3], y [1,2,3]] [x y])]
    (zipmap
     xys
     (map (fn [[x y]] {:x x :y y :contains nil}) xys))))

(defn new-game []
  {:cells (blank-board)
   :to-play :x
   :winner false})


;; Win checking ========================================

;; We use the magic-squares method of checking for a winner in naughts
;; and crosses. Each cell is mapped to a number; if either player
;; plays in a collection of cells adding up to 15, they've won.

(def magic-square
  ;;Magic squares method of checkcing noughts and crosses
  ;;  http://webserv.jcu.edu/math//Vignettes/magicsquares.htm
  {[1 1] 6, [1 2] 1, [1 3] 8,
   [2 1] 7, [2 2] 5, [2 3] 3,
   [3 1] 2, [3 2] 9, [3 3] 4})

(defn numbers-claimed-by [x cells]
  (set
   (map (fn [cell]
          (let [x (:x cell)
                y (:y cell)]
            (magic-square [x y])))
        (filter #(= x (:contains %))
                cells))))

(defn combinations [ns]
  (loop [pos 0
         combos []]
    (if (= pos (count ns))
      combos
      (let [xs (take pos ns)
            ys (drop pos ns)]
        (recur
         (inc pos)
         (concat combos (for [y ys]
                          (conj xs y))))))))

(defn is-winner? [ns]
  (some #(= 15 (apply + %)) (combinations ns)))

(defn exists-winner? [cells]
  (let [xs (numbers-claimed-by :x (vals cells))
        os (numbers-claimed-by :o (vals cells))]
    (cond
     (is-winner? xs) :x
     (is-winner? os) :o
     :else false)))


;; Move Making ========================================
(defn play-in-space [board to-play [x y :as location]]
  (let [new-cells (assoc
                      (:cells board) [x y]
                      {:x x, :y y, :contains to-play})]
    (assoc board
      :cells new-cells
      :to-play ({:x :o, :o :x} to-play)
      :winner (exists-winner? new-cells))))
