(ns todomvc.views
  (:require [re-posh.core :refer [subscribe dispatch]]))

(defn create-task-panel []
  (let [form-id (subscribe [:create-todo-form/id])
        form (subscribe [:create-todo-form @form-id])]
    (fn []
      [:div.create-tast-panel
       [:input {:type "text"
                :value (:create-todo-form/title @form)
                :on-change #(dispatch [:create-todo-form/set-title @form-id (-> % .-target .-value)])}]
       [:button.create-task-button
        {:on-click #(dispatch [:create-todo-form/create-todo @form-id (:create-todo-form/title @form)])} "Create"]])))

(defn task-list-item [id]
  (let [task (subscribe [:task id])]
    (fn []
      [:div.task-list-item
       [:input {:type "checkbox"
                :checked (:task/done? @task)
                :on-change #(dispatch [:task/set-status (:db/id @task) (not (:task/done? @task))])}]
       [:span (:task/title @task)]])))

(defn task-list []
  (let [task-ids (subscribe [:task-ids])]
    (fn []
      [:div.task-list
       (for [task-id @task-ids]
         ^{:key task-id} [task-list-item task-id])])))

(defn main-panel []
  [:div.main-panel
   [:h1 "TodoMVC"]
   [create-task-panel]
   [task-list]])
