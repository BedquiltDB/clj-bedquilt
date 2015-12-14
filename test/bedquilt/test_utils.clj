(ns bedquilt.test-utils
  (:require  [clojure.test :as t]
             [bedquilt.core :as bq]))

(def test-config
  {:subname "//localhost/bedquilt_test"})

(defn get-test-connection []
  (bq/build-db-spec test-config))

(defn reset-db! [spec]
  (let [collections (bq/list-collections spec)]
    (doseq [coll collections]
      (bq/delete-collection spec coll))))
