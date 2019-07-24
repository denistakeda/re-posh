(ns todomvc.subs
  (:require
   [re-posh.core :as re-posh]))

(re-posh/reg-sub
 ::create-todo-form-id
 (fn [_ _]
   {:type :query
    :query '[:find ?id .
             :where [?id :app/type :type/create-todo-form]]}))

(re-posh/reg-sub
 ::create-todo-form
 :<- [::create-todo-form-id]
 (fn [id _]
   {:type    :pull
    :pattern '[:db/id :create-todo-form/title]
    :id      id}))

(re-posh/reg-sub
 ::task-ids
 (fn [_ _]
   {:type :query
    :query '[:find  [?tid ...]
             :where [?tid :app/type :type/task]]}))

(re-posh/reg-sub
 ::task
 (fn [_ [_ id]]
   {:type    :pull
    :pattern '[:db/id :task/done? :task/title]
    :id      id}))
