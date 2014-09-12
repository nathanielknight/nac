(ns nac.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan]]
;            [nac.repl]
            [nac.components :refer [nac-cell]]))

(enable-console-print!)

;; Initial State
(def app-state (atom {:x 1, :y 1, :z 1, :contains nil}))

(defn nac-app [app-state owner]
  (reify
    om/IInitState
    (init-state [_]
      {:chan (chan)})
    om/IWillMount
    (will-mount [_]
      (let [c (om/get-state owner :chan)]
        (go-loop []
                 (let [x (<! c)]
                   (print "jkl")
                   (recur)))))
    
    om/IRenderState
    (render-state [_ state]
      (let [c (om/get-state owner :chan)]
        (om/build nac-cell app-state {:init-state {:chan c}})))))


;; Exec main
(om/root
 nac-app
 app-state
 {:target (. js/document (getElementById "app"))})
