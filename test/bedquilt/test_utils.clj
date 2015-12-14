(ns bedquilt.test-utils
  (:require  [clojure.test :as t]
             [bedquilt.core :as bq]))

(def test-config
  {:dbname "bedquilt_test"})

(defn get-test-connection []
  (bq/build-db-spec test-config))
