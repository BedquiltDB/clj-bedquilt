(ns bedquilt.core
  (:require [clojure.java.jdbc :as j]))


(defn get-connection [config]
  (let [connection (j/get-connection (assoc config :subprotocol "postgresql"))]
    {:jdbc-conn connection}))
