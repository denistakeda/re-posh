(ns tree.db
  (:require [datascript.core :as datascript]
            [re-posh.core :as re-posh]))

(def schema
  {:tree/children {:db/cardinality :db.cardinality/many
                   :db/valueType   :db.type/ref}})

(def initial-db
  [{:db/id 1
    :tree/content "Root Node"
    :tree/children [2 3 4]
    :tree/root true}
   {:db/id 2
    :tree/content "A"}
   {:db/id 3
    :tree/content "B"}
   {:db/id 4
    :tree/content "C"
    :tree/children [5]}
   {:db/id 5
    :tree/content "D"}])

(def conn (datascript/create-conn schema))

(re-posh/connect! conn)
