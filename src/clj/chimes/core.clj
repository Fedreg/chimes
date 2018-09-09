(ns chimes.core)

;; TERMS: Using some terms that apply specfically to music theory:
;; CANNTUS: Lowest voice in counterpoint. The main "melody"
;; SPECIES:  Different forms of coutnerpoint are named differennt species.  First species has one note per each note of the cantus; Second has two, etc. 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Facts
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def state
  (atom {:pixels [1]
         :index 4
         :intervals [4 4 4 4]}))

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
  (not (empty? (clojure.set/intersection
                (set
                 (if (vector? interval)
                   interval
                   [interval]))
                type))))

(defn all-true? [stuff]
  (let [res (->> stuff 
                 (filter #(or (false? %)
                              (nil? %))))]
    (empty? res)))

(defn -previous-melodic-note
  "Gets the last melody from the state"
  []
  (let [notes (:pixels @state)
        index (:index  @state)]
    (nth notes (dec index))))

(defn => [a b] (when (all-true? a) b))

(defn rule [rule-name a b]
  {(keyword rule-name) (=> a b)})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rules 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def start
  "Must always begin with a perfect interval."
  (rule :start
        [(= 1 (:index @state))]
        perfect))

(def no-duplicate-perfect-motion
  "Never use two perfect consonances of the same size in a row."
  (rule :duplicate-p
        [(is-interval? perfect (:intervals @state))]
        (set (filter #(not= (first (:intervals @state)) %) perfect))))

(def no-more-than-three-consecutive-imperfect
  "Never use more than 3 imperfect consonances in a row"
  (rule :three-consecutive-c
        [(is-interval? consonant (nth (:intervals @state) (- (:index @state) 1)))
         (is-interval? consonant (nth (:intervals @state) (- (:index @state) 2)))
         (is-interval? consonant (nth (:intervals @state) (- (:index @state) 3)))]
        (clojure.set/union perfect disonant)))

no-more-than-three-consecutive-imperfect

(def species-one
  "Aggregates several rules"
  [start
   no-duplicate-perfect-motion
   
   ]

       )


(comment
(defrule
  :perfect
  [(not (true? false))
   1
   2 #_nil])

:end)

;; similar ; oblique ; conntrary

(defn next-interval []
  (prn "HI")
  (let [rules    (apply merge species-one)
        _ (prn rules)
        possible (->> (vals rules)
                      (filter identity)
                      (clojure.set/intersection)
                      first
                      (apply vector))
        interval (rand-nth possible)]
    ;; (update/add-interval interval)
    interval
    1))
