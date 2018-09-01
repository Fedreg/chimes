(ns chimes.core
  (:require
   [reagent.core   :as reagent]
   [clojure.string :as str]
   [chimes.audio   :as audio]
   [chimes.state   :as state]
   [chimes.update  :as update]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Funcs 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn rand-rgb [& hue]
  (let [r (if (= :red   (first hue)) 255 (rand-int 256))
        g (if (= :green (first hue)) 255 (rand-int 256))
        b (if (= :blue  (first hue)) 255 (rand-int 256))]
    (str "rgb(" r "," g "," b ")")))

(defn get-property
  "Parses element styles to return height value as an Int"
  [el prop]
  (let [styles (-> el (.getAttribute "style"))
        split  (str/split styles ";")
        height (-> (filterv #(str/starts-with? % (str " " (name prop))) split)
                    (str/split ": ")
                    last
                    (str/split "px")
                    first
                    js/parseInt)]
    height))

(defn kill-pixel [el pixel]
  (js/setTimeout
   #(.removeChild js/document.body el) (* 1000 (:dur pixel))))


(defn new-pixel [pixel]
  (let [new-div  (.createElement  js/document "div")]
    (set! (-> new-div .-style .-backgroundColor) (rand-rgb :blue))
    (set! (-> new-div .-style .-height) "20px")
    (set! (-> new-div .-style .-width) "20px")
    (set! (-> new-div .-style .-position) "absolute")
    (set! (-> new-div .-style .-top) (str (:y pixel) "px"))
    (set! (-> new-div .-style .-left) (str (:x pixel) "px"))
    (set! (-> new-div .-id) (:ts pixel))
    (set! (-> new-div .-style .-borderRadius) (str (* 0.5 (get-property new-div :width)) "px"))
    (.appendChild js/document.body new-div)
    (js/setTimeout 
     #(do
        (set! (-> new-div .-style .-transition) (str "all " (:dur pixel) "s ease-in"))
        (set! (-> new-div .-style .-height) "50px")
        (set! (-> new-div .-style .-width) "1600px")
        (set! (-> new-div .-style .-boxShadow) "8px 8px 10px  #000")
        (set! (-> new-div .-style .-borderRadius) (str (* 0.5 (get-property new-div :width)) "px"))
        (set! (-> new-div .-style .-transform)
              (str "translateX(-"
                   (* 0.5 (get-property new-div :width))
                   "px)"))
        (set! (-> new-div .-style .-opacity) 0))
    250)
    (kill-pixel new-div pixel)))

(defn new-pixels []
  (mapv new-pixel (->> @state/state
                       :pixels
                       (take-last (:voices @state/state)))))

(defn note-duration
  "Determines note duration based on pageX position within frame.  First quarter of screen is whole note, next quarter is half note, ...quarter, eigth"
  [y]
  (let [frame   js/window.outerWidth
        half    (/ frame 2)
        quarter (/ half 2)
        dur     (cond
                  (<= y (- half quarter))        4
                  (<= (- half quarter) y half)   2
                  (<= half y (- frame quarter))  1
                  (<= (- frame quarter) y frame) 0.5)]
    dur))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-pixel-per-voice [x y]
  (let [voices   (:voices @state/state)
        duration (note-duration x)
        ts       (.getTime (js/Date.))
        freqs    (case voices
                   1 [y]
                   2 [y (audio/interval y 4 :add)]
                   3 [y (audio/interval y 7 :add) (audio/interval y 12 :add)])]
    (mapv (fn [-y] {:x x :y -y :dur duration :ts ts}) freqs)))

(defn page [state]
  [:div {:style {:position "absolute"
                 :top 0
                 :bottom 0
                 :color "#666"
                 :left 0
                 :right 0
                 :transition "background-color 4.0s ease"
                 :backgroundColor "#222"}
         :id "main-div"
         :on-click #(do
                      (mapv
                       update/add-pixel
                       (create-pixel-per-voice (.-pageX %) (.-pageY %)))
                      (new-pixels)
                      (audio/play-notes))} @state])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (reagent/render [page state/state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
