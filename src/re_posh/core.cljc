(ns re-posh.core
  (:require
    [re-posh.db :as db]
    [re-posh.subs :as subs]
    [re-posh.events :as events]
    [re-posh.effects]
    [re-posh.coeffects]))

(def connect! db/connect!)
(def reg-query-sub subs/reg-query-sub)
(def reg-pull-sub subs/reg-pull-sub)
(def reg-event-ds events/reg-event-ds)
