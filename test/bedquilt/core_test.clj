(ns bedquilt.core-test
  (:require [clojure.test :refer :all]
            [bedquilt.core :as bq]
            [bedquilt.test-utils :refer [get-test-connection reset-db!]]))

(def spec (get-test-connection))

(deftest basics
  (testing "connection"
    (let [conn (get-test-connection)]
      (is (not (nil? conn))))))

(deftest list-collections

  (testing "with no collections"
    (let [_ (reset-db! spec)
          collections (bq/list-collections spec)]
      (is (empty? collections))))

  (testing "after creating two collections"
    (do
      (reset-db! spec)
      (is (= true (bq/create-collection spec "coll_one")))
      (is (= true (bq/create-collection spec "coll_two")))
      (is (= '("coll_one" "coll_two")
             (bq/list-collections spec))))))
