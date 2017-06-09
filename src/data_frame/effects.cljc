(ns data-frame.effects
  (:require
    [re-frame.core :as r]
    [data-frame.db :refer [store]]
    [posh.reagent  :as p]))

(r/reg-fx
  :transact
  (fn [datoms]
    (p/transact! @store datoms)))
