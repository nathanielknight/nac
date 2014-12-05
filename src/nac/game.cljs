(ns nac.game)

;; We use the magic-squares method of checking for a winner in naughts
;; and crosses. Each cell is mapped to a number in a magic square such
;; that if a player's pieces occupy the positions of numbers adding up
;; to the magic square's value, they've won.

;; If this doesn't make sense, google it. It's a neat trick!


(def magic-square
  ;;Magic squares method of checkcing noughts and crosses
  ;;stolen from wikipedia.
  {[1 1] 2, [1 2] 7, [1 3] 6,
   [2 1] 9, [2 2] 5, [2 3] 1,
   [3 1] 4, [3 2] 3, [3 3] 8})


(defn- pairs
  ([ns] (pairs ns []))
  ([ns acc]
     (if (< (count ns) 2)
       acc
       (pairs ;; recur, adding pairs with the first el't to the accumulator
        (rest ns)
        (let [fst (first ns)
              rst (rest ns)]
          (into acc (for [r rst] [fst r])))))))

(defn- triplets
  ([ns] (triplets ns []))
  ([ns acc]
     (if (< (count ns) 3)
       acc
       (triplets
        (rest ns)
        (into acc
              (let [fst (first ns)
                    rst (rest ns)
                    prs (pairs rst)]
                (for [p prs]
                  (apply conj [fst] p))))))))

(defn is-winner?
  "Given a seq of [x y] vectors, check if they contain a winning set
  of naughts and crosses positions."
  [xys]
  (println "seeking winner in " xys)
  (println (triplets (map magic-square xys)))
  (some
   #(= 15 (apply + %))
   (triplets
    (map magic-square xys))))
