(ns tree.views
  (:require
   [re-posh.core :refer [subscribe dispatch]]
   [tree.subs :as subs]
   [tree.events :as evt]))

(declare node)

(defn main-panel []
  (let [root-id (subscribe [::subs/root])]
    [:div.main-panel
     [:h1 "Tree"]
     [node @root-id nil]]))

(defn node [id parent-id]
  (let [n (subscribe [::subs/node id])
        {:keys [tree/content tree/children]} @n]
    [:div.node
     [:input.content {:value content
                      :on-change #(dispatch [::evt/set-node-content id (-> % .-target .-value)])}]
     (when parent-id
       [:button.remove {:on-click #(dispatch [::evt/remove-child parent-id id])} "-"])
     [:button.add {:on-click #(dispatch [::evt/add-child-node id])} "+"]
     [:div.children
      (for [{child-id :db/id} children]
        ^{:key child-id} [node child-id id])]]))
