(ns todomvc.core
  (:require [reagent.core :as reagent]
            [re-posh.core :as re-posh]
            [todomvc.events :as evt]
            [todomvc.subs]
            [todomvc.views :as views]
            [todomvc.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-posh/dispatch-sync [::evt/initialize-db])
  (dev-setup)
  (mount-root))
