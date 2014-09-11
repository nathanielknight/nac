(ns nac.game)

;; We use the magic-squares method of checking for a winner in naughts
;; and crosses. Each cell is mapped to a number in a magic square such
;; that if a player's pieces occupy the positions of numbers adding up
;; to the magic square's value, they've won.

;; If this doesn't make sense, google it. It's a neat trick, and
;; you've missed out!

(def magic-square
  ;;Magic squares method of checkcing noughts and crosses
  ;;  http://webserv.jcu.edu/math//Vignettes/magicsquares.htm
  {[1 1] 6, [1 2] 1, [1 3] 8,
   [2 1] 7, [2 2] 5, [2 3] 3,
   [3 1] 2, [3 2] 9, [3 3] 4})

(defn combinations
  "Given a collection, return a svector of all combinations of items
   in the collection (except the empty set)."
  ; This isn't very idiomatic; I think the index could be replaced by
  ; a partition or a reduce. Mabe? I think it would take a third
  ; argument in the recur which contains the vector of those elements
  ; already processed.
  [ns]
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

(defn is-winner?
  "Given a seq of [x y] vectors, check if they contain a winning set
  of naughts and crosses positions."
  [xys]
  (some
   #(= 15 (apply + %))
   (combinations
    (map magic-square xys))))
