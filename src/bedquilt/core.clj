(ns bedquilt.core
  (:require [clojure.java.jdbc :as j]))



(defn- build-jdbc-connection [spec]
  (j/get-connection (assoc spec :subprotocol "postgresql")))


(defn get-connection [config]
  (let [host (or (:host config) "127.0.0.1")
        port (or (:port config) "5432")
        db   (or (:db   config) "")
        subname (str "//" host ":" port "/" db)
        spec (-> config
                 (dissoc :host :port :db)
                 (assoc :subname subname))
        jdbc-conn (build-jdbc-connection spec)]
    {:jdbc-conn jdbc-conn}))
