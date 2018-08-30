(ns chimes.core)


(defn get-four [n]
  (let [half (/ 1000 2)
        -half (/ half 2)]
    (cond 
      (<= n (- half -half))      1
      (<= (- half -half) n half) 2
      (<= half n (- 1000 -half)) 4
      (<= (- 1000 -half) n 1000) 8)))

(get-four 200)
