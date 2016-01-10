(defproject bedquilt "2.0.0"
  :description "Clojure driver for BedquiltDB"
  :url "http://github.com/BedquiltDB/clj-bedquilt"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [cheshire "5.5.0"]
                 [org.postgresql/postgresql "9.4-1206-jdbc42"]]
  :profiles {:test {}}
  :plugins [[lein-codox "0.9.1"]]
  :codox {:namespaces [bedquilt.core]
          :output-path "html-docs"})
