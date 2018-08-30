(ns chimes.audio
  (:require
   [chimes.state :as state]))

(def modes
  {:ionian     {:1 0 :2 2 :3 4 :4 5 :5 7 :6 9 :7 11 :8 12}
   :dorian     {:1 0 :2 2 :3 3 :4 5 :5 7 :6 9 :7 10 :8 12}
   :phrygian   {:1 0 :2 1 :3 3 :4 5 :5 7 :6 8 :7 10 :8 12}
   :lydian     {:1 0 :2 2 :3 4 :4 6 :5 7 :6 9 :7 11 :8 12}
   :mixolydian {:1 0 :2 2 :3 3 :4 5 :5 7 :6 8 :7 11 :8 12}
   :aeolian    {:1 0 :2 2 :3 3 :4 5 :5 7 :6 8 :7 10 :8 12}})

(defn hs
  "Determines how many half-steps in each number by mode"
  [n]
  (let [index (-> n str keyword)
        mode  (:mode @state/state)]
    (get-in modes [mode index])))

(defn hz
  "Determines frequency in hz from half-steps above given frequency"
  [n]
  (if (= 0 n)
    0
    (* 13.5 (Math/pow 1.059463 (hs n)))))

(defn build-note
  "Prepares note map to be sent to oscillators"
  ([freq octave duration]
  {:frequency freq
   :octave    octave
   :duration  duration})
  ([freq octave]
   (build-note freq octave 1))
  ([freq]
   (build-note freq 1 0.125)))

(defn get-base-freq
  "Divides a frequency down until it is under 25hz and returns number of divisions it took"
  [freq divides]
  (if (< freq 25)
    [(float freq) divides]
    (recur (/ freq 2) (inc divides))))

(defn pitch-adjust
  "Like auto tune; Normalizes pitch. Based on a A = 432 HZ scale."
  [freq]
  (if (:pitch-adjust? @state/state)
    (let [base     (get-base-freq freq 0)
          pitch    (first base)
          divs     (last  base)
          hzs      (map hz (range 1 10))
          p1       (nth hzs 0)
          p2       (nth hzs 1)
          p3       (nth hzs 2)
          p4       (nth hzs 3)
          p5       (nth hzs 4)
          p6       (nth hzs 5)
          p7       (nth hzs 6)
          p8       (nth hzs 7)
          adj-freq (cond
                     (<    (or pitch freq) p1) p1
                     (< p1 (or pitch freq) p2) p2
                     (< p2 (or pitch freq) p3) p3
                     (< p3 (or pitch freq) p4) p4
                     (< p4 (or pitch freq) p5) p5 
                     (< p5 (or pitch freq) p6) p6 
                     (< p6 (or pitch freq) p7) p7
                     (< p7 (or pitch freq))    p8)]
      (* adj-freq ((.-pow js/Math) 2 divs)))
    freq))

(def ctx
  "Initial music context constructor"
  (let [constructor (or js/window.AudioContext
                        js/window.webkitAudioContext)]
    (constructor.)))

(defn play-note
  "Creates a synthesizer that connects web audio parts and generates frequency"
  [freq octave sustain]
  (let [osc     (.createOscillator ctx)
        vol     (.createGain ctx)
        wave    (name (:wave @state/state))]
    (.connect osc vol)
    (.connect vol (.-destination ctx))

    (set! (.-valoe (.-gain vol)) 0)
    (.setTargetAtTime (.-gain vol) 0.25 (.-currentTime ctx) 0.05)
    (.setTargetAtTime (.-gain vol) 0.00 (+ (.-currentTime ctx) sustain) 0.25)

    (set! (.-value  (.-frequency osc)) (* (pitch-adjust freq) octave))
    (set! (.-type osc) wave)

    (.start osc (.-currentTime ctx))
    (.stop osc (+ (.-currentTime ctx) sustain 1.25))))
