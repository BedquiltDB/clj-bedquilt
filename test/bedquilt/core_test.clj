(ns bedquilt.core-test
  (:require [clojure.test :refer :all]
            [bedquilt.core :as bq]
            [bedquilt.test-utils :refer [get-test-connection!]]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))


(deftest basics
  (testing "connection"
    (is (not (nil? (get-test-connection!))))))
