(ns nac.components
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan close!]]
            [nac.game :as game]))

(enable-console-print!)

;; Individual Cells===============================================

(defn class-str [x y contains]
  (str "cell pos" (str x) (str y) " " ({:x "x", :o "o", nil ""} contains)))

(defn render-cell [cell owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [cells]}]
      (let [{:keys [x y contains]} cell]
        (dom/button
         #js {:className (class-str x y contains)
              :onClick (fn [e] (put! cells {:x x :y y}))}
         ({:x "X", :o "O", nil " "} contains))))))


;; Individual NaC board=========================================

(defn try-click [board click]
  (let [{:keys [to-play cells] :as board} board
        {:keys [x y] :as click} click]
    (let [clicked-cell (get cells [x y])]
      (if (and
           (nil? (:contains clicked-cell))
           (not (game/exists-winner? (:cells board)))) 
        (game/play-in-space board to-play [x y])
        board))))

(defn render-board [board owner]
  (reify
    om/IInitState
    (init-state [_]
      {:cells (chan)})
    om/IWillMount
    (will-mount [_]
      (let [cell-chan (om/get-state owner :cells)]
        (go-loop []
                 (let [click (<! cell-chan)]
                   (om/transact! board (fn [board] (try-click board click))))
                 (recur))))

    om/IRenderState
    (render-state [this {:keys [cells]}]
      (dom/div nil
               (dom/p nil
                      (str "To play: "  (:to-play board)))
               (dom/p nil
                      (str "Winner: " (:winner board)))
               (apply dom/div nil
                      (om/build-all
                       render-cell
                       (vals (:cells board))
                       {:init-state {:cells cells}}))))))
