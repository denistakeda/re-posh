(ns todomvc.subs
  (:require
   [re-posh.core :as re-posh]))

(re-posh/reg-sub
 :create-todo-form/id
 (fn [_ _]
   {:type :query
    :query '[:find ?id .
             :where [?id :app/type :type/create-todo-form]]}))

(re-posh/reg-pull-sub
:create-todo-form
'[*])

(re-posh/reg-query-sub
:task-ids
'[ :find  [?tid ...]
  :where [?tid :app/type :type/task] ])

(re-posh/reg-pull-sub
:task
'[*])
