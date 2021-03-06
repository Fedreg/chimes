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
  (swap! state/state update-in [:pixels] conj pixel))

(defn set-page-dimensions
  "Sets a page's height and width into the state"
  [wd ht]
  (swap! state/state assoc :page-height ht :page-width wd))

(defn add-interval
  "Conjes interval into state."
  [interval]
  (swap! state/state update-in [:intervals] conj interval))
