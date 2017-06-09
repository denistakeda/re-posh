(ns data-frame.core
  (:require
    [data-frame.db :as db]
    [data-frame.subs :as subs]
    [data-frame.events :as events]
    [data-frame.effects]
    [data-frame.coeffects]))

(def connect! db/connect!)
(def reg-query-sub subs/reg-query-sub)
(def reg-pull-sub subs/reg-pull-sub)
(def reg-event-ds events/reg-event-ds)
