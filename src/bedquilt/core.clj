(ns bedquilt.core
  (:require [clojure.java.jdbc :as j]
            [cheshire.core :as json]))



(defn build-db-spec [spec]
  (assoc spec
         :subprotocol "postgresql"
         :classname "org.postgresql.Driver"))

(defn- query [spec query-vec]
  (j/query spec query-vec))

(defn list-collections [spec]
  (map :bq_result
       (query spec ["select bq_list_collections() as bq_result;"])))

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
