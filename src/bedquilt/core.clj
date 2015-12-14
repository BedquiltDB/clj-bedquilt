(ns bedquilt.core
  (:require [clojure.java.jdbc :as j]))



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
