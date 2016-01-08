# bedquilt

A Clojure client for [BedquiltDB](http://bedquiltdb.github.io)

## Usage

```clojure

;; in a repl

(require '[bedquilt.core :as bq])

(def spec (bq/make-db-spec {:subname "//localhost/bedquilt_test"}))

(bq/create-collection spec "users")

(bq/insert spec "users" {:name "Sarah Lyons"
                         :age  24
                         :city "Edinburgh"})

(bq/find spec "users" {:city "Edinburgh"})
```

## Documentation

http://bedquiltdb.github.io/clj-bedquilt-docs/


## License

Copyright © 2016 Shane Kilkelly

Distributed under the MIT license (http://opensource.org/licenses/MIT).
