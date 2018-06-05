(ns re-posh.core
  (:require
    [re-posh.subs :as subs]
    [re-posh.events :as events]
    [re-posh.effects]
    [re-posh.coeffects]
    [re-frame.core :as re-frame]))

(def reg-query-sub subs/reg-query-sub)
(def reg-pull-sub subs/reg-pull-sub)
(def reg-event-ds events/reg-event-ds)

;; Reexport re-frame functions
(def subscribe re-frame/subscribe)
(def dispatch re-frame/dispatch)
(def dispatch-sync re-frame/dispatch-sync)
(def reg-event-fx re-frame/reg-event-fx)
(def inject-cofx re-frame/inject-cofx)
