(ns todomvc.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [re-posh.core :as re-posh]))

(re-posh/reg-query-sub
 :create-todo-form/id
 '[ :find ?id .
    :where [?id :app/type :type/create-todo-form]])

(re-posh/reg-pull-sub
 :create-todo-form
 '[*])

(re-posh/reg-query-sub
 :task-ids
 '[ :find  [?tid ...]
    :where [?tid :app/type :type/task]])

(re-posh/reg-pull-sub
 :task
 '[*])
