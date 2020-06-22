(ns ^:figwheel-hooks tree.core
  (:require
   [re-posh.core :as re-posh]
   [tree.events :as evt]
   [tree.subs]
   [tree.views :as views]
   [reagent.core :as reagent :refer [atom]]))

(defn mount-app-element []
  (reagent/render-component [views/main-panel]
                            (.getElementById js/document "app")))

(defn init []
  (re-posh/dispatch-sync [::evt/initialize-db])
  (mount-app-element))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))

(defonce initialize-block
  (do
    (re-posh/dispatch-sync [::evt/initialize-db])
    (mount-app-element)
    true))
