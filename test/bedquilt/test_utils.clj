(ns bedquilt.test-utils
  (:require  [clojure.test :as t]
             [bedquilt.core :as bq]))

(def test-config
  {:db "bedquilt_test"})

(defn get-test-connection! []
  (bq/get-connection! test-config))
