(ns nac.components
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan pipe]]))

;; This file describes the components that make up a meta-nac board

;;== Individual cells
(defn class-str [x y contains]
  (str "pos" x y " cell" ({:x " x", :o " o"} contains)))

(defn nac-cell [cell owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (let [{:keys [x y z contains] :as cell} cell
            c (om/get-state owner :chan)]
              (dom/button
               #js {:className (class-str x y contains)
                    :onClick (fn [e] (if (nil? contains)
                                      (put! c cell)))}
               ({:x "X", :o "O", nil " "} contains))))))

;;== Individual board
(defn blank-board [z]
  (let [xy (for [x [1 2 3]
                 y [1 2 3]]
             [x y])]
    (zipmap xy
            (map (fn [[x y]] {:x x, :y y, :z z, :contains nil})
                 xy))))

(defn nac-board [board owner]
  (reify
    om/IInitState
    (init-state [_]
      {:cell-chan (chan)
       :chan (chan)})
    om/IWillMount
    (will-mount [_]
      (let [c (om/get-state owner :chan)
            cc (om/get-state owner :cell-chan)]
        (pipe c cc)))
    om/IRenderState
    (render-state [_ state]
      (let [c (om/get-state owner :chan)
            cells (vals (:cells board))
            winner (:winner board)]
        (dom/div nil ({:x "X", :o "O", nil " "} winner) )
        (apply dom/div nil
               (om/build-all nac-cell cells {:init-state {:chan c}}))))))

