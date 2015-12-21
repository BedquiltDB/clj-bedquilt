(ns bedquilt.core-test
  (:require [clojure.test :refer :all]
            [bedquilt.core :as bq]
            [bedquilt.test-utils :refer [get-test-connection reset-db!]]))

(def spec (get-test-connection))

(deftest connecting
  (testing "connection"
    (let [conn (get-test-connection)]
      (is (not (nil? conn))))))

(deftest collection-basics

  (testing "with no collections"
    (let [_ (reset-db! spec)
          collections (bq/list-collections spec)]
      (is (empty? collections))))

  (testing "after creating two collections"
    (do
      (reset-db! spec)
      (is (= true (bq/create-collection spec "coll_one")))
      (is (= true (bq/create-collection spec "coll_two")))
      (is (= (set '("coll_one" "coll_two"))
             (set (bq/list-collections spec))))))

  (testing "create two collections, delete one"
    (do
      (reset-db! spec)
      (is (= true (bq/create-collection spec "coll_one")))
      (is (= true (bq/create-collection spec "coll_two")))
      (is (= true (bq/delete-collection spec "coll_one")))
      (is (= '("coll_two")
             (bq/list-collections spec))))))

(deftest constraints

  (testing "with no constriants"
    (do
      (reset-db! spec)
      (is (= '()
             (bq/list-constraints spec "one")))))

  (testing "add constraint, then remove"
    (do
      (reset-db! spec)
      (is (= true (bq/add-constraints spec "one" {"name" {"$required" true}})))
      (is (= '("name:required")
             (bq/list-constraints spec "one")))
      (is (= true (bq/remove-constraints spec "one" {"name" {"$required" true}})))
      (is (= '()
             (bq/list-constraints spec "one"))))))
