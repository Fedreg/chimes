(ns chimes.state
  (:require
   [reagent.core   :as reagent]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; State 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def init-state
  {:pitch-adjust? true 
   :pixels        []
   :wave          :square
   :index         0
   :voices        3
   :mode          :aeolian})

(defonce state
  (reagent/atom init-state))


