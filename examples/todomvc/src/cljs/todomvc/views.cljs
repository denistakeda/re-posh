(ns todomvc.views
  (:require
   [re-posh.core :refer [subscribe dispatch]]
   [todomvc.subs :as subs]
   [todomvc.events :as evt]))

;; Create todo form
(defn render-create-todo-form [form]
  (let [{id    :db/id
         title :create-todo-form/title} form]
    [:div.create-tast-panel
     [:input
      {:type "text"
       :value title
       :on-change #(dispatch [::evt/set-todo-form-title id (-> % .-target .-value)])}]
     [:button.create-task-button
      {:on-click #(dispatch [::evt/create-todo id title])}
      "Create"]]))

(defn create-todo-form []
  (let [form (subscribe [::subs/create-todo-form])]
    (fn []
      (render-create-todo-form @form))))

;; Task list item
(defn render-task-list-item [task]
  (let [{id :db/id
         done? :task/done?
         title :task/title} task]
    [:div.task-list-item
     [:input {:type "checkbox"
              :ckecked (if done? "true" nil)
              :on-change #(dispatch [::evt/set-task-status id (not done?)])}]
     [:span title]]))

(defn task-list-item [id]
  (let [task (subscribe [::subs/task id])]
    (fn []
      (render-task-list-item @task))))

;; Task list
(defn task-list []
  (let [task-ids (subscribe [::subs/task-ids])]
    (fn []
      [:div.task-list
       (for [task-id @task-ids]
         ^{:key task-id} [task-list-item task-id])])))

(defn main-panel []
  [:div.main-panel
   [:h1 "TodoMVC"]
   [create-todo-form]
   [task-list]])
