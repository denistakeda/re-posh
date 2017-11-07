(ns todomvc.views
  (:require [re-frame.core :as re-frame]))

(defn create-task-panel []
  (let [form-id (re-frame/subscribe [:create-todo-form/id])
        form (re-frame/subscribe [:create-todo-form @form-id])]
    (fn []
      [:div {:class-name "create-task-panel"}
       [:input {:type "text"
                :value (:create-todo-form/title @form)
                :on-change #(re-frame/dispatch [:create-todo-form/set-title @form-id (-> % .-target .-value)])}]
       [:button {:class-name "create-task-button"
                 :on-click   #(re-frame/dispatch [:create-todo-form/create-todo @form-id (:create-todo-form/title @form)])} "Create"]])))

(defn task-list-item [id]
  (let [task (re-frame/subscribe [:task id])]
    (fn []
      [:div {:class-name "task-list-item"}
       [:input {:type "checkbox"
                :checked (:task/done? @task)
                :on-change #(re-frame/dispatch [:task/set-status (:db/id @task) (not (:task/done? @task))])}]
       [:span (:task/title @task)]])))

(defn task-list []
  (let [task-ids (re-frame/subscribe [:task-ids])]
    (fn []
      [:div {:class-name "task-list"}
       (for [task-id @task-ids]
         ^{:key task-id} [task-list-item task-id])])))

(defn main-panel []
  [:div {:class-name "main-panel"}
   [:h1 "TodoMVC"]
   [create-task-panel]
   [task-list]])
