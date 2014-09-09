(ns nac.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan]]
;            [nac.repl]
            [nac.components :refer [render-board]]
            [nac.game :refer [new-game]]))

(enable-console-print!)

;; Initial State
(def app-state (atom (new-game)))

(defn nac-app [app-state owner]
  (reify
    om/IInitState
    (init-state [_]
      {:reset-chan (chan)})

    om/IWillMount
    (will-mount [_]
      (let [reset-chan (om/get-state owner :reset-chan)]
        (go-loop []
                 (let [click (<! reset-chan)]
                   (om/update! app-state (new-game))
                   (recur)))))
    om/IRenderState
    (render-state [this state]
      (dom/div nil
              (dom/button
               #js {:onClick
                    (fn [e] (put! (:reset-chan state) :click))}
               "New Game")
              (om/build render-board app-state)))))


;; Exec main
(om/root
 nac-app
 app-state
 {:target (. js/document (getElementById "app"))})
