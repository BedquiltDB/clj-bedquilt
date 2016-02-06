(ns bedquilt.core
  (:require [clojure.java.jdbc :as j]
            [cheshire.core :as json]
            [bedquilt.utils :as utils])
  (:refer-clojure :exclude [find count remove distinct]))

(defn make-db-spec
  "Build a connection spec, same as a clojure.java.jdbc spec.
  This function will add the appropriate PostgreSQL bits to the supplied
  spec map.
  See http://clojure.github.io/java.jdbc/#clojure.java.jdbc/get-connection.
  Example:
    (def spec (bq/make-db-spec {:subname \"//localhost/some_db\"
                                :user \"a_username\"
                                :password \"a_bad_password\"}))"
  [spec]
  (assoc spec
         :subprotocol "postgresql"
         :classname "org.postgresql.Driver"))

(defmacro with-connection
  "Evaluates body expressions in the context of a single db connection.
  Example:
    (bq/with-connection [conn spec]
      (bq/insert conn \"things\" {:a 1})
      (bq/insert conn \"things\" {:a 2}))"
  [binding & body]
  `(j/with-db-connection ~binding ~@body))

(defn- query [spec query-vec]
  (j/query spec query-vec))

(defn list-collections
  "Get a list of collections on the server.
  Returns a sequence of strings"
  [spec]
  (map :bq_result
       (query spec ["select bq_list_collections() as bq_result;"])))

;; Collection Ops
(defn create-collection
  "Create a collection if it does not already exist.
  Returns boolean indicating whether the collection was created by this command."
  [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_create_collection(?) as bq_result"
                     collection-name]))))

(defn delete-collection
  "Delete a collection if it exists.
  Returns boolean indicating whether the collection was deleted by this command."
  [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_delete_collection(?) as bq_result"
                     collection-name]))))

(defn collection-exists?
  "Check if a collection exists currently.
  Returns boolean."
  [spec collection-name]
  (first
   (map :bq_result
        (query spec ["select bq_collection_exists(?) as bq_result"
                     collection-name]))))

;; Constraints
(defn add-constraints
  "Add a set of constraints to the collection.
  Returns true if any constraints were added by this command"
  [spec collection-name constraints]
  (first
   (map :bq_result
        (query spec ["select bq_add_constraints(?, ?::json) as bq_result"
                     collection-name
                     (json/encode constraints)]))))

(defn remove-constraints
  "Remove a set of constraints from the collection.
  Returns true if any constraints were removed by this command"
  [spec collection-name constraints]
  (first
   (map :bq_result
        (query spec ["select bq_remove_constraints(?, ?::json) as bq_result"
                     collection-name
                     (json/encode constraints)]))))

(defn list-constraints
  "Get a list of constraints on this collection."
  [spec collection-name]
   (map :bq_result
        (query spec ["select bq_list_constraints(?) as bq_result"
                     collection-name])))

;; TODO: various arities
;; Query Ops
(defn find
  "Find documents from the collection matching a given query."
  ([spec collection-name]
   (find spec collection-name {} {}))
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

(defn find-one
  "Find a single document from the collection matching a given query.
  Returns a map, or nil if not found."
  [spec collection-name query-doc]
  (first
   (map :bq_result
       (query spec ["select bq_find_one(?, ?::json) as bq_result"
                    collection-name
                    (json/encode query-doc)]))))

(defn find-one-by-id
  "Find a single document which has a _id equal to the supplied doc-id.
  Returns a map, or nil if not found."
  [spec collection-name doc-id]
  (first
   (map :bq_result
        (query spec ["select bq_find_one_by_id(?, ?) as bq_result"
                     collection-name
                     doc-id]))))

(defn find-many-by-ids
  "Find documents which have _id in the supplied sequence of doc-ids.
  Returns a potentially empty sequence of docs."
  [spec collection-name doc-ids]
  (map :bq_result
       (query spec ["select bq_find_many_by_ids(?, ?::jsonb) as bq_result"
                    collection-name
                    (json/encode doc-ids)])))

(defn count
  "Get a count of documents in the collection."
  ([spec collection-name]
   (count spec collection-name {}))
  ([spec collection-name query-doc]
   (first
    (map :bq_result
         (query spec ["select bq_count(?, ?::json) as bq_result"
                      collection-name
                      (json/encode query-doc)])))))

(defn distinct
  "Get a sequence of unique values at the given dotted-path.
  Example: (bq/distinct db \"people\" \"address.city\")"
  [spec collection-name dotted-path]
  (map :bq_result
       (query spec ["select bq_distinct(?, ?) as bq_result"
                    collection-name
                    dotted-path])))

;; Write Ops
(defn insert
  "Insert a document (map) into the collection"
  [spec collection-name doc]
  (first
   (map :bq_result
        (query spec ["select bq_insert(?, ?::json) as bq_result"
                     collection-name
                     (json/encode doc)]))))

(defn save
  "Save a document (map) to the collection, overwriting any existing
  doc with the same _id value."
  [spec collection-name doc]
  (first
   (map :bq_result
        (query spec ["select bq_save(?, ?::json) as bq_result"
                     collection-name
                     (json/encode doc)]))))

(defn remove
  "Remove documunts matching the supplied query-doc."
  [spec collection-name query-doc]
  (first
   (map :bq_result
        (query spec ["select bq_remove(?, ?::json) as bq_result"
                     collection-name
                     (json/encode query-doc)]))))

(defn remove-one
  "Remove a single document matching the supplied query-doc."
  [spec collection-name query-doc]
  (first
   (map :bq_result
        (query spec ["select bq_remove_one(?, ?::json) as bq_result"
                     collection-name
                     (json/encode query-doc)]))))

(defn remove-one-by-id
  "Remove a single document which has a _id value matching the supplied doc-id."
  [spec collection-name doc-id]
  (first
   (map :bq_result
        (query spec ["select bq_remove_one_by_id(?, ?) as bq_result"
                     collection-name
                     doc-id]))))
