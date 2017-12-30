(ns todomvc.events
  (:require [re-frame.core :as re-frame]
            [re-posh.core :as re-posh]
            [todomvc.db :as db]
            [ajax.core :refer [GET]]))

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

(re-posh/reg-event-ds
 :create-todo-form/create-todo
 (fn [_ [_ id value]]
   [[ :db/add id :create-todo-form/title ""]
    { :db/id       -1
      :app/type    :type/task
      :task/title  value
      :task/done?  false}]))

;; Retrieval of entries from the Server
(re-frame/reg-event-fx
  :retrieve
  [(re-frame/inject-cofx :ds)] ;; inject coeffect
  (fn [{:keys [ds]} [_ signal params]] ;; ds here is the DataScript database
    (GET
      "/upload"
      :format :edn
      :handler #(re-frame/dispatch  [:add-entries  %1])
      :error-handler #(js/alert (str "Retrieval of entries failed!")))
    ds))

(re-posh/reg-event-ds
  :add-entries
  (fn  [_ [_ ds]]
    (let [new-db (cljs.reader/read-string ds)]
      new-db)))