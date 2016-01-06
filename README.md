# bedquilt

A Clojure client for [BedquiltDB](http://bedquiltdb.github.io)

## Usage

```clojure

;; in a repl

(require '[bedquilt.core :as bq]')

(def spec (bq/make-db-spec {:subname "//localhost/bedquilt_test"}))

(bq/create-collection spec "users")

(bq/insert spec "users" {:name "Sarah Lyons"
                         :age  24
                         :city "Edinburgh"})

(bq/find spec "users" {:city "Edinburgh"})
```


## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
