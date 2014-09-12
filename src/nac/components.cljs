(ns nac.components
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put! chan]]))

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
               "")))))

