(ns chimes.rules
  (:require
   [chimes.state :as state]))

;; TERMS: Using some terms that apply specfically to music theory:
;; CANNTUS: Lowest voice in counterpoint. The main "melody"
;; SPECIES:  Different forms of coutnerpoint are named differennt species.  First species has one note per each note of the cantus; Second has two, etc. 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Facts
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def octave 12)
(def unison 0)
(def perfect
  "Perfect intervals"
  #{0 7 12})

(def perfect-not-unison
  "Perfect intervals"
  #{7 12})

(def consonant
  "Consonant intervals"
  #{3 4 8 9})

(def disonant
  "Disonant intervals"
  #{1 2 5 6 10 11})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Funcs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-interval?
  "Determinnes if a given interval is of a type (perfect, consonannt, etc)"
  [type interval]
  (not (empty? (clojure.set/intersection (set [interval]) type))))

(defn all-true? [stuff]
  (let [res (->> stuff 
                 (filter #(or (false? %)
                              (nil? %))))]
    (empty? res)))

(defn -previous-melodic-note
  "Gets the last melody from the state"
  []
  (let [notes (:pixels @state/state)
        index (:index  @state/state)]
    (nth notes (dec index))))

(defn => [a] (when (all-true? a) a))

(defn defrule [rule-name a]
  {(keyword rule-name) (=> a)})

(defn species-one
  "takes in the current bass note and state adn determines interval(s) of the next countervoice"
  [cantus]
  (cond
    (= 1 (:index @state/state))  perfect  ;; Must always begin with a perfect interval.




       )))




(comment
(defrule
  :perfect
  [(not (true? false))
   1
   2 #_nil])

:end)

;; similar ; oblique ; conntrary
