(ns chimes.update
  (:require
   [chimes.state :as state]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Update: All changes to state are these fns
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-index
  "increments the index which incresaes everytime a new pixel is added"
  []
  (swap! state/state update-in [:index] inc))

(defn add-pixel
  "Conj's new pixels into state"
  [pixel]
  (update-index)
  (swap! state/state update-in [:pixels] conj pixel))

