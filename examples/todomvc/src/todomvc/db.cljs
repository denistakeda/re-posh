(ns todomvc.db
  (:require [datascript.core :as datascript]
            [re-posh.core :as re-posh]))

(def initial-db
  [{:db/id                        -1
    :app/type                     :type/create-todo-form
    :create-todo-form/title        ""
    :create-todo-form/description  ""}
   {:db/id            -2
    :app/type         :type/task
    :task/title       "Learn Clojure a little bit"
    :task/description "Just learn it"
    :task/done?       false}
   {:db/id            -3
    :app/type         :type/task
    :task/title       "Have a coffe"
    :task/description "Just relax"
    :task/done?       false}])

(def conn (datascript/create-conn))
(re-posh/connect! conn)
