(ns rules-test
  (:require
   [chimes.rules :refer :all]
   [cljs.test    :refer :all :include-macros true]))

(deftest is-interval-test
  (testing "is-interval?"
    (is (= true  (is-interval? perfect 0)))
    (is (= true  (is-interval? perfect 7)))
    (is (= true  (is-interval? perfect 12)))
    (is (= false (is-interval? perfect 3)))
    (is (= false (is-interval? perfect 8)))

    (is (= false (is-interval? perfect-not-unison 0)))

    (is (= true  (is-interval? consonant 3)))
    (is (= true  (is-interval? consonant 4)))
    (is (= true  (is-interval? consonant 8)))
    (is (= true  (is-interval? consonant 9)))
    (is (= false (is-interval? consonant 0)))

    (is (= true  (is-interval? perfect 1)))
    (is (= true  (is-interval? perfect 2)))
    (is (= true  (is-interval? perfect 5)))
    (is (= true  (is-interval? perfect 6)))
    (is (= true  (is-interval? perfect 10)))
    (is (= true  (is-interval? perfect 11)))
    (is (= false (is-interval? perfect 12)))
    ))
