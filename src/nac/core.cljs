(ns nac.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan]]
            [nac.components :refer [nac-cell nac-board blank-board blank-nac-state]]
            [nac.game :refer [is-winner?]]
            [nac.helpers :as h]))

(enable-console-print!)

;;== Initial State =======================
(def app-state (atom (blank-nac-state)))

;;== Helpers ============================
(defn advance-game-state [{:keys [player winner cells] :as app-state}
                          {:keys [x y contains] :as cell}]
  (println "advancing game state")
  (let [new-cells (assoc cells [x y] (assoc cell :contains player))
        new-player ({:x :o, :o :x} player)
        new-winner (h/check-for-winner new-cells)]
    {:player new-player
     :winner new-winner
     :cells new-cells}))

(defn nac-app [app-state owner]
  (reify
    om/IInitState
    (init-state [_]
      {:chan (chan)
       :reset-chan (chan)})
    ;; == This is where the game logic is.============
    ;; Can't put this in the component because the logic will be
    ;; different depending on the context of the board (e.g. reguarl
    ;; nac vs meta-nac).
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

    ; == This displays the game-state and input mechanics.============
    om/IRenderState
    (render-state [_ state]
                  (let [c (om/get-state owner :chan)
                        rc (om/get-state owner :reset-chan)]
                    (dom/div nil
                             (dom/button #js {:onClick (fn [e] (put! rc "reset the board!"))
                                              :className "resetbutton"}
                                         "Reset")
                             (let [winner (:winner app-state)]
                               (dom/p #js {:className "scorebox"}
                                (when winner (str ({:x "X", :o "O"} winner) " wins!"))))
                             (dom/div #js {:className "nac-board"}
                                      (om/build nac-board app-state {:init-state {:chan c}}))
)))))


;;== Attach app  =========================================
(om/root
 nac-app
 app-state
 {:target (. js/document (getElementById "app"))})
