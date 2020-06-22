(ns tree.subs
  (:require
   [re-posh.core :as re-posh]))

(re-posh/reg-sub
 ::root
 (fn [_ _]
   {:type :query
    :query '[:find ?id .
             :where [?id :tree/root true]]}))

(re-posh/reg-sub
 ::node
 (fn [_ [_ id]]
   {:type    :pull
    :pattern '[*]
    :id      id}))
