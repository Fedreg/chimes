(ns chimes.state
  (:require
   [reagent.core   :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; State 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def init-state
  {:pitch-adjust? true 
   :wave          :square
   :voices        1
   :mode          :lydian})

(defonce state
  (reagent/atom init-state))
