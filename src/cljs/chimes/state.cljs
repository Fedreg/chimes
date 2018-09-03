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
   :voices        2
   :intervals     [3]
   :page-height   1000
   :page-width    1000
   :mode          :aeolian})

(defonce state
  (reagent/atom init-state))


