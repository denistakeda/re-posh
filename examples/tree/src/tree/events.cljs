(ns tree.events
  (:require
   [re-posh.core :as re-posh]
   [tree.db :as db]))

(re-posh/reg-event-ds
 ::initialize-db
 (fn [_ _]
   db/initial-db))

(re-posh/reg-event-ds
 ::set-node-content
 (fn [_ [_ node-id content]]
   [[:db/add node-id :tree/content content]]))

(re-posh/reg-event-ds
 ::add-child-node
 (fn [_ [_ node-id]]
   [[:db/add node-id :tree/children -1]]))

(re-posh/reg-event-ds
 ::remove-child
 (fn [_ [_ node-id child-id]]
   [[:db/retract node-id :tree/children child-id]]))
