# Introduction to clj-bedquilt

`clj-bedquilt` is the clojure client library for
[BedquiltDB](http://bedquiltdb.github.io).


## Installation

[![Clojars Project](https://img.shields.io/clojars/v/bedquilt.svg)](https://clojars.org/bedquilt)

Add the following to `:dependencies` in `project.clj`:

```
[bedquilt "2.0.0"]
```


## Prerequisites

- Clojure 1.7
- A PostgreSQL server with the `bedquilt` extension installed
(http://bedquiltdb.readthedocs.org).

## Further info about BedquiltDB

See the [BedquiltDB Guide](http://bedquiltdb.readthedocs.org/en/latest/guide).


## Basic usage

Require the `bedquilt.core` namespace:

```clojure

(ns example.core
  (:require [bedquilt.core :as bq]))

```

Make a db spec, with which to connect to the PostgreSQL instance with
`bedquilt` installed:

```clojure

(def spec
  (bq/make-db-spec {:subname  "//localhost/bedquilt_test"
                    :user     "user"
                    :password "password"}))

```

Get a list of collections on the server:

```clojure

(bq/list-collections spec)
;; => '("users" "documents")

```

Create a collection:


```clojure

(bq/create-collection spec "things")
;; => true

```

Delete a collection:


```clojure

(bq/delete-collection spec "things")
;; => true

```

Check if a collection exists:

```clojure

(bq/collection-exists? spec "nope")
;; => false

```

Add constraints to a collection:

```clojure

(bq/add-constraints spec
  "users"
  {"email" {"$required" true
            "$notnull" true
            "$type" "string"}
   "name"  {"$required" true}})
;; => true

```

Remove constraints to a collection:

```clojure

(bq/remove-constraints spec
  "users"
  {"name"  {"$required" true}})
;; => true

```


Find documents in a collection:

```clojure

(bq/find spec "users" {:age 22})
;; => '({:_id "123", :age 22, :name "Jim", ...},  {...}, ...)


(bq/find spec "users" {:address {:city "Glasgow"}}
                      {:sort [{:age 1}], :skip 2, :limit 4})

(bq/find-one spec "users" {:address {:city "Edinburgh"}})
;; => {:_id "242", ...}

(bq/find-one-by-id spec "users" "462")
;; => {:_id "462", ...}

```

Get a count of documents in a collection:

```clojure

(bq/count spec "things")
;; => 42

(bq/count spec "users" {:age 45})
;; => 2040

```


Get a list of distinct values under a given key path in a collection:

```clojure

(bq/distinct spec "users" "address.city")
;; => '("Edinburgh" "London" ...)'

```

Insert a document in a collection:

```clojure

(bq/insert spec "users" {:name "Jill"})
;; => "68e65f4a55246b0e5ce5d075"
;; (returns the _id field of the inserted document)

(bq/save spec "users" {:_id "..." :name "Mike"})
;; => "10a75a73d607a04044023010"

```

Update an existing document:

```clojure

(let [doc (bq/find-one-by-id spec "users" "123")
      new-doc (assoc doc :age 24)]
  (bq/save spec "users" new-doc))
;; => "123"
;; (save either overwrites the document with the same _id, or inserts a new document)

```

Even better, do the same with a single connection:

```clojure

(bq/with-connection [conn spec]
  (let [doc (bq/find-one-by-id conn "users" "123")
        new-doc (assoc doc :age 24)]
    (bq/save conn "users" new-doc)))

```

Remove documents from a collection:

```clojure

(bq/remove spec "users" {:age 22})
;; => 45
;; (count of documents removed)

(bq/remove-one spec "users" {:address {:city "Edinburgh"}})
;; => 1

(bq/remove-one-by-id spec "users" "462")
;; => 1

```
