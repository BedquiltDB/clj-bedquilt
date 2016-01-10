(ns bedquilt.core-test
  (:require [clojure.test :refer :all]
            [bedquilt.core :as bq]
            [bedquilt.test-utils :refer [get-test-connection reset-db!]]))

(def db (get-test-connection))

(deftest connecting
  (testing "connection"
    (let [conn (get-test-connection)]
      (is (not (nil? conn))))))

(deftest collection-basics

  (testing "with no collections"
    (let [_ (reset-db! db)
          collections (bq/list-collections db)]
      (is (empty? collections))))

  (testing "after creating two collections"
    (do
      (reset-db! db)
      (is (= true (bq/create-collection db "coll_one")))
      (is (= true (bq/collection-exists? db "coll_one")))
      (is (= false (bq/collection-exists? db "coll_two")))
      (is (= true (bq/create-collection db "coll_two")))
      (is (= (set '("coll_one" "coll_two"))
             (set (bq/list-collections db))))))

  (testing "create two collections, delete one"
    (do
      (reset-db! db)
      (is (= true (bq/create-collection db "coll_one")))
      (is (= true (bq/create-collection db "coll_two")))
      (is (= true (bq/delete-collection db "coll_one")))
      (is (= '("coll_two")
             (bq/list-collections db))))))

(deftest constraints

  (testing "with no constriants"
    (do
      (reset-db! db)
      (is (= '()
             (bq/list-constraints db "one")))))

  (testing "add constraint, then remove"
    (do
      (reset-db! db)
      (is (= true (bq/add-constraints db "one" {"name" {"$required" true}})))
      (is (= '("name:required")
             (bq/list-constraints db "one")))
      (is (= true (bq/remove-constraints db "one" {"name" {"$required" true}})))
      (is (= '()
             (bq/list-constraints db "one"))))))

(deftest with-connection

  (testing "with-connection macro"
    (do
      (reset-db! db)
      (is (= 3
             (bq/with-connection [conn db]
               (bq/insert conn "things" {:a 1})
               (bq/insert conn "things" {:a 2})
               (bq/insert conn "things" {:a 3})
               (bq/count conn "things")))))))

(deftest insert-and-find

  (testing "with no docs"
    (do
      (reset-db! db)
      (is (= '()
             (bq/find db "people")))))

  (testing "with three docs"
    (do
      (reset-db! db)
      ;; insert
      (doseq [doc [{:_id "01" :name "Jane" :age 22}
                   {:_id "02" :name "John" :age 19}
                   {:_id "03" :name "Mike" :age 22}]]
        (is (= (:_id doc)
               (bq/insert db "people" doc))))
      (is (= 3)
          (count (bq/find db "people")))
      ;; count
      (is (= 3)
          (bq/count db "people"))
      ;; find
      (is (= #{"Jane" "John" "Mike"}
             (->> (bq/find db "people")
                  (map :name)
                  (set))))
      (is (= #{"01" "03"}
             (->> (bq/find db "people" {:age 22})
                  (map :_id)
                  (set))))
      (is (= '("03" "02")
             (->> (bq/find db "people" {} {:skip 1 :sort [{:age -1}]})
                  (map :_id))))
      ;; find-one
      (is (= "02"
             (:_id (bq/find-one db "people" {:age 19}))))
      (is (= "02"
             (:_id (bq/find-one-by-id db "people" "02"))))
      ;; distinct
      (is (= '(22 19)
             (bq/distinct db "people" "age")))
      ;; save
      (let [doc (bq/find-one-by-id db "people" "01")]
        (is (= "01" (bq/save db "people" (assoc doc :age 44))))
        (is (= 44 (:age (bq/find-one-by-id db "people" "01")))))
      ;; remove
      (do
        (is (= 1 (bq/remove-one-by-id db "people" "01")))
        (is (= #{"02" "03"}
               (->> (bq/find db "people" {})
                    (map :_id)
                    (set))))
        (is (= 2
               (bq/remove db "people" {})))
        (is (= 0 (bq/count db "people")))))))
