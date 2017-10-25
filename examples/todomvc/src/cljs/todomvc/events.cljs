(ns todomvc.events
  (:require [re-frame.core :as re-frame]
            [re-posh.core :as re-posh]
            [todomvc.db :as db]))

(re-posh/reg-event-ds
 :initialize-db
 (fn [_ _]
   db/initial-db))

(re-posh/reg-event-ds
 :task/set-status
 (fn [_ [_ id status]]
   [[:db/add id :task/done? status]]))

(re-posh/reg-event-ds
 :create-todo-form/set-title
 (fn [_ [_ id value]]
   [[:db/add id :create-todo-form/title value]]))
