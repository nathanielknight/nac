(ns nac.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan]]
;            [nac.repl]
            [nac.components :refer [nac-cell nac-board blank-board]]
            [nac.game :refer [is-winner?]]))

(enable-console-print!)

;;== Initial State =======================
(def app-state (atom  {:player :x, :winner nil, :cells (blank-board 0)}))


;;== Helpers ============================
(defn get-cells-with [cells contains]
  (map first (filter
              (fn [c] (= (:contains (second c))  contains))
              cells)))

(defn check-for-winner [cells]
  (let [xs (get-cells-with cells :x)
        os (get-cells-with cells :o)]
    (print "====================")
    (print "xs: " xs)
    (print "os: " os)
    (cond
     (is-winner? xs) :x
     (is-winner? os) :o
     :else false)))

(defn advance-game-state [{:keys [player winner cells] :as app-state}
                          {:keys [x y contains] :as cell}]
  
  (let [new-cells (assoc cells [x y] (assoc cell :contains player))
        new-player ({:x :o, :o :x} player)
        new-winner (check-for-winner new-cells)]
    {:player new-player
     :winner new-winner
     :cells new-cells}))

(defn nac-app [app-state owner]
  (reify
    om/IInitState
    (init-state [_]
      {:chan (chan)
       :reset-chan (chan)})

    ; == This is where the game logic is.============
    om/IWillMount
    (will-mount [_]
      (let [c (om/get-state owner :chan)
            rc (om/get-state owner :reset-chan)]
        (go-loop []
                 (let [cell (<! c)]
                   (when-not (:winner @app-state)
                     (om/transact! app-state
                                   (fn [app-state]
                                     (advance-game-state app-state @cell)))))
                 (recur))
        
        (go-loop []
                 (let [x (<! rc)]
                   (om/update! app-state {:player :x
                                          :winner false
                                          :cells (blank-board 0)})
                 (recur)))))
    
    om/IRenderState
    (render-state [_ state]
                  (let [c (om/get-state owner :chan)
                        rc (om/get-state owner :reset-chan)]
                    (dom/div nil
                           (dom/button #js {:onClick (fn [e] (put! rc "reset the board!"))}
                                       "Reset")
                           (om/build nac-board app-state {:init-state {:chan c}})
                           (let [winner (:winner app-state)]
                             (dom/p nil (when winner
                                          (str ({:x "X", :o "O"} winner) " wins!")))))))))


;;== Attach app  =========================================
(om/root
 nac-app
 app-state
 {:target (. js/document (getElementById "app"))})
