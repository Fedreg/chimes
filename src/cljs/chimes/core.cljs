(ns chimes.core
  (:require
   [reagent.core   :as reagent]
   [clojure.string :as str]
   [chimes.audio   :as audio]
   [chimes.state   :as state]
   [chimes.rules   :as rules]
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
   #(.removeChild js/document.body el) (+ 500 (* 1000 (:dur pixel)))))


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
    (set! (-> new-div .-innerHTML) (str (last (:intervals @state/state))))
    (set! (-> new-div .-style .-fontSize) "25px")
    (set! (-> new-div .-style .-color) "white")
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

(defn new-pixels
  "Maps over the last few pixels in state to create new divs.  Channges the Y posiiton of each pixel locally to keep ratios even."
  []
  (let [pixels    (->> @state/state
                       :pixels
                       (take-last (:voices @state/state)))
        intervals (:intervals @state/state)
        ratio     (/ (:page-height @state/state) 50)
        bass      (nth pixels 0)
        tenor     (when (> (count pixels) 1) (nth pixels 1))
        alto      (when (> (count pixels) 2) (nth pixels 2))
        tenor     (when tenor
                    (assoc tenor :y (- (:y bass) (* ratio (last intervals)))))
        alto      (when alto 
                    (assoc alto :y (- (:y bass) (* ratio (last intervals)))))
        pixels    (filter identity [bass tenor alto])]
  (mapv new-pixel [bass tenor alto])))

(defn note-duration
  "Determines note duration based on pageX position within frame.  First quarter of screen is whole note, next quarter is half note, ...quarter, eigth"
  [y]
  (let [frame   (:page-width @state/state) ;js/window.outerWidth
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
  (let [voices    (:voices @state/state)
        duration  (note-duration x)
        intervals (:intervals @state/state)
        interval  (rules/next-interval)
        ts        (.getTime (js/Date.))
        freqs     (case voices
                    1 [y]
                    2 [y (audio/interval y interval :sub)]
                    3 [y (audio/interval y (first intervals) :sub) (audio/interval y (last intervals) :sub)])]
    (mapv (fn [-y] {:x x :y -y :dur duration :ts ts}) freqs)))

(defn page [state]
  (update/set-page-dimensions js/window.outerWidth js/window.outerHeight )
  [:div {:style {:position "absolute"
                 :top 0
                 :bottom 0
                 :color "#777"
                 :fontSize "16px"
                 :left 0
                 :right 0
                 :transition "background-color 4.0s ease"
                 :backgroundColor "#222"}
         :id "main-div"
         :on-click #(do
                      (mapv
                       update/add-pixel
                       (create-pixel-per-voice (.-pageX %) (.-pageY %)))
                      (update/update-index)
                      (new-pixels)
                      (audio/play-notes))} (dissoc @state :pixels)])

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
