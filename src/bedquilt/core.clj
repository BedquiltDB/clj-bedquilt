(ns bedquilt.core
  (:require [clojure.java.jdbc :as j]
            [cheshire.core :as json]
            [bedquilt.utils :as utils])
  (:refer-clojure :exclude [find count remove distinct]))

(defn build-db-spec [spec]
  (assoc spec
         :subprotocol "postgresql"
         :classname "org.postgresql.Driver"))

(defn- query [spec query-vec]
  (j/query spec query-vec))

(defn list-collections [spec]
  (map :bq_result
       (query spec ["select bq_list_collections() as bq_result;"])))

;; Collection Ops
(defn create-collection [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_create_collection(?) as bq_result"
                     collection-name]))))

(defn delete-collection [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_delete_collection(?) as bq_result"
                     collection-name]))))

(defn collection-exists [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_collection_exists(?) as bq_result"
                     collection-name]))))

(defn collection-exists [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_collection_exists(?) as bq_result"
                     collection-name]))))

;; Constraints
(defn add-constraints [spec collection-name constraints]
  (first
   (map :bq_result
        (query spec ["select bq_add_constraints(?, ?::json) as bq_result"
                     collection-name
                     (json/encode constraints)]))))

(defn remove-constraints [spec collection-name constraints]
  (first
   (map :bq_result
        (query spec ["select bq_remove_constraints(?, ?::json) as bq_result"
                     collection-name
                     (json/encode constraints)]))))

(defn list-constraints [spec collection-name]
   (map :bq_result
        (query spec ["select bq_list_constraints(?) as bq_result"
                     collection-name])))

;; TODO: various arities
;; Query Ops
(defn find
  ([spec collection-name query-doc]
   (find spec collection-name query-doc {}))
  ([spec collection-name query-doc {:keys [skip limit sort] :as options}]
   (map :bq_result
        (query spec
               ["select bq_find(?, ?::json, ?::int, ?::int, ?::json) as bq_result"
                collection-name
                (json/encode query-doc)
                (or skip 0)
                limit
                (if sort (json/encode sort) nil)]))))

(defn find-one [spec collection-name query-doc]
  (first
   (map :bq_result
       (query spec ["select bq_find_one(?, ?::json) as bq_result"
                    collection-name
                    (json/encode query-doc)]))))

(defn find-one-by-id [spec collection-name doc-id]
  (first
   (map :bq_result
        (query spec ["select bq_find_one_by_id(?, ?) as bq_result"
                     collection-name
                     doc-id]))))

(defn count [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_count(?) as bq_result"
                     collection-name]))))

(defn distinct [spec collection-name]
  (map :bq_result
       (query spec ["select bq_distinct(?) as bq_result"
                    collection-name])))

;; Write Ops
(defn insert [spec collection-name doc]
  (first
   (map :bq_result
        (query spec ["select bq_insert(?, ?::json) as bq_result"
                     collection-name
                     (json/encode doc)]))))

(defn save [spec collection-name doc]
  (first
   (map :bq_result
        (query spec ["select bq_save(?, ?::json) as bq_result"
                     collection-name
                     (json/encode doc)]))))

(defn remove [spec collection-name query-doc]
  (first
   (map :bq_result
        (query spec ["select bq_remove(?, ?::json) as bq_result"
                     collection-name
                     (json/encode query-doc)]))))

(defn remove-one [spec collection-name query-doc]
  (first
   (map :bq_result
        (query spec ["select bq_remove_one(?, ?::json) as bq_result"
                     collection-name
                     (json/encode query-doc)]))))

(defn remove-one-by-id [spec collection-name doc-id]
  (first
   (map :bq_result
        (query spec ["select bq_remove_one_by_id(?, ?) as bq_result"
                     collection-name
                     doc-id]))))
